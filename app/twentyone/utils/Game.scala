package twentyone.utils

import twentyone.utils.deck.{Card, multiDeck}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Game(totalDeck: List[Card], shuffle: Boolean = true) {
  // With 52 cards the deck can't go out of stock with 4 players, using next on deck is safe.

  protected val deck: Iterator[Card] = {
    if (shuffle) Random.shuffle(totalDeck).toIterator
    else totalDeck.toIterator
  }

  protected var _bank: Player = Player("bank").addCard(deck.next())
  def bank: Player = _bank

  protected var _state: Game.State.Value = Game.State.OpenToJoin
  /** Returns the state of the Game */
  def state: Game.State.Value = _state

  protected val players: ArrayBuffer[Player] = ArrayBuffer()

  /**
    * Adds a player to the game
    * @throws IllegalArgumentException If action is not allowed
    * @param name Player name
    * @return PlayerIdx
    */
  def addPlayer(name: String): Int = {
    require(state == Game.State.OpenToJoin, "Game of not open to join anymore")
    require(players.size < 3, "Game is already full")
    players += Player(name).addCard(deck.next())
    if (players.size == 3) _state = Game.State.Betting
    players.size - 1
  }

  /**
    * Starts the game and blocking players to join the game
    * @throws IllegalArgumentException If action is not allowed
    */
  def start(): Unit = {
    require(players.nonEmpty, "Can't start a game without players")
    require(state == Game.State.OpenToJoin, "Game is already started")
    _state = Game.State.Betting
  }

  /**
    * Add a bet for a player
    * Game goes to next stage when all players did place a bet
    *
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Id of the player
    * @param bet Bet should be above 0
    */
  def playerAddBet(playerId: Int, bet: Int): Unit = {
    players.lift(playerId) match {
      case Some(player) =>
        require(state == Game.State.Betting, "Not possible to bet right now")
        require(player.bet.isEmpty, "Player did already made a bet")
        require(bet > 0, "Bet must be higher then 0")
        players(playerId) = player.setBet(bet)
        if (players.forall(_.bet.isDefined)) {
          players.zipWithIndex.foreach { case (p, idx) => players(idx) = p.addCard(deck.next())}
          _bank = bank.addCard(deck.next())
          _state = Game.State.Playing
        }
      case _ => throw new IllegalArgumentException(s"Player id '$playerId' does not exists in this game")
      }
  }

  /**
    * Is possible player get a new card
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Player Id
    */
  def playerHit(playerId: Int): Unit = {
    playerTurn match {
      case Some(id) if id == playerId =>
        require(players(playerId).status == Player.Status.Playing, "First hand is not playing")
        players(playerId) = players(playerId).addCard(deck.next())
      case _ => throw new IllegalArgumentException("Not players turn")
    }
  }

  /**
    * If possible player hold it's hand
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Player Id
    */
  def playerHold(playerId: Int): Unit = {
    playerTurn match {
      case Some(id) if id == playerId =>
        require(players(playerId).status == Player.Status.Playing, "First hand is not playing")
        players(playerId) = players(playerId).copy(status = Player.Status.Hold)
      case _ => throw new IllegalArgumentException("Not players turn")
    }
  }

  /**
    * If possible player get card for second hand
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Player Id
    */
  def playerSplitHit(playerId: Int): Unit = {
    playerTurn match {
      case Some(id) if id == playerId =>
        require(players(playerId).splitStatus.contains(Player.Status.Playing), "Second hand is not playing")
        players(playerId) = players(playerId).addSplitCard(deck.next())
      case _ => throw new IllegalArgumentException("Not players turn")
    }
  }

  /**
    * if possible player hold second hand
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Player Id
    */
  def playerSplitHold(playerId: Int): Unit = {
    playerTurn match {
      case Some(id) if id == playerId =>
        require(players(playerId).splitStatus.contains(Player.Status.Playing), "Second hand is not playing")
        players(playerId) = players(playerId).copy(splitStatus = Some(Player.Status.Hold))
      case _ => throw new IllegalArgumentException("Not players turn")
    }
  }

  /**
    * If possible player will split hand
    * @throws IllegalArgumentException If action is not allowed
    * @param playerId Player Id
    */
  def playerSplit(playerId: Int): Unit = {
    playerTurn match {
      case Some(id) if id == playerId =>
        players(playerId) = players(playerId)
          .split
          .addCard(deck.next())
          .addSplitCard(deck.next())
      case _ => throw new IllegalArgumentException("Not players turn")
    }
  }

  /** Returns Id of player who's turn is it */
  def playerTurn: Option[Int] = {
    val playing = players.zipWithIndex.find{case (player, _)  => player.status == Player.Status.Playing || player.splitStatus.contains(Player.Status.Playing)}
    val waiting = players.zipWithIndex.find{case (player, _)  => player.status == Player.Status.Waiting}

    (state, playing, waiting) match {
      case (Game.State.Playing, Some((_, id)), _) => Some(id)
      case (Game.State.Playing, _, Some((player, id))) =>
        players(id) = player.copy(status = Player.Status.Playing)
        Some(id)
      case (Game.State.Playing, _, _) =>
        bankPlays()
        _state = Game.State.Done
        None
      case _ => None
    }
  }

  /** Bank will hit till total value is above 17 */
  protected def bankPlays(): Unit = {
    require(players.forall(x => x.status == Player.Status.Bust || x.status == Player.Status.Hold))
    val total = bank.totalPoints
    if (total < 17) {
      _bank = bank.addCard(deck.next())
      bankPlays()
    } else {
      if (total > 21) _bank = bank.copy(status = Player.Status.Bust)
      else _bank = bank.copy(status = Player.Status.Hold)
      _state = Game.State.Done
    }
  }

  /**
    * Returns which players did win
    *
    * @throws IllegalArgumentException If action is not allowed
    * @return Map of results
    */
  def result: Map[Int, Game.Result.Value] = {
    require(playerTurn.isEmpty, s"A player is still playing: $playerTurn")
    require(state == Game.State.Done, "Game is not yet done")
    val bankTotal = bank.totalPoints
    players.zipWithIndex.map { case (player, id) =>
      (bank.status, player.status, player.splitStatus) match {
        case (Player.Status.Bust, Player.Status.Hold, _) => id -> Game.Result.Win
        case (Player.Status.Bust, _, Some(Player.Status.Hold)) => id -> Game.Result.Win
        case (_, Player.Status.Hold, _) if player.totalPoints > bank.totalPoints => id -> Game.Result.Win
        case (_, _, Some(Player.Status.Hold)) if player.totalSplitPoints.getOrElse(0) > bank.totalPoints => id -> Game.Result.Win
        case _ => id -> Game.Result.Lose
      }
    }.toMap
  }

  /**
    * Return player
    * @throws IllegalArgumentException If player id does not eixts
    * @param id Id of player
    * @return player
    */
  def getPlayer(id: Int): Player = {
    players.lift(id)
      .getOrElse(throw new IllegalArgumentException(s"Player Id '$id' does not exists"))
  }

  /** Returns the number of players */
  def numberPlayers: Int = players.size

}

object Game {

  def apply(decks: Int): Game = apply(decks, shuffle = true)
  def apply(cards: List[Card]): Game = apply(cards, shuffle = true)
  def apply(decks: Int, shuffle: Boolean): Game = new Game(multiDeck(decks), shuffle)
  def apply(cards: List[Card], shuffle: Boolean): Game = new Game(cards, shuffle)

  object State extends Enumeration {
    val OpenToJoin, Betting, Playing, Done = Value
  }

  object Result extends Enumeration {
    val Win, Lose = Value
  }

}
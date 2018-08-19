package twentyone.utils

import twentyone.utils.deck.{Card, CardNumber}

/**
  * This class will store the cards and status of a player
  *
  * @param name Name of the player
  * @param bet Bet of the player
  * @param cards Current cards of the player
  * @param status Current status of the player
  * @param splitCards Cuurent cards in split hand
  * @param splitStatus Status of the split hand
  */
case class Player(name: String,
                  bet: Option[Int] = None,
                  cards: List[Card] = Nil,
                  status: Player.Status.Value = Player.Status.Waiting,
                  splitCards: Option[List[Card]] = None,
                  splitStatus: Option[Player.Status.Value] = None) {

  /** Return total points of hand. A ace is automatically converted */
  def totalPoints: Int = {
    Player.totalPoints(cards)
  }

  /** Return total points of hand. A ace is automatically converted */
  def totalSplitPoints: Option[Int] = {
    splitCards.map(Player.totalPoints)
  }

  /** Adds a card to the players hand */
  def addCard(card: Card): Player = {
    val p = this.copy(cards = card :: cards)
    if (p.totalPoints > 21) p.copy(status = Player.Status.Bust)
    else p
  }

  /**
    * Adds a card to the players split hand
    *
    * @throws IllegalArgumentException
    * @param card card to add
    * @return new Player class
    */
  def addSplitCard(card: Card): Player = {
    splitCards match {
      case Some(s) =>
        val p = this.copy(splitCards = Some(card :: s))
        if (p.totalSplitPoints.getOrElse(0) > 21) p.copy(splitStatus = Some(Player.Status.Bust))
        else p
      case _ => throw new IllegalArgumentException("Player does not have a split hand")
    }
  }

  /** If true player can call split */
  def canSplit: Boolean = {
    bet.isDefined && cards.size == 2 && splitCards.isEmpty && cards.map(_.number).distinct.size == 1 && status == Player.Status.Playing
  }

  /**
    * Split hand into 2 deck if possible
    *
    * @throws IllegalArgumentException
    * @return New player
    */
  def split: Player = {
    require(canSplit, "Split is not allowed")
    Player(name, bet, List(cards(0)), Player.Status.Playing, Some(List(cards(1))), Some(Player.Status.Playing))
  }

  /** Setting bet */
  def setBet(newBet: Int): Player = {
    require(bet.isEmpty, "Bet is already set")
    this.copy(bet = Some(newBet))
  }
}

object Player {
  def totalPoints(cards: List[Card]): Int = {
    val minimalPoints = cards.map(_.points).sum

    // Ace can only be once 1 or 11, 2 x 11 == 22, so bust
    if (cards.exists(_.number == CardNumber.Ace) && minimalPoints <= 11)
      minimalPoints + 10
    else minimalPoints
  }

  object Status extends Enumeration {
    val Waiting, Playing, Hold, Bust = Value
  }
}
package twentyone.utils

import org.scalatest.Matchers
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.{DataProvider, Test}
import twentyone.utils.deck.{Card, CardNumber, CardSuit}

class GameTest extends TestNGSuite with Matchers {

  @Test
  def testDefaultSinglePlayer(): Unit = {
    // Init game without player
    val game = Game(testDeck1, shuffle = false)
    game.state shouldBe Game.State.OpenToJoin
    game.bank.cards shouldBe List(Card(CardSuit.Diamond, CardNumber.n9))
    game.numberPlayers shouldBe 0

    // Adding a player
    game.addPlayer("player1")
    game.getPlayer(0).name shouldBe "player1"
    game.getPlayer(0).cards shouldBe List(Card(CardSuit.Heart, CardNumber.n6))

    // Starting game - Betting
    game.start()
    game.playerTurn shouldBe None
    game.state shouldBe Game.State.Betting
    game.bank.cards shouldBe List(Card(CardSuit.Diamond, CardNumber.n9))
    game.getPlayer(0).cards shouldBe List(Card(CardSuit.Heart, CardNumber.n6))
    game.playerAddBet(0, 10)

    // Starting game - Playing
    game.state shouldBe Game.State.Playing
    game.playerTurn shouldBe Some(0)
    game.bank.cards shouldBe List(Card(CardSuit.Diamond, CardNumber.n6), Card(CardSuit.Diamond, CardNumber.n9))
    game.getPlayer(0).cards shouldBe List(Card(CardSuit.Spade, CardNumber.Queen), Card(CardSuit.Heart, CardNumber.n6))
    game.getPlayer(0).totalPoints shouldBe 8

    game.playerHit(0)
    game.getPlayer(0).cards shouldBe List(Card(CardSuit.Spade, CardNumber.n10), Card(CardSuit.Spade, CardNumber.Queen), Card(CardSuit.Heart, CardNumber.n6))
    game.playerTurn shouldBe Some(0)
    game.getPlayer(0).totalPoints shouldBe 18
    game.playerHit(0)
    game.getPlayer(0).cards shouldBe List(Card(CardSuit.Club, CardNumber.n3), Card(CardSuit.Spade, CardNumber.n10), Card(CardSuit.Spade, CardNumber.Queen), Card(CardSuit.Heart, CardNumber.n6))
    game.getPlayer(0).totalPoints shouldBe 21
    game.playerHold(0)

    // Finishing game
    game.playerTurn shouldBe None
    game.state shouldBe Game.State.Done
    game.bank.cards shouldBe List(Card(CardSuit.Spade, CardNumber.Jack), Card(CardSuit.Club, CardNumber.Ace), Card(CardSuit.Diamond, CardNumber.n6), Card(CardSuit.Diamond, CardNumber.n9))
    game.bank.totalPoints shouldBe 17
    game.result shouldBe Map(0 -> Game.Result.Win)
  }

  def testDeck1: List[Card] = Card.allCards.toList
  def testDeck2: List[Card] = List.fill(40)(
    Card(CardSuit.Spade, CardNumber.n3)
  )

  @DataProvider(name = "results")
  def resultsProvider(): Array[Array[Any]] = {
    Array(
      Array(testDeck1,
        List[Game => Any](
          _.addPlayer("player1"),
          _.start(),
          _.playerAddBet(0, 10),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHold(0)
        ),
        Map(0 -> Game.Result.Win), Map(0 -> Player.Status.Hold), Player.Status.Hold),
      Array(testDeck1,
        List[Game => Any](
          _.addPlayer("player1"),
          _.start(),
          _.playerAddBet(0, 10),
          _.playerHit(0),
          _.playerHold(0)
        ),
        Map(0 -> Game.Result.Lose), Map(0 -> Player.Status.Hold), Player.Status.Hold),
      Array(testDeck1,
        List[Game => Any](
          _.addPlayer("player1"),
          _.addPlayer("player2"),
          _.start(),
          _.playerAddBet(0, 10),
          _.playerAddBet(1, 10),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHold(0),
          _.playerHit(1),
          _.playerHit(1),
          _.playerHit(1)
        ),
        Map(0 -> Game.Result.Win, 1 -> Game.Result.Lose),
        Map(0 -> Player.Status.Hold, 1 -> Player.Status.Bust), Player.Status.Bust),
      Array(testDeck2,
        List[Game => Any](
          _.addPlayer("player1"),
          _.start(),
          _.playerAddBet(0, 10),
          _.playerSplit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerHit(0),
          _.playerSplitHit(0),
          _.playerSplitHit(0),
          _.playerSplitHit(0),
          _.playerSplitHit(0),
          _.playerSplitHit(0),
          _.playerSplitHold(0)
        ),
        Map(0 -> Game.Result.Win),
        Map(0 -> Player.Status.Bust), Player.Status.Hold)

    )
  }

  @Test(dataProvider = "results")
  def testResults(deck: List[Card],
                  actions: List[Game => _],
                  result: Map[Int, Game.Result.Value],
                  playerStatus: Map[Int, Player.Status.Value],
                  bankStatus: Player.Status.Value): Unit = {
    // This test will test a sequence of steps and the results.
    // No intermediate results are tested here

    val game = Game(deck, shuffle = false)
    actions.foreach(_(game))
    game.playerTurn shouldBe None
    game.bank.status shouldBe bankStatus
    playerStatus.foreach { case (id, status) => game.getPlayer(id).status shouldBe status}
    game.result shouldBe result
  }

  //TODO: Exception testing
}

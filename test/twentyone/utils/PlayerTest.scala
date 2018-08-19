package twentyone.utils

import org.scalatest.Matchers
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test
import twentyone.utils.deck._

class PlayerTest extends TestNGSuite with Matchers {

  @Test
  def testAddCard(): Unit = {
    val player1 = Player("name")
    player1.cards.size shouldBe 0

    val card1 = Card(CardSuit.Club, CardNumber.Ace)

    val player2 = player1.addCard(card1)
    player2.cards.size shouldBe 1
    player2.cards shouldBe List(card1)

    val player3 = player2.addCard(card1)
    player3.cards.size shouldBe 2
    player3.cards shouldBe List(card1, card1)
  }

  @Test
  def testTotalPoints(): Unit = {
    val player1 = Player("name")
    player1.totalPoints shouldBe 0
    val ace = Card(CardSuit.Club, CardNumber.Ace)
    val card2 = Card(CardSuit.Club, CardNumber.n2)

    val player2 = player1.addCard(card2)
    player2.totalPoints shouldBe 2
    val player3 = player2.addCard(ace)
    player3.totalPoints shouldBe 13
    val player4 = player3.addCard(ace)
    player4.totalPoints shouldBe 14
    val player5 = player4.addCard(card2)
      .addCard(card2)
      .addCard(card2)
    player5.totalPoints shouldBe 20
    val player6 = player5.addCard(card2)
    player6.totalPoints shouldBe 12
  }

  @Test
  def testTotalSplitPoints(): Unit = {
    val player1 = Player("name", splitCards = Some(Nil))
    player1.totalSplitPoints shouldBe Some(0)
    val ace = Card(CardSuit.Club, CardNumber.Ace)
    val card2 = Card(CardSuit.Club, CardNumber.n2)

    val player2 = player1.addSplitCard(card2)
    player2.totalSplitPoints shouldBe Some(2)
    val player3 = player2.addSplitCard(ace)
    player3.totalSplitPoints shouldBe Some(13)
    val player4 = player3.addSplitCard(ace)
    player4.totalSplitPoints shouldBe Some(14)
    val player5 = player4.addSplitCard(card2)
      .addSplitCard(card2)
      .addSplitCard(card2)
    player5.totalSplitPoints shouldBe Some(20)
    val player6 = player5.addSplitCard(card2)
    player6.totalSplitPoints shouldBe Some(12)
  }

  @Test
  def testBet(): Unit = {
    val player = Player("name")
    player.bet shouldBe None

    player.setBet(10)
    player.setBet(10).bet shouldBe Some(10)
    intercept[IllegalArgumentException] {
      player.setBet(10).setBet(5)
    }.getMessage shouldBe "requirement failed: Bet is already set"
  }

  @Test
  def testCanSplit(): Unit = {
    val ace = Card(CardSuit.Club, CardNumber.Ace)
    val player = Player("name", status = Player.Status.Playing, bet = Some(10))
      .addCard(ace)
      .addCard(ace)
    player.canSplit shouldBe true

    val playerSplit = player.split
    playerSplit.canSplit shouldBe false
    playerSplit.cards shouldBe List(ace)
    playerSplit.splitCards shouldBe Some(List(ace))

    val playerSplit2 = playerSplit.addSplitCard(ace)
    playerSplit2.cards shouldBe List(ace)
    playerSplit2.splitCards shouldBe Some(List(ace, ace))
  }

  @Test
  def testCanNotSplit(): Unit = {
    val playerEmpty = Player("name", status = Player.Status.Playing, bet = Some(10))
    val ace = Card(CardSuit.Club, CardNumber.Ace)
    val card2 = Card(CardSuit.Club, CardNumber.n2)
    val error = "requirement failed: Split is not allowed"
    intercept[IllegalArgumentException] {
      playerEmpty.addSplitCard(ace)
    }.getMessage shouldBe "Player does not have a split hand"

    playerEmpty.canSplit shouldBe false
    intercept[IllegalArgumentException] {
      playerEmpty.split
    }.getMessage shouldBe error

    val player1 = playerEmpty.addCard(ace)
    player1.canSplit shouldBe false
    intercept[IllegalArgumentException] {
      player1.split
    }.getMessage shouldBe error

    val player2 = player1.addCard(card2)
    player2.canSplit shouldBe false
    intercept[IllegalArgumentException] {
      player2.split
    }.getMessage shouldBe error

    val player3 = playerEmpty
      .addCard(ace)
      .addCard(ace)
      .addCard(ace)

    player3.canSplit shouldBe false
    intercept[IllegalArgumentException] {
      player3.split
    }.getMessage shouldBe error
  }
}

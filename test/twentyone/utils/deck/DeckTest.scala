package twentyone.utils.deck

import org.scalatest.Matchers
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.{DataProvider, Test}

class DeckTest extends TestNGSuite with Matchers {

  @DataProvider(name = "allCards")
  def allCards: Array[Array[Card]] = {
    Card.allCards.map(Array(_)).toArray
  }

  @Test(dataProvider = "allCards")
  def testPoints(card: Card): Unit = {
    val minimal = card.number match {
      case CardNumber.Ace => 1
      case CardNumber.n2 => 2
      case CardNumber.n3 => 3
      case CardNumber.n4 => 4
      case CardNumber.n5 => 5
      case CardNumber.n6 => 6
      case CardNumber.n7 => 7
      case CardNumber.n8 => 8
      case CardNumber.n9 => 9
      case CardNumber.n10 => 10
      case CardNumber.Jack => 1
      case CardNumber.Queen => 2
      case CardNumber.King => 3
    }

    card.points shouldBe minimal
  }

  @Test
  def numberCards(): Unit = {
    allCards.length shouldBe 52
    Card.allCards.size shouldBe 52
  }

  @Test
  def testMultiDeck(): Unit = {
    multiDeck(3).size shouldBe (52 * 3)
    multiDeck(3).toSet.size shouldBe 52
  }
}

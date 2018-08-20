package views

import org.scalatest.Matchers
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.{DataProvider, Test}
import twentyone.utils.deck.Card
import views.html.card

class CardTest extends TestNGSuite with Matchers {

  @DataProvider(name = "allCards")
  def allCards: Array[Array[Card]] = {
    Card.allCards.map(Array(_)).toArray
  }

  @Test(dataProvider = "allCards")
  def testTemplate(c: Card): Unit = {
    // Executing this test will test if the file to the card does exists
    val output = card(c)
  }
}

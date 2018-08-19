package twentyone.utils

package object deck {
  object CardSuit extends Enumeration {
    val Club, Diamond, Heart, Spade = Value
  }

  object CardNumber extends Enumeration {
    val Ace, n2, n3, n4, n5, n6, n7, n8, n9, n10, Jack, Queen, King = Value
  }

  /** Returns a deck with multiple card decks of 52 cards */
  def multiDeck(decks: Int): List[Card] = {
    List.fill(decks)(Card.allCards.toList).flatten
  }
}

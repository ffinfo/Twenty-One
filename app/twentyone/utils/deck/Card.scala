package twentyone.utils.deck

case class Card(suit: CardSuit.Value, number: CardNumber.Value) {

  /**
    * This will return point and optional extra points for aces
    * @return
    */
  def points: Int = number match {
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

  /** Returns file name of the svg image */
  def imgFileName: String = {
    val suitName = suit match {
      case CardSuit.Spade => "spades"
      case CardSuit.Club => "clubs"
      case CardSuit.Heart => "hearts"
      case CardSuit.Diamond => "diamonds"
    }
    val numberName = number match {
      case CardNumber.Ace => "ace"
      case CardNumber.n2 => 2
      case CardNumber.n3 => 3
      case CardNumber.n4 => 4
      case CardNumber.n5 => 5
      case CardNumber.n6 => 6
      case CardNumber.n7 => 7
      case CardNumber.n8 => 8
      case CardNumber.n9 => 9
      case CardNumber.n10 => 10
      case CardNumber.Jack => "jack"
      case CardNumber.Queen => "queen"
      case CardNumber.King => "king"
    }
    s"${numberName}_of_$suitName.svg"
  }
}

object Card {

  def allCards: Set[Card] = {
    for {
      suit <- CardSuit.values
      number <- CardNumber.values
    } yield Card(suit, number)
  }
}

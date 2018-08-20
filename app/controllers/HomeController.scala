package controllers

import javax.inject._
import play.api.mvc._
import twentyone.utils.Game

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /** Landing page, show all games and what games can still be joined */
  def index = Action {
    Ok(views.html.index(HomeController.games.toList))
  }

  /** Showing a game without binding it to a player */
  def game(gameId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) => Ok(views.html.game(game, gameId, None, game.playerTurn))
      case _ => NotFound("Game does not exists")
    }
  }

  /** Showing a game with binding it to a player */
  def player(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        Ok(views.html.game(game, gameId, Some(playerId), game.playerTurn))
      case _ => NotFound("Game does not exists")
    }
  }

  /** Creates a game and redirect to that game page */
  def createGame(playerName: String) = Action {
    require(playerName.nonEmpty, "Player can't be a empty string")
    val game = Game(1)
    game.addPlayer(playerName)
    HomeController.games += game
    Redirect(s"/game/${HomeController.games.size - 1}/player/0")
  }

  /** Joins a game and redirect to that game page */
  def joinGame(gameId: Int, playerName: String) = Action {
    require(playerName.nonEmpty, "Player can't be a empty string")
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.addPlayer(playerName)
        Redirect(s"/game/$gameId/player/${game.numberPlayers - 1}")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Starting a game and redirect to that game page */
  def startGame(gameId: Int, playerId: Option[Int]) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.start()
        playerId match {
          case Some(p) => Redirect(s"/game/$gameId/player/$p")
          case _ => Redirect(s"/game/$gameId")
        }
      case _ => NotFound("Game does not exists")
    }
  }

  /** Setting a bet for a player and redirect to that game page */
  def bet(gameId: Int, playerId: Int, bet: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerAddBet(playerId, bet)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Execute action hit and redirect to that game page */
  def hit(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerHit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Execute action hold and redirect to that game page */
  def hold(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerHold(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Execute action split and redirect to that game page */
  def split(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerSplit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Execute action splitHit and redirect to that game page */
  def splitHit(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerSplitHit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  /** Execute action splitHold and redirect to that game page */
  def splitHold(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerSplitHold(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

}

object HomeController {
  /** Storage for all games */
  private val games: ListBuffer[Game] = ListBuffer()
}
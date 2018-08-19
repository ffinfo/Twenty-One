package controllers

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import twentyone.utils.Game

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index = Action { request =>
    Ok(views.html.index(HomeController.games.toList))
  }

  def game(gameId: Int) = Action { request =>
    HomeController.games.lift(gameId) match {
      case Some(game) => Ok(views.html.game(game, gameId, None, game.playerTurn))
      case _ => NotFound("Game does not exists")
    }
  }

  def player(gameId: Int, playerId: Int) = Action { request =>
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        Ok(views.html.game(game, gameId, Some(playerId), game.playerTurn))
      case _ => NotFound("Game does not exists")
    }
  }

  def createGame(playerName: String) = Action { request =>
    require(playerName.nonEmpty, "Player can't be a empty string")
    val game = Game(1)
    game.addPlayer(playerName)
    HomeController.games += game
    Redirect(s"/game/${HomeController.games.size - 1}/player/0")
  }

  def joinGame(gameId: Int, playerName: String) = Action { request =>
    require(playerName.nonEmpty, "Player can't be a empty string")
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.addPlayer(playerName)
        Redirect(s"/game/$gameId/player/${game.numberPlayers - 1}")
      case _ => NotFound("Game does not exists")
    }
  }

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

  def bet(gameId: Int, playerId: Int, bet: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerAddBet(playerId, bet)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  def hit(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerHit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  def hold(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerHold(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  def split(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerSplit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

  def splitHit(gameId: Int, playerId: Int) = Action {
    HomeController.games.lift(gameId) match {
      case Some(game) =>
        game.playerSplitHit(playerId)
        Redirect(s"/game/$gameId/player/$playerId")
      case _ => NotFound("Game does not exists")
    }
  }

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
  private val games: ListBuffer[Game] = ListBuffer()
}
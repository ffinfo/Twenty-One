package controllers

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import twentyone.utils.Game

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index = Action { request =>
    Ok(views.html.index(HomeController.games.toMap))
  }

  def game(gameId: Int) = Action { request =>
    HomeController.games.get(gameId) match {
      case Some(game) => Ok("")
      case _ => NotFound("Game does not exists")
    }
  }

  def player(gam: Int, player: Int) = Action { request =>
    Ok("")
  }

}

object HomeController {
  private val games: mutable.Map[Int, Game] = mutable.Map()
}
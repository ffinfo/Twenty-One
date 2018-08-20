package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api._
import play.api.cache.ehcache.EhCacheModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._

class HomeTest extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with Results {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .disable[EhCacheModule]
      .build()

  "Game" must {
    val baseUrl = s"http://localhost:$port"

    "Start with" in {
      go to baseUrl
      textField("name").value = "player1"
      submit()
    }
    "Join with" in {
      go to baseUrl
      textField("join").value = "player2"
      submit()
    }
  }
}
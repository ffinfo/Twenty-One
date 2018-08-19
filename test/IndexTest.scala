import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api._
import play.api.cache.ehcache.EhCacheModule
import play.api.inject.guice.GuiceApplicationBuilder

class IndexTest extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .disable[EhCacheModule]
      .build()

  "Index Page" must {
    "Title" in {
      go to s"http://localhost:$port"
      pageTitle mustBe "Twenty One"
    }
  }
}
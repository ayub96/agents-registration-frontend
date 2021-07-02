/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agentsregfrontend.controllers

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, session, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.agentsregfrontend.services.SummaryService
import uk.gov.hmrc.agentsregfrontend.views.html.Summary
import uk.gov.hmrc.agentsregfrontend.views.html.ARNPage

import scala.concurrent.Future

class SummaryControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  val service: SummaryService = mock(classOf[SummaryService])
  val summaryPage: Summary = app.injector.instanceOf[Summary]
  val arnPage: ARNPage = app.injector.instanceOf[ARNPage]
  val controller = new SummaryController(Helpers.stubMessagesControllerComponents(),service,summaryPage,arnPage)

  "GET /summary" should {
    "get all the session values and send them to the summary page" in  {
      val result = controller.summary.apply(FakeRequest("GET", "/").withSession("password"  -> "testPassword", "businessName" -> "testBusinessName", "email" -> "testEmail", "contactNumber" -> "09876", "modes" -> "test,test", "address" -> "propertyNum/postcode"))
      status(result) shouldBe 200
      Jsoup.parse(contentAsString(result)).getElementsByClass("govuk-summary-list__row").size shouldBe(5)
    }
  }
  "GET / arn" should {
    "get all the session values and send them to the summary page" in  {
      when(service.agentDetails(any())) thenReturn(Future.successful("ARN0000005"))
      val result = controller.getArn.apply(FakeRequest("GET", "/").withSession("password"  -> "testPassword", "businessName" -> "testBusinessName", "email" -> "testEmail", "contactNumber" -> "09876", "modes" -> "test,test", "address" -> "propertyNum/postcode"))
      status(result) shouldBe 200
      Jsoup.parse(contentAsString(result)).text() should include("ARN0000005")
    }
  }


}
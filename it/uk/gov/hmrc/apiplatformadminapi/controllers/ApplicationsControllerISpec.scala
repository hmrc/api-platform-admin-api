/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatformadminapi.controllers

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.test.WireMockSupport

import uk.gov.hmrc.apiplatformadminapi.models.ErrorResponse
import uk.gov.hmrc.apiplatformadminapi.stubs.{InternalAuthStub, ThirdPartyOrchestratorConnectorStub}
import uk.gov.hmrc.apiplatformadminapi.utils.{ApplicationTestData, AsyncHmrcSpec}

class ApplicationsControllerISpec extends AsyncHmrcSpec with WireMockSupport with GuiceOneAppPerSuite {

  val stubConfig = Configuration(
    "microservice.services.third-party-orchestrator.port" -> wireMockPort,
    "microservice.services.internal-auth.port"            -> wireMockPort,
    "metrics.enabled"                                     -> false,
    "auditing.enabled"                                    -> false
  )

  override def fakeApplication() = GuiceApplicationBuilder()
    .configure(stubConfig)
    .build()

  trait Setup extends ThirdPartyOrchestratorConnectorStub with InternalAuthStub with ApplicationTestData {

    val token     = "123456"
    val underTest = app.injector.instanceOf[ApplicationsController]
  }

  "getApplication" should {

    "return 200 on the agreed route" in new Setup {
      Authenticate.returns(token)
      GetApplication.stubWithApplicationId(applicationId)
      GetApplicationDevelopers.stubWithApplicationId(applicationId)

      val fakeRequest = FakeRequest("GET", s"/applications/$applicationId").withHeaders("Authorization" -> token)

      val result = route(app, fakeRequest).get

      status(result) mustBe OK
      // the response body is tested in `tests/.../ApplicationsControllerSpec` so not repeated here
    }

    // Testing the Binder for ApplicationId
    "return 400 when applicationId is not a UUID" in new Setup {
      val result = route(app, FakeRequest("GET", "/applications/not-a-uuid")).get

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe ErrorResponse("BAD_REQUEST", "applicationId is not a UUID").asJson
    }

    "return 401 with no Authorisation header" in new Setup {
      val fakeRequest = FakeRequest("GET", s"/applications/$applicationId")

      val result = route(app, fakeRequest).get

      status(result) mustBe UNAUTHORIZED
    }
  }

  "getApplicationsByQueryParam" should {

    "return 200 on the agreed route" in new Setup {
      Authenticate.returns(token)
      GetApplicationByClientId.stubWithClientId(clientId)

      val fakeRequest = FakeRequest("GET", s"/applications?clientId=$clientId").withHeaders("Authorization" -> token)

      val result = route(app, fakeRequest).get

      status(result) mustBe OK
      // the response body is tested in `tests/.../ApplicationsControllerSpec` so not repeated here
    }

    "return 400 when there are no query parameters" in new Setup {
      Authenticate.returns(token)

      val fakeRequest = FakeRequest("GET", s"/applications").withHeaders("Authorization" -> token)

      val result = route(app, fakeRequest).get

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe ErrorResponse("BAD_REQUEST", "Invalid or missing query parameters").asJson
    }

    "return 400 when the query parameters are not valid" in new Setup {
      Authenticate.returns(token)

      val fakeRequest = FakeRequest("GET", s"/applications?x=a&y=b").withHeaders("Authorization" -> token)

      val result = route(app, fakeRequest).get

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe ErrorResponse("BAD_REQUEST", "Invalid or missing query parameters").asJson
    }
  }
}

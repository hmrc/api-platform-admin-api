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
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.test.WireMockSupport

import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatform.modules.tpd.test.data.UserTestData
import uk.gov.hmrc.apiplatform.modules.tpd.test.utils.LocalUserIdTracker
import uk.gov.hmrc.apiplatformadminapi.models.{ErrorResponse, UserRequest}
import uk.gov.hmrc.apiplatformadminapi.stubs.{InternalAuthStub, ThirdPartyOrchestratorConnectorStub}
import uk.gov.hmrc.apiplatformadminapi.utils.AsyncHmrcSpec

class UsersControllerISpec extends AsyncHmrcSpec with WireMockSupport with GuiceOneAppPerSuite with ThirdPartyOrchestratorConnectorStub {

  val stubConfig = Configuration(
    "microservice.services.third-party-orchestrator.port" -> wireMockPort,
    "microservice.services.internal-auth.port"            -> wireMockPort,
    "metrics.enabled"                                     -> false,
    "auditing.enabled"                                    -> false
  )

  override def fakeApplication() = GuiceApplicationBuilder()
    .configure(stubConfig)
    .build()

  trait Setup extends UserTestData with LocalUserIdTracker with InternalAuthStub {
    val token     = "123456"
    val underTest = app.injector.instanceOf[ApplicationsController]
    val sessionId = UserSessionId.random
  }

  "userQuery" should {

    "return 200 on the agreed route" in new Setup {
      Authenticate.returns(token)
      GetBySessionId.stubWithSessionId(sessionId)

      val result = route(app, FakeRequest("POST", s"/users/query").withJsonBody(Json.toJson(UserRequest(sessionId))).withHeaders("Authorization" -> token)).get

      status(result) mustBe OK
      // the response body is tested in `tests/.../UsersControllerSpec` so not repeated here
    }

    "return 400 when the request body is missing" in new Setup {
      Authenticate.returns(token)
      GetBySessionId.stubWithSessionId(sessionId)

      val result = route(app, FakeRequest("POST", s"/users/query").withHeaders("Authorization" -> token)).get

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe ErrorResponse("BAD_REQUEST", "Invalid JSON payload").asJson
    }
  }
}

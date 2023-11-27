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
import uk.gov.hmrc.apiplatformadminapi.stubs.ThirdPartyOrchestratorConnectorStub
import uk.gov.hmrc.apiplatformadminapi.utils.{ApplicationTestData, AsyncHmrcSpec}

class ApplicationsControllerISpec extends AsyncHmrcSpec with WireMockSupport with GuiceOneAppPerSuite {

  val stubConfig = Configuration(
    "microservice.services.third-party-orchestrator.port" -> wireMockPort,
    "metrics.enabled"                                     -> false,
    "auditing.enabled"                                    -> false
  )

  override def fakeApplication() = GuiceApplicationBuilder()
    .configure(stubConfig)
    .build()

  trait Setup extends ThirdPartyOrchestratorConnectorStub with ApplicationTestData {
    val underTest = app.injector.instanceOf[ApplicationsController]

  }

  "getApplication" should {

    "return 200 on the agreed route" in new Setup {
      GetApplication.returns(applicationResponse)
      GetApplicationDevelopers.returnsFor(applicationId, developers)

      val result = route(app, FakeRequest("GET", s"/applications/$applicationId")).get

      status(result) mustBe OK
      // the response body is tested in `tests/.../ApplicationsControllerSpec` so not repeated here
    }

    // Testing the Binder for ApplicationId
    "return 400 when applicationId is not a UUID" in new Setup {
      val result = route(app, FakeRequest("GET", "/applications/not-a-uuid")).get

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe ErrorResponse("BAD_REQUEST", "applicationId is not a UUID").asJson
    }
  }
}

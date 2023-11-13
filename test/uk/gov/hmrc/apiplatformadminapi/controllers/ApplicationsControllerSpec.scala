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

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.http.Status
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, Environment, LaxEmailAddress, UserId}
import uk.gov.hmrc.apiplatformadminapi.mocks.ApplicationsServiceMockModule
import uk.gov.hmrc.apiplatformadminapi.models.{ApplicationResponse, Developer, ErrorResponse, FetchedApplication, User}
import uk.gov.hmrc.apiplatformadminapi.utils.HmrcSpec

class ApplicationsControllerSpec extends HmrcSpec with ApplicationsServiceMockModule {

  trait Setup {
    implicit val hc = HeaderCarrier()

    // Request is not interrogated for parameters or body, so a dummy for now; this will change when adding internal-auth
    val fakeRequest = FakeRequest()

    val underTest = new ApplicationsController(mockApplicationsService, Helpers.stubControllerComponents())

    val userId    = UserId.random
    val developer = Developer(userId, LaxEmailAddress("test@test.com"), "Barbara", "Liskov")
    val user      = User(userId, LaxEmailAddress("test@test.com"), "Barbara", "Liskov")

    val applicationId       = ApplicationId.random
    val fetchedApplication  = FetchedApplication(applicationId, "name", Environment.PRODUCTION, Set(developer))
    val applicationResponse = ApplicationResponse(applicationId, "name", Environment.PRODUCTION, Set(user))
  }

  "getApplication" should {
    "return 200 and an Application body" in new Setup {
      GetApplication.returns(fetchedApplication)

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[ApplicationResponse] shouldBe applicationResponse
      GetApplication.verifyCalledWith(applicationId)
    }

    "return 404 if the application cannot found" in new Setup {
      GetApplication.returnsNone()

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "Application could not be found").asJson
    }

    "return 500 if there is an unexpected error" in new Setup {
      GetApplication.fails()

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
    }
  }
}

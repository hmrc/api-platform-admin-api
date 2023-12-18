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
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apiplatform.modules.developers.domain.models.SessionId
import uk.gov.hmrc.apiplatformadminapi.mocks.UsersServiceMockModule
import uk.gov.hmrc.apiplatformadminapi.models.{ErrorResponse, User, UserRequest}
import uk.gov.hmrc.apiplatformadminapi.utils.{HmrcSpec, UserTestData}

class UsersControllerSpec extends HmrcSpec with UsersServiceMockModule with UserTestData {

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val sessionId   = SessionId.random
    val fakeRequest = FakeRequest().withJsonBody(Json.toJson(UserRequest(sessionId)))

    val underTest = new UsersController(mockService, Helpers.stubControllerComponents())
  }

  "userQuery" should {
    "return 200 and a User body" in new Setup {
      GetUserBySessionId.returns(developer)

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[User] shouldBe user
      GetUserBySessionId.verifyCalledWith(sessionId)
    }

    "return 404 if the user cannot be found" in new Setup {
      GetUserBySessionId.returnsNone()

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "User could not be found").asJson
    }

    "return 400 if the body is invalid" in new Setup {
      GetUserBySessionId.returnsNone()

      val result = underTest.userQuery()(FakeRequest().withTextBody("random"))

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe ErrorResponse("BAD_REQUEST", "Invalid JSON payload").asJson
    }

    "return 400 if the body is missing sessionId" in new Setup {
      GetUserBySessionId.returnsNone()

      val result = underTest.userQuery()(FakeRequest().withJsonBody(Json.parse("{}")))

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe ErrorResponse("BAD_REQUEST", "Invalid JSON payload").asJson
    }

    "return 500 if there is an unexpected error" in new Setup {
      GetUserBySessionId.fails()

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
    }
  }
}

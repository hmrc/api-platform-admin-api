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
import scala.concurrent.Future

import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client.{Retrieval, _}

import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec
import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatform.modules.tpd.test.data.UserTestData
import uk.gov.hmrc.apiplatform.modules.tpd.test.utils.LocalUserIdTracker
import uk.gov.hmrc.apiplatformadminapi.mocks.UsersServiceMockModule
import uk.gov.hmrc.apiplatformadminapi.models.{ErrorResponse, UserRequest, UserResponse}

class UsersControllerSpec extends HmrcSpec with UsersServiceMockModule with UserTestData with LocalUserIdTracker {

  trait Setup {
    implicit val hc: HeaderCarrier        = HeaderCarrier()
    implicit val cc: ControllerComponents = Helpers.stubControllerComponents()

    val sessionId = UserSessionId.random

    val mockStubBehaviour = mock[StubBehaviour]
    val underTest         = new UsersController(mockService, cc, BackendAuthComponentsStub(mockStubBehaviour))

    val expectedPredicate = Permission(Resource(ResourceType("api-platform-admin-api"), ResourceLocation("users/all")), IAAction("READ"))
  }

  "userQuery" should {
    "return 200 and a User body" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetUserBySessionId.returns(standardDeveloper)

      val fakeRequest = FakeRequest().withJsonBody(Json.toJson(UserRequest(sessionId))).withHeaders("Authorization" -> "123456")

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[UserResponse] shouldBe UserResponse.from(standardDeveloper)
      GetUserBySessionId.verifyCalledWith(sessionId)
    }

    "return 404 if the user cannot be found" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetUserBySessionId.returnsNone()

      val fakeRequest = FakeRequest().withJsonBody(Json.toJson(UserRequest(sessionId))).withHeaders("Authorization" -> "123456")

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "User could not be found").asJson
    }

    "return 400 if the body is invalid" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetUserBySessionId.returnsNone()

      val fakeRequest = FakeRequest().withTextBody("random").withHeaders("Authorization" -> "123456")

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe ErrorResponse("BAD_REQUEST", "Invalid JSON payload").asJson
    }

    "return 400 if the body is missing sessionId" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetUserBySessionId.returnsNone()

      val fakeRequest = FakeRequest().withJsonBody(Json.parse("{}")).withHeaders("Authorization" -> "123456")

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe ErrorResponse("BAD_REQUEST", "Invalid JSON payload").asJson
    }

    "return 500 if there is an unexpected error" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetUserBySessionId.fails()

      val fakeRequest = FakeRequest().withJsonBody(Json.toJson(UserRequest(sessionId))).withHeaders("Authorization" -> "123456")

      val result = underTest.userQuery()(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
    }

    "return unauthorised when invalid token" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.failed(UpstreamErrorResponse("Unauthorized", Status.UNAUTHORIZED)))

      val fakeRequest = FakeRequest().withJsonBody(Json.toJson(UserRequest(sessionId))).withHeaders("Authorization" -> "123456")

      intercept[UpstreamErrorResponse] {
        await(underTest.userQuery()(fakeRequest))
      }
    }
  }
}

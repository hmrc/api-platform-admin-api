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
import play.api.mvc.request.RequestTarget
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client.{Retrieval, _}

import uk.gov.hmrc.apiplatformadminapi.mocks.ApplicationsServiceMockModule
import uk.gov.hmrc.apiplatformadminapi.models.{ApplicationWithUsers, Applications, ErrorResponse}
import uk.gov.hmrc.apiplatformadminapi.utils.{ApplicationTestData, HmrcSpec}

class ApplicationsControllerSpec extends HmrcSpec with ApplicationTestData {

  trait Setup extends ApplicationsServiceMockModule {
    implicit val hc = HeaderCarrier()
    implicit val cc = Helpers.stubControllerComponents()

    val fakeRequest = FakeRequest().withHeaders("Authorization" -> "123456")

    val fakeRequestWithClientId = FakeRequest().withTarget(RequestTarget("GET", "/", Seq("clientId" -> Seq(clientId.value)).toMap)).withHeaders("Authorization" -> "123456")

    val mockStubBehaviour = mock[StubBehaviour]
    val underTest         = new ApplicationsController(mockApplicationsService, cc, BackendAuthComponentsStub(mockStubBehaviour))

    val expectedPredicate = Permission(Resource(ResourceType("api-platform-admin-api"), ResourceLocation("applications/all")), IAAction("READ"))
  }

  "getApplication" should {
    "return 200 and an Application body" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationWithUsers.returns(applicationWithUsers)

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[ApplicationWithUsers] shouldBe applicationWithUsers
      GetApplicationWithUsers.verifyCalledWith(applicationId)
    }

    "return 404 if the application cannot be found" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationWithUsers.returnsNotFound()

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "Application could not be found").asJson
      GetApplicationWithUsers.verifyCalledWith(applicationId)
    }

    "return 500 if there is an unexpected error" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationWithUsers.fails()

      val result = underTest.getApplication(applicationId)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
      GetApplicationWithUsers.verifyCalledWith(applicationId)
    }

    "return unauthorised when invalid token" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.failed(UpstreamErrorResponse("Unauthorized", Status.UNAUTHORIZED)))

      intercept[UpstreamErrorResponse] {
        await(underTest.getApplication(applicationId)(fakeRequest))
      }
      GetApplicationWithUsers.verifyNeverCalledWith(applicationId)
    }
  }

  "getApplicationByQueryParams" should {
    "return 200 and an Application body" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationByClientId.returns(application)

      val result = underTest.getApplicationsByQueryParam()(fakeRequestWithClientId)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[Applications] shouldBe applications
      GetApplicationByClientId.verifyCalledWith(clientId)
    }

    "return 404 if the application cannot be found" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationByClientId.returnsNotFound()

      val result = underTest.getApplicationsByQueryParam()(fakeRequestWithClientId)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "Application could not be found").asJson
      GetApplicationByClientId.verifyCalledWith(clientId)
    }

    "return 500 if there is an unexpected error" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      GetApplicationByClientId.fails()

      val result = underTest.getApplicationsByQueryParam()(fakeRequestWithClientId)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
      GetApplicationByClientId.verifyCalledWith(clientId)
    }

    "return unauthorised when invalid token" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.failed(UpstreamErrorResponse("Unauthorized", Status.UNAUTHORIZED)))

      intercept[UpstreamErrorResponse] {
        await(underTest.getApplicationsByQueryParam()(fakeRequestWithClientId))
      }
    }
  }
}

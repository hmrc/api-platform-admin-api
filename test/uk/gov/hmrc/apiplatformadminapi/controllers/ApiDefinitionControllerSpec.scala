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
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client.{Retrieval, _}

import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment
import uk.gov.hmrc.apiplatformadminapi.mocks._
import uk.gov.hmrc.apiplatformadminapi.models._
import uk.gov.hmrc.apiplatformadminapi.utils.{ApiTestData, HmrcSpec}

class ApiDefinitionControllerSpec extends HmrcSpec with ApisServiceMockModule with ApiTestData {

  trait Setup {
    implicit val hc = HeaderCarrier()
    implicit val cc = Helpers.stubControllerComponents()

    val fakeRequest = FakeRequest().withHeaders("Authorization" -> "123456")

    val mockStubBehaviour = mock[StubBehaviour]
    val underTest         = new ApiDefinitionController(mockApisService, cc, BackendAuthComponentsStub(mockStubBehaviour))

    val expectedPredicate = Permission(Resource(ResourceType("api-platform-admin-api"), ResourceLocation("api-definitions/all")), IAAction("READ"))
  }

  "fetch" should {
    "return 200 and an Api body" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      FetchApi.returns(anApiInBoth)

      val result = underTest.fetch(aServiceName, Environment.PRODUCTION)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result).as[ApiResponse] shouldBe apiResponse
      FetchApi.verifyCalledWith(aServiceName)
    }

    "return 404 if the api cannot be found in SANDBOX" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      FetchApi.returns(anApiInProduction)

      val result = underTest.fetch(aServiceName, Environment.SANDBOX)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "API could not be found").asJson
    }

    "return 404 if the api cannot be found in PRODUCTION" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      FetchApi.returns(anApiInSandbox)

      val result = underTest.fetch(aServiceName, Environment.PRODUCTION)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "API could not be found").asJson
    }

    "return 404 if the api cannot be found" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      FetchApi.returnsNone()

      val result = underTest.fetch(aServiceName, Environment.PRODUCTION)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentAsJson(result) shouldBe ErrorResponse("NOT_FOUND", "API could not be found").asJson
    }

    "return 500 if there is an unexpected error" in new Setup {
      when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.successful(Retrieval.Username("Bob")))
      FetchApi.fails()

      val result = underTest.fetch(aServiceName, Environment.PRODUCTION)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: bang").asJson
    }
  }
}

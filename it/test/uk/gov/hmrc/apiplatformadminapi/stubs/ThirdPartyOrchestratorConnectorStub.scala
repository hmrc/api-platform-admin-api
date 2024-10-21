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

package uk.gov.hmrc.apiplatformadminapi.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

import play.api.test.Helpers._

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId}
import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatformadminapi.utils.WireMockExtensions

trait ThirdPartyOrchestratorConnectorStub extends WireMockExtensions {

  object GetApplication {

    def stubWithApplicationId(applicationId: ApplicationId): StubMapping =
      stubFor(
        get(urlPathEqualTo(s"/applications/$applicationId"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(applicationResponseBody)
          )
      )
  }

  object GetApplicationByClientId {

    def stubWithClientId(clientId: ClientId): StubMapping =
      stubFor(
        get(urlPathEqualTo(s"/applications"))
          .withQueryParam("clientId", equalTo(clientId.value))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(applicationResponseBody)
          )
      )
  }

  object GetApplicationDevelopers {

    def stubWithApplicationId(applicationId: ApplicationId): StubMapping =
      stubFor(
        get(urlPathEqualTo(s"/applications/$applicationId/developers"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(s"[$userResponseBody]")
          )
      )
  }

  object GetBySessionId {

    def stubWithSessionId(sessionId: UserSessionId): StubMapping =
      stubFor(
        post(urlPathEqualTo(s"/session/validate"))
          .withRequestBody(equalTo(s"""{"sessionId":"${sessionId.value}"}"""))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(userResponseBody)
          )
      )
  }

  private val applicationResponseBody =
    s"""{
       |  "id": "967226ae-46ca-4b71-a76c-72efbc402a9b",
       |  "clientId": "tl68AJH3PA8kKe7H9gxIatou2UTK",
       |  "gatewayId": "gateway-id",
       |  "name": "Application Name",
       |  "deployedTo": "PRODUCTION",
       |  "collaborators": [],
       |  "createdOn": "2023-11-22T14:56:57.833Z",
       |  "grantLength": 30,
       |  "redirectUris": [],
       |  "access": {
       |    "redirectUris": [],
       |    "overrides": [],
       |    "accessType": "STANDARD"
       |  },
       |  "state": {
       |    "name": "TESTING",
       |    "updatedOn": "2023-11-22T14:56:57.833Z"
       |  },
       |  "rateLimitTier": "BRONZE",
       |  "blocked": false,
       |  "trusted": false,
       |  "ipAllowlist": {
       |    "required": false,
       |    "allowlist": []
       |  },
       |  "moreApplication": {
       |    "allowAutoDelete": false,
       |    "lastActionActor": "UNKNOWN"
       |  }
       |}""".stripMargin

  private val userResponseBody =
    s"""{
       |  "userId": "967226ae-46ca-4b71-a76c-72efbc402a9b",
       |  "email": "test@test.com",
       |  "firstName": "Ada",
       |  "lastName": "Lovelace",
       |  "registrationTime": "2023-09-21T19:25:41.251Z",
       |  "lastModified": "2024-10-18T08:21:35.329Z",
       |  "verified": true,
       |  "mfaDetails": [],
       |  "emailPreferences": {
       |    "interests": [],
       |    "topics": []
       |  }
       |}""".stripMargin
}

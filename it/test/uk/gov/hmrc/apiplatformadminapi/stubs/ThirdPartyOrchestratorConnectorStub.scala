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

import play.api.libs.json.Json
import play.api.test.Helpers._

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithCollaboratorsFixtures
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId}
import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatform.modules.tpd.test.data.UserTestData
import uk.gov.hmrc.apiplatform.modules.tpd.test.utils.LocalUserIdTracker
import uk.gov.hmrc.apiplatformadminapi.utils.WireMockExtensions

trait ThirdPartyOrchestratorConnectorStub extends WireMockExtensions with ApplicationWithCollaboratorsFixtures with UserTestData with LocalUserIdTracker {

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

  private val applicationResponseBody = Json.toJson(standardApp).toString

  private val userResponseBody = Json.toJson(standardDeveloper).toString

}

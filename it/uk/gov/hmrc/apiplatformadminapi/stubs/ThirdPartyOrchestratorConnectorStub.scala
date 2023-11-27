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

import play.api.test.Helpers._

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationResponse
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatform.modules.developers.domain.models.Developer
import uk.gov.hmrc.apiplatformadminapi.utils.WireMockExtensions

trait ThirdPartyOrchestratorConnectorStub extends WireMockExtensions {

  object GetApplication {

    def returns(applicationResponse: ApplicationResponse): Any =
      stubFor(
        get(urlPathEqualTo(s"/applications/${applicationResponse.id}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withJsonBody(applicationResponse)
          )
      )
  }

  object GetApplicationDevelopers {

    def returnsFor(applicationId: ApplicationId, developers: Set[Developer]): Any =
      stubFor(
        get(urlPathEqualTo(s"/applications/$applicationId/developers"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withJsonBody(developers)
          )
      )
  }

  object GetBySessionId {

    def returns(developer: Developer): Any =
      stubFor(
        post(urlPathEqualTo(s"/session/validate"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withJsonBody(developer)
          )
      )
  }
}

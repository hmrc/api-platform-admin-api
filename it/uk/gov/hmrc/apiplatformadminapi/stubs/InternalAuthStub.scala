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

import uk.gov.hmrc.apiplatformadminapi.utils.WireMockExtensions

trait InternalAuthStub extends WireMockExtensions {

  object Authenticate {

    def returns(token: String): Any = {
      stubFor(
        post(urlEqualTo("/internal-auth/auth"))
          .withHeader("Authorization", equalTo(token))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "retrievals": [ "user1", "user1@example.com", [ { "resourceType": "api-platform-admin-api", "resourceLocation": "applications/all" } ], true ]
                   |}""".stripMargin
              )
          )
      )
    }
  }

}

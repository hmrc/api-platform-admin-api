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

package uk.gov.hmrc.apiplatformadminapi.services

import scala.concurrent.ExecutionContext.Implicits.global

import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatform.modules.tpd.test.data.UserTestData
import uk.gov.hmrc.apiplatform.modules.tpd.test.utils.LocalUserIdTracker
import uk.gov.hmrc.apiplatformadminapi.mocks.ThirdPartyOrchestratorConnectorMockModule
import uk.gov.hmrc.apiplatformadminapi.utils.AsyncHmrcSpec

class UsersServiceSpec extends AsyncHmrcSpec with ThirdPartyOrchestratorConnectorMockModule with UserTestData with LocalUserIdTracker {

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val underTest = new UsersService(mockThirdPartyOrchestratorConnector)

    val sessionId = UserSessionId.random
  }

  "getUserBySessionId" should {
    "return a User with the requested sessionId" in new Setup {
      GetBySessionId.returns(standardDeveloper)

      val result = await(underTest.getUserBySessionId(sessionId))

      result shouldBe Some(standardDeveloper)
      GetBySessionId.verifyCalledWith(sessionId)
    }

    "return None if the user was not found" in new Setup {
      GetBySessionId.returnsNone()

      val result = await(underTest.getUserBySessionId(sessionId))

      result shouldBe None
      GetBySessionId.verifyCalledWith(sessionId)
    }
  }
}

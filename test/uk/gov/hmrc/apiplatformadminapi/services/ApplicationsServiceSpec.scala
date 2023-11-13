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

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, Environment, LaxEmailAddress, UserId}
import uk.gov.hmrc.apiplatformadminapi.mocks.ThirdPartyOrchestratorConnectorMockModule
import uk.gov.hmrc.apiplatformadminapi.models.{Developer, FetchedApplication}
import uk.gov.hmrc.apiplatformadminapi.utils.AsyncHmrcSpec

class ApplicationsServiceSpec extends AsyncHmrcSpec with ThirdPartyOrchestratorConnectorMockModule {

  trait Setup {
    implicit val hc = HeaderCarrier()

    val underTest = new ApplicationsService(mockThirdPartyOrchestratorConnector)

    val applicationId = ApplicationId.random

    val application = FetchedApplication(
      applicationId,
      "name",
      Environment.PRODUCTION,
      Set(Developer(UserId.random, LaxEmailAddress("test@test.com"), "Barbara", "Liskov"))
    )
  }

  "getApplication" should {
    "return an application with the requested applicationId" in new Setup {
      GetApplication.returns(application)

      val result = await(underTest.getApplication(applicationId))

      result shouldBe Some(application)
      GetApplication.verifyCalledWith(applicationId)
    }

    "return None if the application was not found" in new Setup {
      GetApplication.returnsNone()

      val result = await(underTest.getApplication(applicationId))

      result shouldBe None
      GetApplication.verifyCalledWith(applicationId)
    }
  }
}

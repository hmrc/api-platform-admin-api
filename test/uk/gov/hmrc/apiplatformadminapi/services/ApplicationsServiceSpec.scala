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

import uk.gov.hmrc.apiplatformadminapi.mocks.ThirdPartyOrchestratorConnectorMockModule
import uk.gov.hmrc.apiplatformadminapi.utils.{ApplicationTestData, AsyncHmrcSpec}

class ApplicationsServiceSpec extends AsyncHmrcSpec with ApplicationTestData {

  trait Setup extends ThirdPartyOrchestratorConnectorMockModule {
    implicit val hc = HeaderCarrier()

    val underTest = new ApplicationsService(mockThirdPartyOrchestratorConnector)

  }

  "getApplication" should {
    "return an application with the requested applicationId" in new Setup {
      GetApplication.returns(applicationResponse)
      GetApplicationDevelopers.returns(developers)

      val result = await(underTest.getApplicationWithUsers(applicationId))

      result shouldBe Right(applicationWithUsers)
      GetApplication.verifyCalledWith(applicationId)
      GetApplicationDevelopers.verifyCalledWith(applicationId)
    }

    "return an application with no developers" in new Setup {
      GetApplication.returns(applicationResponse.copy(collaborators = Set.empty))
      GetApplicationDevelopers.returnsNoDevelopers()

      val result = await(underTest.getApplicationWithUsers(applicationId))

      result shouldBe Right(applicationWithUsers.copy(users = Set.empty))
      GetApplication.verifyCalledWith(applicationId)
      GetApplicationDevelopers.verifyCalledWith(applicationId)
    }

    "return None if the application was not found" in new Setup {
      GetApplication.returnsNone()

      val result = await(underTest.getApplicationWithUsers(applicationId))

      result shouldBe Left("Application could not be found")
      GetApplication.verifyCalledWith(applicationId)
      GetApplicationDevelopers.verifyNotCalled()
    }
  }
}

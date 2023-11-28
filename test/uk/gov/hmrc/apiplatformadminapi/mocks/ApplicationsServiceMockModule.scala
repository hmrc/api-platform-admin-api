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

package uk.gov.hmrc.apiplatformadminapi.mocks

import scala.concurrent.Future

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatformadminapi.models.ApplicationWithUsers
import uk.gov.hmrc.apiplatformadminapi.services.ApplicationsService

trait ApplicationsServiceMockModule extends MockitoSugar with ArgumentMatchersSugar {

  val mockApplicationsService = mock[ApplicationsService]

  object GetApplicationWithUsers {

    def returns(application: ApplicationWithUsers) =
      when(mockApplicationsService.getApplicationWithUsers(*[ApplicationId])(*)).thenReturn(Future.successful(Right(application)))

    def returnsNotFound() =
      when(mockApplicationsService.getApplicationWithUsers(*[ApplicationId])(*)).thenReturn(Future.successful(Left("Application could not be found")))

    def fails() =
      when(mockApplicationsService.getApplicationWithUsers(*[ApplicationId])(*)).thenReturn(Future.failed(new Exception("bang")))

    def verifyCalledWith(applicationId: ApplicationId) =
      verify(mockApplicationsService).getApplicationWithUsers(eqTo(applicationId))(*)
  }
}

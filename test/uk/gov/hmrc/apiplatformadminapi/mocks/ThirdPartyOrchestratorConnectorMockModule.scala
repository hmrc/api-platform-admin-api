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

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithCollaborators
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId}
import uk.gov.hmrc.apiplatform.modules.tpd.core.domain.models.User
import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatformadminapi.connectors.ThirdPartyOrchestratorConnector

trait ThirdPartyOrchestratorConnectorMockModule extends MockitoSugar with ArgumentMatchersSugar {

  val mockThirdPartyOrchestratorConnector = mock[ThirdPartyOrchestratorConnector]

  object GetApplication {

    def returns(application: ApplicationWithCollaborators) =
      when(mockThirdPartyOrchestratorConnector.getApplication(*[ApplicationId])(*)).thenReturn(Future.successful(Some(application)))

    def returnsNone() =
      when(mockThirdPartyOrchestratorConnector.getApplication(*[ApplicationId])(*)).thenReturn(Future.successful(None))

    def verifyCalledWith(applicationId: ApplicationId) =
      verify(mockThirdPartyOrchestratorConnector).getApplication(eqTo(applicationId))(*)
  }

  object GetApplicationByClientId {

    def returns(application: ApplicationWithCollaborators) =
      when(mockThirdPartyOrchestratorConnector.getApplicationByClientId(*[ClientId])(*)).thenReturn(Future.successful(Some(application)))

    def returnsNone() =
      when(mockThirdPartyOrchestratorConnector.getApplicationByClientId(*[ClientId])(*)).thenReturn(Future.successful(None))

    def verifyCalledWith(clientId: ClientId) =
      verify(mockThirdPartyOrchestratorConnector).getApplicationByClientId(eqTo(clientId))(*)
  }

  object GetApplicationDevelopers {

    def returns(users: Set[User]) =
      when(mockThirdPartyOrchestratorConnector.getApplicationDevelopers(*[ApplicationId])(*)).thenReturn(Future.successful(users))

    def returnsNoDevelopers() =
      when(mockThirdPartyOrchestratorConnector.getApplicationDevelopers(*[ApplicationId])(*)).thenReturn(Future.successful(Set.empty))

    def verifyCalledWith(applicationId: ApplicationId) =
      verify(mockThirdPartyOrchestratorConnector).getApplicationDevelopers(eqTo(applicationId))(*)

    def verifyNotCalled() =
      verify(mockThirdPartyOrchestratorConnector, never).getApplicationDevelopers(*[ApplicationId])(*)
  }

  object GetBySessionId {

    def returns(developer: User) =
      when(mockThirdPartyOrchestratorConnector.getBySessionId(*[UserSessionId])(*)).thenReturn(Future.successful(Some(developer)))

    def returnsNone() =
      when(mockThirdPartyOrchestratorConnector.getBySessionId(*[UserSessionId])(*)).thenReturn(Future.successful(None))

    def verifyCalledWith(sessionId: UserSessionId) =
      verify(mockThirdPartyOrchestratorConnector).getBySessionId(eqTo(sessionId))(*)
  }
}

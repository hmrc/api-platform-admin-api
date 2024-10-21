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

import uk.gov.hmrc.apiplatform.modules.tpd.core.domain.models.User
import uk.gov.hmrc.apiplatform.modules.tpd.session.domain.models.UserSessionId
import uk.gov.hmrc.apiplatformadminapi.services.UsersService

trait UsersServiceMockModule extends MockitoSugar with ArgumentMatchersSugar {

  val mockService = mock[UsersService]

  object GetUserBySessionId {

    def returns(user: User) =
      when(mockService.getUserBySessionId(*[UserSessionId])(*)).thenReturn(Future.successful(Some(user)))

    def returnsNone() =
      when(mockService.getUserBySessionId(*[UserSessionId])(*)).thenReturn(Future.successful(None))

    def fails() =
      when(mockService.getUserBySessionId(*[UserSessionId])(*)).thenReturn(Future.failed(new Exception("bang")))

    def verifyCalledWith(sessionId: UserSessionId) =
      verify(mockService).getUserBySessionId(eqTo(sessionId))(*)
  }
}

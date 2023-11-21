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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import uk.gov.hmrc.apiplatform.modules.developers.domain.models.SessionId
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apiplatformadminapi.connectors.ThirdPartyOrchestratorConnector
import uk.gov.hmrc.apiplatformadminapi.models._

@Singleton
class UsersService @Inject() (thirdPartyOrchestratorConnector: ThirdPartyOrchestratorConnector)(implicit val ec: ExecutionContext) {

  def getUserBySessionId(sessionId: SessionId)(implicit hc: HeaderCarrier): Future[Option[Developer]] = {
    thirdPartyOrchestratorConnector.getBySessionId(sessionId)
  }
}

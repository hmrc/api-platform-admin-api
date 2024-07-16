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

package uk.gov.hmrc.apiplatformadminapi.connectors

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationResponse
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId}
import uk.gov.hmrc.apiplatform.modules.developers.domain.models.{Developer, SessionId}
import uk.gov.hmrc.apiplatformadminapi.models.UserRequest

@Singleton
class ThirdPartyOrchestratorConnector @Inject() (http: HttpClientV2, config: ThirdPartyOrchestratorConnector.Config)(implicit ec: ExecutionContext) {

  def getApplication(applicationId: ApplicationId)(implicit hc: HeaderCarrier): Future[Option[ApplicationResponse]] = {
    http.get(url"${config.serviceBaseUrl}/applications/$applicationId")
      .execute[Option[ApplicationResponse]]
  }

  def getApplicationByClientId(clientId: ClientId)(implicit hc: HeaderCarrier): Future[Option[ApplicationResponse]] = {
    http.get(url"${config.serviceBaseUrl}/applications?clientId=${clientId.value}")
      .execute[Option[ApplicationResponse]]
  }

  def getApplicationDevelopers(applicationId: ApplicationId)(implicit hc: HeaderCarrier): Future[Set[Developer]] = {
    http.get(url"${config.serviceBaseUrl}/applications/$applicationId/developers").execute[Set[Developer]]
  }

  def getBySessionId(sessionId: SessionId)(implicit hc: HeaderCarrier): Future[Option[Developer]] = {
    http.post(url"${config.serviceBaseUrl}/session/validate")
      .withBody(Json.toJson(UserRequest(sessionId)))
      .execute[Option[Developer]]
  }
}

object ThirdPartyOrchestratorConnector {
  case class Config(serviceBaseUrl: String)
}

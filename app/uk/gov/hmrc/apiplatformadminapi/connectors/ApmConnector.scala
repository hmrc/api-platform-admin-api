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

import play.api.libs.json.OFormat
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import uk.gov.hmrc.apiplatform.modules.apis.domain.models._

@Singleton
class ApmConnector @Inject() (http: HttpClientV2, config: ApmConnector.Config)(implicit ec: ExecutionContext) {

  def fetchApi(serviceName: ServiceName)(implicit hc: HeaderCarrier): Future[Option[Locator[ApiDefinition]]] = {
    implicit val formatter: OFormat[Locator[ApiDefinition]] = Locator.buildLocatorFormatter[ApiDefinition]

    http.get(url"${config.serviceBaseUrl}/api-definitions/service-name/$serviceName")
      .execute[Option[Locator[ApiDefinition]]]
  }
}

object ApmConnector {

  case class Config(
      serviceBaseUrl: String
    )
}

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

package uk.gov.hmrc.apiplatformadminapi.models

import play.api.libs.json.{JsObject, Json, OFormat}
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._

import uk.gov.hmrc.apiplatform.modules.common.domain.models._

case class EndpointResponse(uriPattern: String, method: HttpMethod)

object EndpointResponse {
  implicit val endpointFormat: OFormat[EndpointResponse] = Json.format[EndpointResponse]

  def from(endpoint: Endpoint): EndpointResponse = {
    EndpointResponse(endpoint.uriPattern, endpoint.method)
  }
}

case class VersionResponse(versionNbr: ApiVersionNbr, status: ApiStatus, endpoints: List[EndpointResponse])

object VersionResponse {
  implicit val apiFormat: OFormat[VersionResponse] = Json.format[VersionResponse]

  def from(apiVersion: ApiVersion): VersionResponse = {
    VersionResponse(apiVersion.versionNbr, apiVersion.status, apiVersion.endpoints.map(EndpointResponse.from))
  }

  def from(versions: Map[ApiVersionNbr, ApiVersion]): Map[ApiVersionNbr, VersionResponse] = {
    versions.map(version => version._1 -> from(version._2))
  }
}

case class ApiResponse(serviceName: ServiceName, apiContext: ApiContext, name: String, description: String, versions: Map[ApiVersionNbr, VersionResponse]) {
  def asJson: JsObject = ApiResponse.apiFormat.writes(this)
}

object ApiResponse {
  implicit val apiFormat: OFormat[ApiResponse] = Json.format[ApiResponse]

  def from(apiDefinition: ApiDefinition): ApiResponse = {
    ApiResponse(apiDefinition.serviceName, apiDefinition.context, apiDefinition.name, apiDefinition.description, VersionResponse.from(apiDefinition.versions))
  }

}

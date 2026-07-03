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

package uk.gov.hmrc.apiplatformadminapi.utils

import uk.gov.hmrc.apiplatform.modules.apis.domain.models.*
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, ApiVersionNbr}
import uk.gov.hmrc.apiplatformadminapi.models.*

trait ApiTestData {
  val aServiceName: ServiceName = ServiceName("hello-world")

  private val apiContext: ApiContext    = ApiContext("hello")
  private val name                      = Endpoint.Name("hello")
  private val description               = "a default def"
  private val versionNbr: ApiVersionNbr = ApiVersionNbr("1.0")
  private val uriPattern                = Endpoint.UriPattern("/hello/{user}")

  private val endpoint: Endpoint =
    Endpoint(uriPattern, Endpoint.Name("hello user"), HttpMethod.Get, AuthType.User, ResourceThrottlingTier.Unlimited, None, List(QueryParameter("user", true)))
  val apiVersions                = Map(versionNbr -> ApiVersion(versionNbr, ApiStatus.Stable, ApiAccessType.Public, List(endpoint), true, None, ApiVersionSource.OAS))
  val apiVersionResponse         = Map(versionNbr -> VersionResponse(versionNbr, ApiStatus.Stable, List(EndpointResponse(uriPattern, HttpMethod.Get))))

  val anApiDefinition   = ApiDefinition(
    aServiceName,
    ApiDefinition.ServiceBaseUrl("http://localhost"),
    ApiDefinition.Name(name),
    ApiDefinition.Description(description),
    apiContext,
    apiVersions,
    false,
    None,
    List.empty
  )
  val apiResponse       = ApiResponse(aServiceName, apiContext, name, description, apiVersionResponse)
  val anApiInBoth       = Locator.Both(anApiDefinition, anApiDefinition)
  val anApiInSandbox    = Locator.Sandbox(anApiDefinition)
  val anApiInProduction = Locator.Production(anApiDefinition)

}

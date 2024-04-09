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

import java.time.Instant

import uk.gov.hmrc.apiplatform.modules.applications.access.domain.models.Access
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._

trait ApplicationBuilder {

  val instant = Instant.now

  def buildApplication(applicationId: ApplicationId, clientId: ClientId, applicationName: String, environment: Environment): ApplicationResponse = {
    ApplicationResponse(
      id = applicationId,
      clientId = clientId,
      gatewayId = "gateway-id",
      name = applicationName,
      deployedTo = environment,
      description = None,
      collaborators = Set.empty,
      createdOn = instant,
      lastAccess = None,
      grantLength = GrantLength.ONE_MONTH,
      lastAccessTokenUsage = None,
      termsAndConditionsUrl = None,
      privacyPolicyUrl = None,
      access = Access.Standard(),
      state = ApplicationState(name = State.TESTING, updatedOn = instant),
      rateLimitTier = RateLimitTier.BRONZE,
      checkInformation = None,
      blocked = false,
      trusted = false,
      ipAllowlist = IpAllowlist(required = false, Set.empty),
      moreApplication = MoreApplication(false)
    )
  }
}

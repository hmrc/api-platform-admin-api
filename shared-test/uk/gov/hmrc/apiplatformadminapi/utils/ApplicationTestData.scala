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

import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatformadminapi.models._

trait ApplicationTestData extends ApplicationBuilder with UserTestData {
  val applicationId       = ApplicationId.random
  val clientId            = ClientId.random
  val appName             = "Application Name"
  val environment         = Environment.PRODUCTION
  val applicationResponse = buildApplication(applicationId, clientId, appName, environment)

  val developers = Set(developer)

  val applicationWithUsers = ApplicationWithUsers(
    applicationId,
    appName,
    environment,
    users = developers.map(User.from)
  )

  val application = Application(
    applicationId,
    appName,
    environment
  )

  val applications = Applications(List(application))
}

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

import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatform.modules.common.services.EitherTHelper
import uk.gov.hmrc.apiplatformadminapi.connectors.ThirdPartyOrchestratorConnector
import uk.gov.hmrc.apiplatformadminapi.models.ApplicationWithUsers

@Singleton
class ApplicationsService @Inject() (thirdPartyOrchestratorConnector: ThirdPartyOrchestratorConnector)(implicit val ec: ExecutionContext) {

  private val E = EitherTHelper.make[String]

  def getApplicationWithUsers(applicationId: ApplicationId)(implicit hc: HeaderCarrier): Future[Either[String, ApplicationWithUsers]] = {
    (
      for {
        applicationResponse <- E.fromOptionF(thirdPartyOrchestratorConnector.getApplication(applicationId), "Application could not be found")
        developers          <- E.liftF(thirdPartyOrchestratorConnector.getApplicationDevelopers(applicationId))
      } yield ApplicationWithUsers.from(applicationResponse, developers)
    )
      .value
  }
}

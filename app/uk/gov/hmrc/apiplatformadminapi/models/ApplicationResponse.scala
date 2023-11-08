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

import play.api.libs.json.{JsValue, Json, OFormat}

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, Environment, LaxEmailAddress, UserId}

case class User(userId: UserId, email: LaxEmailAddress, firstName: String, lastName: String)

object User {

  def from(developer: Developer) = User(
    userId = developer.userId,
    email = developer.email,
    firstName = developer.firstName,
    lastName = developer.lastName
  )

  implicit val format = Json.format[User]
}

case class ApplicationResponse(applicationId: ApplicationId, name: String, environment: Environment, users: Set[User]) {
  def asJson: JsValue = ApplicationResponse.format.writes(this)
}

object ApplicationResponse {

  def from(application: FetchedApplication) = ApplicationResponse(
    applicationId = application.applicationId,
    name = application.name,
    environment = application.environment,
    users = application.developers.map(User.from)
  )

  implicit val format: OFormat[ApplicationResponse] = Json.format[ApplicationResponse]
}

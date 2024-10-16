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

import play.api.libs.json.{Format, JsValue, Json, OFormat}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.{ApplicationName, ApplicationWithCollaborators}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, Environment, LaxEmailAddress, UserId}
import uk.gov.hmrc.apiplatform.modules.tpd.core.domain.models.User

case class UserResponse(userId: UserId, email: LaxEmailAddress, firstName: String, lastName: String) {
  def asJson: JsValue = UserResponse.format.writes(this)
}

object UserResponse {

  def from(user: User) = UserResponse(
    userId = user.userId,
    email = user.email,
    firstName = user.firstName,
    lastName = user.lastName
  )

  implicit val format: Format[UserResponse] = Json.format[UserResponse]
}

case class ApplicationWithUsers(applicationId: ApplicationId, name: ApplicationName, environment: Environment, users: Set[UserResponse]) {
  def asJson: JsValue = ApplicationWithUsers.format.writes(this)
}

object ApplicationWithUsers {

  def from(applicationResponse: ApplicationWithCollaborators, users: Set[User]) = ApplicationWithUsers(
    applicationId = applicationResponse.id,
    name = applicationResponse.name,
    environment = applicationResponse.deployedTo,
    users = users.map(UserResponse.from)
  )

  implicit val format: OFormat[ApplicationWithUsers] = Json.format[ApplicationWithUsers]
}

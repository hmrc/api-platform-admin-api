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

package uk.gov.hmrc.apiplatformadminapi.controllers

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import uk.gov.hmrc.apiplatformadminapi.models.{UserRequest, _}
import uk.gov.hmrc.apiplatformadminapi.services.UsersService

@Singleton()
class UsersController @Inject() (usersService: UsersService, cc: ControllerComponents, auth: BackendAuthComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) with JsonUtils {

  def userQuery(): Action[AnyContent] =
    auth.authorizedAction(predicate = Predicate.Permission(Resource.from("api-platform-admin-api", "users/all"), IAAction("READ"))).async {
      implicit request: AuthenticatedRequest[AnyContent, Unit] =>
        withJsonBodyFromAnyContent[UserRequest] { userRequest =>
          {
            usersService.getUserBySessionId(userRequest.sessionId) map {
              case Some(developer) => Ok(User.from(developer).asJson)
              case _               => NotFound(ErrorResponse("NOT_FOUND", "User could not be found").asJson)
            } recover recovery
          }
        }
    }
}

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
import scala.concurrent.{ExecutionContext, Future}

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApplicationId, ClientId}
import uk.gov.hmrc.apiplatformadminapi.models.{Applications, ErrorResponse}
import uk.gov.hmrc.apiplatformadminapi.services.ApplicationsService

@Singleton()
class ApplicationsController @Inject() (applicationsService: ApplicationsService, cc: ControllerComponents, auth: BackendAuthComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getApplication(applicationId: ApplicationId): Action[AnyContent] =
    auth.authorizedAction(predicate = Predicate.Permission(Resource.from("api-platform-admin-api", "applications/all"), IAAction("READ"))).async {
      implicit request: AuthenticatedRequest[AnyContent, Unit] =>
        applicationsService.getApplicationWithUsers(applicationId).map {
          case Right(applicationWithUsers) => Ok(applicationWithUsers.asJson)
          case Left(message)               => NotFound(ErrorResponse("NOT_FOUND", message).asJson)
        } recover recovery
    }

  def getApplicationsByQueryParam(): Action[AnyContent] =
    auth.authorizedAction(predicate = Predicate.Permission(Resource.from("api-platform-admin-api", "applications/all"), IAAction("READ"))).async {
      implicit request: AuthenticatedRequest[AnyContent, Unit] =>
        request.queryString.toList.sortBy(_._1) match {
          case ("clientId", clientIds) :: _ =>
            applicationsService.getApplicationByClientId(ClientId(clientIds.head)).map {
              case Some(application) => Ok(Applications(List(application)).asJson)
              case None              => NotFound(ErrorResponse("NOT_FOUND", "Application could not be found").asJson)
            } recover recovery
          case _                            => Future.successful(BadRequest(ErrorResponse("BAD_REQUEST", "Invalid or missing query parameters").asJson))
        }
    }
}

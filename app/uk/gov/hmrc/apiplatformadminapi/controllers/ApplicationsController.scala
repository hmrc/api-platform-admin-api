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
import scala.util.control.NonFatal

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId
import uk.gov.hmrc.apiplatformadminapi.models.ErrorResponse
import uk.gov.hmrc.apiplatformadminapi.services.ApplicationsService
import uk.gov.hmrc.apiplatformadminapi.utils.ApplicationLogger

@Singleton()
class ApplicationsController @Inject() (applicationsService: ApplicationsService, cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) with ApplicationLogger {

  def getApplication(applicationId: ApplicationId): Action[AnyContent] = Action.async { implicit request =>
    applicationsService.getApplicationWithUsers(applicationId).map {
      case Right(applicationWithUsers) => Ok(applicationWithUsers.asJson)
      case Left(message)               => NotFound(ErrorResponse("NOT_FOUND", message).asJson)
    } recover {
      case NonFatal(e) =>
        val message = s"An unexpected error occurred: ${e.getMessage}"
        logger.error(message)
        InternalServerError(ErrorResponse("INTERNAL_SERVER_ERROR", message).asJson)
    }
  }
}

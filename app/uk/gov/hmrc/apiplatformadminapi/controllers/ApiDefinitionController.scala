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

import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.apiplatformadminapi.models.{ApiResponse, ErrorResponse}
import uk.gov.hmrc.apiplatformadminapi.services.ApisService
import uk.gov.hmrc.apiplatformadminapi.utils.ApplicationLogger

@Singleton()
class ApiDefinitionController @Inject() (apisService: ApisService, cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) with ApplicationLogger {
  private lazy val notFound = NotFound(ErrorResponse("NOT_FOUND", "API could not be found").asJson)

  def fetch(serviceName: ServiceName, environment: Environment): Action[AnyContent] = Action.async { implicit request =>
    apisService.fetchApi(serviceName) map {
      case Some(service) if environment == Environment.SANDBOX    => service.sandbox.map(apiDef => Ok(ApiResponse.from(apiDef).asJson)).getOrElse(notFound)
      case Some(service) if environment == Environment.PRODUCTION => service.production.map(apiDef => Ok(ApiResponse.from(apiDef).asJson)).getOrElse(notFound)
      case _                                                      => notFound
    } recover {
      case NonFatal(e) =>
        val message = s"An unexpected error occurred: ${e.getMessage}"
        logger.error(message)
        InternalServerError(ErrorResponse("INTERNAL_SERVER_ERROR", message).asJson)
    }
  }
}

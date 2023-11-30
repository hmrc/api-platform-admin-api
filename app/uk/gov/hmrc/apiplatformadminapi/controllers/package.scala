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

package uk.gov.hmrc.apiplatformadminapi

import scala.util.control.NonFatal

import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._

import uk.gov.hmrc.apiplatformadminapi.models.ErrorResponse

package object controllers {

  val logger = Logger("controllers")

  def recovery: PartialFunction[Throwable, Result] = {
    case NonFatal(e) =>
      val message = s"An unexpected error occurred: ${e.getMessage}"
      logger.error(message)
      InternalServerError(ErrorResponse("INTERNAL_SERVER_ERROR", message).asJson)
  }
}

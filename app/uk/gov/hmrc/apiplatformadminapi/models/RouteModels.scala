/*
 * Copyright 2026 HM Revenue & Customs
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

import java.util.UUID
import scala.util.control.Exception.allCatch

import play.api.Logger
import play.api.mvc.PathBindable

import uk.gov.hmrc.apiplatform.modules.apis.domain.models.ServiceName
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId

object RouteModels {
  case class SimpleApplicationId(value: UUID) extends AnyVal
  type SimpleServiceName = String

  val logger = Logger("RoutesModels")

  private def applicationIdFromString(text: String): Either[String, SimpleApplicationId] = {
    allCatch.opt(ApplicationId.unsafeApply(text))
      .toRight({
        logger.info("Cannot parse parameter %s as ApplicationId".format(text))
        "applicationId is not a UUID"
      })
      .map(x => SimpleApplicationId(x.value))
  }

  implicit def applicationIdPathBindable(implicit textBinder: PathBindable[String]): PathBindable[SimpleApplicationId] =
    new PathBindable[SimpleApplicationId] {

      override def bind(key: String, value: String): Either[String, SimpleApplicationId] = {
        textBinder.bind(key, value).flatMap(applicationIdFromString)
      }

      override def unbind(key: String, id: SimpleApplicationId): String = {
        textBinder.unbind(key, id.toString)
      }
    }

  object Conversions {

    given Conversion[SimpleApplicationId, ApplicationId] with
      def apply(x: SimpleApplicationId): ApplicationId = ApplicationId(x.value)

    given Conversion[SimpleServiceName, ServiceName] with
      def apply(x: SimpleServiceName): ServiceName = ServiceName(x)
  }
}

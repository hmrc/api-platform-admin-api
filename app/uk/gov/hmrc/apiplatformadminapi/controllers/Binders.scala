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

import java.util.UUID
import scala.util.control.Exception.allCatch

import play.api.Logger
import play.api.mvc.PathBindable

import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApplicationId

object Binders {
  val logger = Logger("binders")

  private def applicationIdFromString(text: String): Either[String, ApplicationId] = {
    allCatch.opt(ApplicationId(UUID.fromString(text)))
      .toRight({
        logger.info("Cannot parse parameter %s as ApplicationId".format(text))
        "applicationId is not a UUID"
      })
  }

  implicit def applicationIdPathBindable(implicit textBinder: PathBindable[String]): PathBindable[ApplicationId] =
    new PathBindable[ApplicationId] {

      override def bind(key: String, value: String): Either[String, ApplicationId] = {
        textBinder.bind(key, value).flatMap(applicationIdFromString)
      }
      // $COVERAGE-OFF$

      override def unbind(key: String, applicationId: ApplicationId): String = {
        textBinder.unbind(key, applicationId.toString)
      }
      // $COVERAGE-ON$
    }

//  For next ticket
//  implicit def clientIdQueryStringBindable(implicit textBinder: QueryStringBindable[String]): QueryStringBindable[ClientId] =
//    new QueryStringBindable[ClientId] {
//
//      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ClientId]] = {
//        textBinder.bind(key, params).map {
//          case Right(clientId) => Right(ClientId(clientId))
//          case _               => Left("Unable to bind clientId")
//        }
//      }
//
//      override def unbind(key: String, clientId: ClientId): String = {
//        textBinder.unbind(key, clientId.toString)
//      }
//    }
}

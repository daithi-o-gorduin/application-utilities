/*
 *  Copyright 2018 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cjwwdev.identifiers

import java.util.UUID

import com.cjwwdev.logging.Logging
import com.cjwwdev.responses.ApiResponse
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.NotAcceptable
import play.api.http.Status.NOT_ACCEPTABLE

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait IdentifierValidation extends Logging with ApiResponse {
  val CONTEXT   = "context"
  val SESSION   = "session"
  val FEED_ITEM = "feed-item"
  val USER      = "user"
  val ORG_USER  = "org-user"
  val DIAG      = "diag"
  val DEVERSITY = "deversity"

  def validateAs(prefix: String, id: String)(f: => Future[Result])(implicit request: Request[_]): Future[Result] = {
    if(id.contains(prefix)) {
      Try(UUID.fromString(id.replace(s"$prefix-", ""))) match {
        case Success(_) => f
        case Failure(_) =>
          logger.warn("[validateAs] - Given identifier was invalid")
          withJsonResponseBody(NOT_ACCEPTABLE, s"$id is not a valid identifier") { json =>
            NotAcceptable(json)
          }
      }
    } else {
      logger.warn("[validateAs] - Couldn't validate the given identifier against the specified prefix")
      withJsonResponseBody(NOT_ACCEPTABLE, s"Could not validate $id as a $prefix id") { json =>
        NotAcceptable(json)
      }
    }
  }
}

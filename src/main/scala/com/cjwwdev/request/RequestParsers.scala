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

package com.cjwwdev.request

import com.cjwwdev.responses.ApiResponse
import com.cjwwdev.security.encryption.DataSecurity
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.BadRequest
import play.api.http.Status.BAD_REQUEST

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import scala.language.reflectiveCalls

trait RequestParsers extends ApiResponse {

  private val logger = LoggerFactory.getLogger(getClass)

  def withJsonBody[T](reads: Reads[T])(f: T => Future[Result])(implicit request: Request[String]): Future[Result] = {
    Try(DataSecurity.decryptIntoType[T](request.body)(reads)) match {
      case Success(jsResult) => jsResult match {
        case JsSuccess(typeT,_) => f(typeT)
        case JsError(errors)    =>
          logger.error(s"Couldn't validate json as specified structure - ${Json.prettyPrint(JsError.toJson(errors))}")
          withFutureJsonResponseBody(BAD_REQUEST, JsError.toJson(errors), "Couldn't validate json as specified structure") { json =>
            Future(BadRequest(json))
          }
      }
      case Failure(_) =>
        logger.error(s"Couldn't decrypt request body on ${request.path}")
        withFutureJsonResponseBody(BAD_REQUEST, s"Couldn't decrypt request body on ${request.path}") { json =>
          Future(BadRequest(json))
        }
    }
  }

  def withEncryptedUrl(enc: String)(f: String => Future[Result])(implicit request: Request[_]): Future[Result] = {
    Try(DataSecurity.decryptString(enc)) match {
      case Success(result) => f(result)
      case Failure(_)      =>
        logger.error(s"[withJsonBody] - decryption failed")
        withFutureJsonResponseBody(BAD_REQUEST, "Could not decrypt given url") { json =>
          Future(BadRequest(json))
        }
    }
  }

  def withEncryptedUrlIntoType[T](enc: String, reads: Reads[T])(f: T => Future[Result])(implicit request: Request[_]): Future[Result] = {
    Try(DataSecurity.decryptIntoType[T](enc)(reads)) match {
      case Success(jsResult) => jsResult match {
        case JsSuccess(typeT,_) => f(typeT)
        case JsError(errors)    =>
          logger.error(s"Couldn't validate json as specified structure - ${Json.prettyPrint(JsError.toJson(errors))}")
          withFutureJsonResponseBody(BAD_REQUEST, JsError.toJson(errors), "Couldn't validate json as specified structure") { json =>
            Future(BadRequest(json))
          }
      }
      case Failure(_) =>
        logger.error(s"Couldn't decrypt request on ${request.path}")
        withFutureJsonResponseBody(BAD_REQUEST, s"Couldn't decrypt request body on ${request.path}") { json =>
          Future(BadRequest(json))
        }
    }
  }
}

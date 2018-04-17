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

package com.cjwwdev.responses

import org.joda.time.LocalDateTime
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc.{Request, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

trait ApiResponse {
  implicit def strToJsString(body: String): JsValue = JsString(body)

  implicit class IntOps(int: Int) {
    def isBetween(range: Range): Boolean = range contains int
  }

  private def requestProperties(statusCode: Int)(implicit request: Request[_]): JsObject = Json.obj(
    "uri"    -> s"${request.uri}",
    "method" -> s"${request.method.toUpperCase}",
    "status" -> statusCode
  )

  private def requestStats(implicit request: Request[_]): JsObject = Json.obj(
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  )

  def withJsonResponseBody(statusCode: Int, body: JsValue)(jsBody: JsValue => Result)(implicit request: Request[_]): Future[Result] = {
    val bodyKey = if(statusCode.isBetween(200 to 299)) "body" else "errorBody"
    Future(jsBody(requestProperties(statusCode) ++ Json.obj(bodyKey -> body) ++ requestStats))
  }

  def withJsonResponseBody(statusCode: Int, body: JsValue, errorMessage: String)
                          (result: JsValue => Result)
                          (implicit request: Request[_]): Future[Result] = {
    Future(result(requestProperties(statusCode) ++
      Json.obj(
        "errorMessage" -> errorMessage,
        "errorBody"    -> body
      ) ++
      requestStats
    ))
  }
}

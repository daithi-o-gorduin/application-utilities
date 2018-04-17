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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Results.{Ok, InternalServerError, BadRequest}

import scala.concurrent.Future

class ApiResponseSpec extends PlaySpec {
  object TestResponse extends ApiResponse

  implicit val request = FakeRequest()

  "jsonResponse" should {
    "a successful ApiResponse" in {
      val result = contentAsJson(TestResponse.withJsonResponseBody(OK, JsString("test"))(x => Ok(x)))
      result.\("uri").as[String]    mustBe "/"
      result.\("method").as[String] mustBe "GET"
      result.\("status").as[Int]    mustBe OK
      result.\("body").as[String]   mustBe "test"
    }

    "an unsuccessful ApiResponse (INS)" in {
      val result = contentAsJson(TestResponse.withJsonResponseBody(INTERNAL_SERVER_ERROR, JsString("test"))(x => InternalServerError(x)))
      result.\("uri").as[String]          mustBe "/"
      result.\("method").as[String]       mustBe "GET"
      result.\("status").as[Int]          mustBe INTERNAL_SERVER_ERROR
      result.\("errorBody").as[String]    mustBe "test"
    }

    "an unsuccessful ApiResponse (BAD REQUEST)" in {
      val result = contentAsJson(TestResponse.withJsonResponseBody(BAD_REQUEST, JsString("test"))(x => BadRequest(x)))
      result.\("uri").as[String]          mustBe "/"
      result.\("method").as[String]       mustBe "GET"
      result.\("status").as[Int]          mustBe BAD_REQUEST
      result.\("errorBody").as[String]    mustBe "test"
    }
  }
}

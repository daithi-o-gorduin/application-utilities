// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cjwwdev.request

import com.cjwwdev.fixtures.TestModel
import com.cjwwdev.security.encryption.DataSecurity
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Format, Json}
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class RequestParsersSpec extends PlaySpec with GuiceOneAppPerSuite {

  val testParsers = new RequestParsers {}

  val testEncString: String = DataSecurity.encryptString("testString").get

  val date = "$date"
  final val now = new DateTime(DateTimeZone.UTC)
  val testModel = TestModel("testString", 616, now)
  val testEncModel: String = DataSecurity.encryptType[TestModel](testModel).get

  def okFunction[T](data: T)(implicit format: Format[T]): Future[Result] = Future.successful(Ok(Json.toJson[T](data)))

  "decryptRequest" should {
    "return an Ok" when {
      "decrypting the request body was successful" in {
        implicit val request = FakeRequest().withBody[String](testEncModel)
        val result = testParsers.decryptRequest[TestModel](TestModel.standardFormat) { model =>
          okFunction(model)
        }
        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(
          s"""{
            | "string":"testString",
            | "int":616,
            | "dateTime":{
            |   "$date":${now.getMillis}
            | }
            |}""".stripMargin
        )
      }
    }

    "return a bad request" when {
      "there was a problem decrypting the request" in {
        implicit val request = FakeRequest().withBody[String](testEncString)
        val result = testParsers.decryptRequest[TestModel](TestModel.standardFormat) { model =>
          okFunction(model)
        }
        status(result) mustBe BAD_REQUEST
      }
    }
  }

  "decryptUrl" should {
    "return an Ok" when {
      "decryption was successful" in {
        implicit val request = FakeRequest()
        val result = testParsers.decryptUrl(testEncString) { str =>
          okFunction(str)
        }

        status(result) mustBe OK
        contentAsString(result) mustBe """"testString""""
      }
    }

    "return a bad request" when {
      "there was a problem decrypting the url" in {
        implicit val request = FakeRequest()
        val result = testParsers.decryptUrl("invalid_string") { str =>
          okFunction(str)
        }

        status(result) mustBe BAD_REQUEST
      }
    }
  }

  "decryptUrlIntoType" should {
    "return an Ok" when {
      "decryption was successful" in {
        implicit val request = FakeRequest()
        val result = testParsers.decryptUrlIntoType[TestModel](testEncModel)(TestModel.standardFormat) { model =>
          okFunction(model)
        }

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(
          s"""{
             | "string":"testString",
             | "int":616,
             | "dateTime":{
             |   "$date":${now.getMillis}
             | }
             |}""".stripMargin
        )
      }
    }

    "return a bad request" when {
      "there was a problem decrpyting the url" in {
        implicit val request = FakeRequest()
        val result = testParsers.decryptUrlIntoType[TestModel]("invalid_string")(TestModel.standardFormat) { model =>
          okFunction(model)
        }

        status(result) mustBe BAD_REQUEST
      }
    }
  }
}

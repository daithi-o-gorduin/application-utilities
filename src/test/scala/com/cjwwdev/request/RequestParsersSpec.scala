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

import com.cjwwdev.fixtures.{TestModel, TestModelTwo}
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

  val testEncString: String = DataSecurity.encryptString("testString")

  val now = new DateTime(DateTimeZone.UTC)
  val testModel = TestModel("testString", 616, now)
  val testEncModel: String = DataSecurity.encryptType[TestModel](testModel)

  val testInvalidEncModel = DataSecurity.encryptType(Json.parse(
    """
      |{
      | "boolean" : true
      |}
    """.stripMargin
  ))

  def okFunction[T](data: T)(implicit format: Format[T]): Future[Result] = Future.successful(Ok(Json.toJson[T](data)))

  "withJsonBody" should {
    "decrypt and parse the request body into a test model to return an OK" when {
      "the reads for test model is implicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testEncModel)

        val result = testParsers.withJsonBody[TestModel] { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe OK
      }

      "the reads for test model is explicitly passed to return an OK" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testEncModel)

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe OK
      }
    }

    "decrypt the request but return a bad request since the Json doesn't fit the specified type" when {
      "the reads for the type is implicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testInvalidEncModel)

        val result = testParsers.withJsonBody[TestModel] { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
      }

      "the reads for the type is explicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testInvalidEncModel)

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
      }
    }

    "fail to decrypt the request as the encrypted string was invalid" when {
      "the reads for the type is implicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody("INVALID_STRING")

        val result = testParsers.withJsonBody[TestModel] { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
      }

      "the reads for the type is explicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody("INVALID_STRING")

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
      }
    }
  }
}

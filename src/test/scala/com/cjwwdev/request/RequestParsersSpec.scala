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

  val now                  = new DateTime(DateTimeZone.UTC)
  val testModel            = TestModel("testString", 616, now)
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
      "the reads for test model is explicitly passed to return an OK" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testEncModel)

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe OK
      }
    }

    "decrypt the request but return a bad request since the Json doesn't fit the specified type" when {
      "the reads for the type is explicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody(testInvalidEncModel)

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
      }
    }

    "fail to decrypt the request as the encrypted string was invalid" when {
      "the reads for the type is explicitly passed" in {
        implicit val request: FakeRequest[String] = FakeRequest().withBody("INVALID_STRING")

        val result = testParsers.withJsonBody[TestModel](TestModel.standardFormat) { data =>
          okFunction[TestModel](data)
        }

        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe s"Couldn't decrypt request body on /"
      }
    }
  }

  "withEncryptedUrl" should {
    "decrypt the url and return an Ok" in {
      val result = testParsers.withEncryptedUrl(DataSecurity.encryptString("testString")) { url =>
        Future.successful(Ok(url))
      }

      status(result) mustBe OK
      contentAsString(result) mustBe "testString"
    }

    "return a BadRequest when the url can't be decrypted" in {
      val result = testParsers.withEncryptedUrl("testString") { url =>
        Future.successful(Ok(url))
      }

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe "Could not decrypt given url"
    }
  }

  "withEncryptedUrlIntoType" should {
    "decrypt the url into Type T" in {
      implicit val request: FakeRequest[String] = FakeRequest().withBody("")

      val enc = DataSecurity.encryptType[TestModel](testModel)

      val result = testParsers.withEncryptedUrlIntoType(enc, TestModel.standardFormat) { data =>
        okFunction(data)
      }

      status(result) mustBe OK
    }

    "return a BadRequest when the url can't be decrypted" in {
      implicit val request: FakeRequest[String] = FakeRequest().withBody("")

      val result = testParsers.withEncryptedUrlIntoType("testString", TestModel.standardFormat) { data =>
        okFunction(data)
      }

      status(result) mustBe BAD_REQUEST
    }
  }
}

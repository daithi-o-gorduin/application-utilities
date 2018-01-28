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

package com.cjwwdev.implicits

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, OFormat}

class ImplicitHandlersSpec extends PlaySpec {

  case class TestModel(string: String, int: Int)
  implicit val format: OFormat[TestModel] = Json.format[TestModel]

  val testModel = TestModel(
    string = "testString",
    int    = 616
  )

  "ImplicitDataSecurityHandlers" should {
    "encrypt a string" in new ImplicitHandlers {
      val result = "testString".encrypt
      assert(result != "testString")
    }

    "decrypt a string" in new ImplicitHandlers {
      val enc = "testString".encrypt
      val result = enc.decrypt

      assert(enc != "testString")
      assert(result == "testString")
    }

    "decrypt into a given type" in new ImplicitHandlers {
      val enc = testModel.encryptType
      val result = enc.decryptType[TestModel]

      enc.getClass mustBe classOf[String]
      result mustBe testModel
    }

    "fail decryption into a given type" in new ImplicitHandlers {
      val enc = testModel.encryptType

      intercept[NoSuchElementException](enc.decryptType[Int])
    }
  }

  "ImplicitGenericTypeHandler" should {
    "encrypt a given type" in new ImplicitHandlers {
      val result = testModel.encryptType
      result.getClass mustBe classOf[String]
      assert(result != testModel.toString)
    }
  }

  "ImplicitJsValueHandlers" should {
    val testJson     = Json.parse("""{"string" : "testString"}""")
    val testJsObject = Json.obj("string" -> "testString")

    "get should retrieve a value from the first level of a JsValue" in new ImplicitHandlers {
      val result = testJson.get[String]("string")
      result mustBe "testString"
    }

    "get should retrieve a value from the first level of a JsObject" in new ImplicitHandlers {
      val result = testJsObject.get[String]("string")
      result mustBe "testString"
    }

    "get should retrieve a value from the third level of a JsValue" in new ImplicitHandlers {
      val testJsonThreeLevels = Json.parse(
        """
          |{
          | "level-1" : {
          |   "level-2" : {
          |     "third" : "testString"
          |   }
          | }
          |}
        """.stripMargin
      )

      val result = testJsonThreeLevels.getFromLevel[String]("level-1", "level-2", "third")
      result mustBe "testString"
    }

    "get should throw a NoSuchElementException" when {
      "getting from a JsValue" in new ImplicitHandlers {
        intercept[NoSuchElementException](testJson.get[String]("invalidKey"))
      }

      "getting from a JsObject" in new ImplicitHandlers {
        intercept[NoSuchElementException](testJsObject.get[String]("invalidKey"))
      }
    }
  }

  "ImplicitJsLookupResultHandlers" should {
    "getOrThrow should return a type if Json validation has succeeded" in new ImplicitHandlers {
      val testJson = Json.parse(
        """
          |{
          | "string" : "testString",
          | "int" : 616
          |}
        """.stripMargin
      )

      val result = testJson.\("string").getOrThrow[String](new NoSuchElementException(s"No data found for key 'string'"))
      result mustBe "testString"
    }

    "throw an error if Json validation has failed" in new ImplicitHandlers {
      val testJson = Json.parse(
        """
          |{
          | "string" : "testString",
          | "int" : 616
          |}
        """.stripMargin
      )

      intercept[NoSuchElementException](testJson.\("invalid").getOrThrow[String](new NoSuchElementException(s"No data found for key 'string'")))
    }
  }
}

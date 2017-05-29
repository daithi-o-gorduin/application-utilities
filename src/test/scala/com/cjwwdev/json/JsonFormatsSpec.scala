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
package com.cjwwdev.json

import com.cjwwdev.fixtures.TestModel
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json, OFormat}

class JsonFormatsSpec extends PlaySpec with JsonFormats[TestModel] {

  override val standardFormat: OFormat[TestModel] = TestModel.standardFormat

  final val now = new DateTime(DateTimeZone.UTC)
  val date = "$date"

  val testModel = TestModel(
    string = "testString",
    int = 616,
    dateTime = now
  )

  val expectedJson = Json.parse(
    s"""
      |{
      | "string" : "testString",
      | "int" : 616,
      | "dateTime" : {
      |   "$date" : ${now.getMillis}
      | }
      |}
    """.stripMargin
  )

  "TestModel" should {
    "transform into json" in {
      Json.toJson[TestModel](testModel) mustBe expectedJson
    }

    "be created from json" in {
      Json.fromJson[TestModel](expectedJson) mustBe JsSuccess(testModel)
    }
  }
}

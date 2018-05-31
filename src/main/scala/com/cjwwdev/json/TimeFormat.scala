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

package com.cjwwdev.json

import java.time.LocalDateTime

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._

trait TimeFormat {
  implicit val dateTimeReadLDT: Reads[LocalDateTime] = Reads[LocalDateTime] {
    _.\("$date").validate[String] fold(JsError(_), str => JsSuccess(LocalDateTime.parse(str)))
  }

  implicit val dateTimeWriteLDT: Writes[LocalDateTime] = Writes[LocalDateTime] {
    ldt => Json.obj("$date" -> ldt.toString)
  }

  @deprecated("User dateTimeReadLDT", "2018-05-31")
  implicit val dateTimeRead: Reads[DateTime] = (__ \ "$date").read[Long] map {
    new DateTime(_, DateTimeZone.UTC)
  }

  @deprecated("User dateTimeWriteLDT", "2018-05-31")
  implicit val dateTimeWrite: Writes[DateTime] = Writes[DateTime] {
    dateTime => Json.obj("$date" -> dateTime.getMillis)
  }
}

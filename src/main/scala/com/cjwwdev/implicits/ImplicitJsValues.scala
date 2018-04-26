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

import play.api.libs.json._

object ImplicitJsValues {
  implicit class JsValueOps(jsValue: JsValue) {
    def get[T](key: String)(implicit reads: Reads[T]): T = jsValue.\(key).getOrThrow(new NoSuchElementException(s"No data found for key '$key'"))
    def getOption[T](key: String)(implicit reads: Reads[T]): Option[T] = jsValue.\(key).asOpt[T]

    def getFirstMatch[T](key: String)(implicit reads: Reads[T]): T = jsValue.\\(key).headOption.collect {
      case x: JsValue if x.validate[T].isSuccess => x.as[T]
    }.getOrElse(throw new NoSuchElementException(s"No data found for key '$key'"))
  }

  implicit class JsLookupResultExtensions(lookupResult: JsLookupResult) {
    def getOrThrow[T](orElse: Throwable)(implicit reads: Reads[T]): T = lookupResult.validate[T] match {
      case JsSuccess(x,_) => x
      case JsError(_)     => throw orElse
    }
  }
}

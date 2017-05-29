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

import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.Reads
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.BadRequest

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait RequestParsers {
  def decryptRequest[T](reads: Reads[T])(f: T => Future[Result])(implicit request: Request[String]): Future[Result] = {
    Try(DataSecurity.decryptIntoType[T](request.body)(reads)) match {
      case Success(Some(data))  => f(data)
      case Success(None)        => Future.successful(BadRequest)
      case Failure(_)           => Future.successful(BadRequest)
    }
  }

  def decryptUrl(enc: String)(f: String => Future[Result]): Future[Result] = {
    Try(DataSecurity.decryptString(enc)) match {
      case Success(Some(data))  => f(data)
      case Success(None)        => Future.successful(BadRequest)
      case Failure(_)           => Future.successful(BadRequest)
    }
  }
}
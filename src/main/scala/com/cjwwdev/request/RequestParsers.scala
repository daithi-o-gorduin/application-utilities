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

import com.cjwwdev.logging.Logger
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.{BadRequest, InternalServerError}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait RequestParsers {
  def decryptRequest[T](reads: Reads[T])(f: T => Future[Result])(implicit request: Request[String]): Future[Result] = {
    Try(DataSecurity.decryptIntoType[T](request.body)(reads)) match {
      case Success(Some(data))  => f(data)
      case Success(None)        =>
        Logger.warn(s"[RequestParsers] - [decryptRequest] - decryption returned none (${request.path})")
        Future.successful(BadRequest)
      case Failure(_)           =>
        Logger.error(s"[RequestParsers] - [decryptRequest] - decryption failed ${request.path}")
        Future.successful(BadRequest)
    }
  }

  def decryptUrl(enc: String)(f: String => Future[Result])(implicit request: Request[_]): Future[Result] = {
    Try(DataSecurity.decryptString(enc)) match {
      case Success(Some(data))  => f(data)
      case Success(None)        =>
        Logger.warn(s"[RequestParsers] - [decryptRequest] - decryption returned none (${request.path})")
        Future.successful(BadRequest)
      case Failure(_)           =>
        Logger.error(s"[RequestParsers] - [decryptRequest] - decryption failed ${request.path}")
        Future.successful(BadRequest)
    }
  }

  def decryptUrlIntoType[T](enc: String)(reads: Reads[T])(f: T => Future[Result])(implicit request: Request[_]): Future[Result] = {
    Try(DataSecurity.decryptIntoType[T](enc)(reads)) match {
      case Success(Some(data))  => f(data)
      case Success(None)        =>
        Logger.warn(s"[RequestParsers] - [decryptRequest] - decryption returned none (${request.path})")
        Future.successful(BadRequest)
      case Failure(_)           =>
        Logger.error(s"[RequestParsers] - [decryptRequest] - decryption failed ${request.path}")
        Future.successful(BadRequest)
    }
  }

  def withJsonBody[T](f: T => Future[Result])(implicit reads: Reads[T], manifest: Manifest[T], request: Request[_]): Future[Result] = {
    DataSecurity.decryptString(request.body.toString) match {
      case Some(stringJson) => Json.parse(stringJson).validate[T] match {
        case JsSuccess(ting, _) => f(ting)
        case JsError(errors)    => Future.successful(BadRequest(s"Invalid ${manifest.runtimeClass.getSimpleName} errors: $errors"))
      }
      case None => Future.successful(BadRequest)
    }
  }
}

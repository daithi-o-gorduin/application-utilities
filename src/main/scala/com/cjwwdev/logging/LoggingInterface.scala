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

package com.cjwwdev.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.mvc.Request

trait LoggingInterface {
  val logger: Logger

  def trace(message: String)(implicit request: Request[_]): Unit
  def debug(message: String)(implicit request: Request[_]): Unit
  def info(message: String)(implicit request: Request[_]): Unit
  def warn(message: String)(implicit request: Request[_]): Unit
  def error(message: String)(implicit request: Request[_]): Unit
}

class BackendLogging(className: Class[_]) extends LoggingInterface {
  override val logger = LoggerFactory.getLogger(className)

  private def getSessionId(implicit request: Request[_]): String = request.headers.get("cookieId").fold("session=[INVALID SESSION] ")(header => s"session=[$header] ")

  private def getAppId(implicit request: Request[_]): String = request.headers.get("appId").fold("application=[UNKNOWN APP] ")(header => s"application=[$header] ")

  override def trace(message: String)(implicit request: Request[_]): Unit = logger.trace(s"$getAppId$getSessionId$message")
  override def debug(message: String)(implicit request: Request[_]): Unit = logger.debug(s"$getAppId$getSessionId$message")
  override def info(message: String)(implicit request: Request[_]): Unit  = logger.info(s"$getAppId$getSessionId$message")
  override def warn(message: String)(implicit request: Request[_]): Unit  = logger.warn(s"$getAppId$getSessionId$message")
  override def error(message: String)(implicit request: Request[_]): Unit = logger.error(s"$getAppId$getSessionId$message")
}

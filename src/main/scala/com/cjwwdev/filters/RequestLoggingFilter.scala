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
package com.cjwwdev.filters

import javax.inject.Inject

import akka.stream.Materializer
import org.apache.commons.lang3.time.FastDateFormat
import org.joda.time.DateTimeUtils
import org.slf4j.LoggerFactory
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RequestLoggingFilter @Inject()(implicit val mat: Materializer) extends Filter {

  val logger = LoggerFactory.getLogger("Logging filter")

  private val dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSSZZ")

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    val result = f(rh)
    buildLoggerOutput(result, rh, DateTimeUtils.currentTimeMillis) map logger.info
    result
  }

  private def getElapsedTime(start: Long): Long = DateTimeUtils.currentTimeMillis - start

  private def buildLoggerOutput(result: Future[Result], rh: RequestHeader, start: Long): Future[String] = result map {
    res => s"${rh.method.capitalize} request to ${rh.uri} returned a ${res.header.status} and took ${getElapsedTime(start)}"
  }
}

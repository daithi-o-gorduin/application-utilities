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
package com.cjwwdev.logging.converters

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import play.utils.Colors

class HighlightedMessage extends ClassicConverter {

  private val httpMethodsRegex = """^HEAD|GET|POST|PUT|PATCH|DELETE$""".r
  private val uriRegex         = """\/[A-Za-z0-9]+""".r
  private val statusCodeRegex  = """([0-9]{3})""".r
  private val elaspedRegex     = """[0-9]+ms""".r

  override def convert(event: ILoggingEvent): String = {
    val method = httpMethodsRegex.findAllIn(event.getMessage).mkString
    val uri    = uriRegex.findAllIn(event.getMessage).mkString
    val code   = statusCodeRegex.findFirstIn(event.getMessage).mkString
    val time   = elaspedRegex.findAllIn(event.getMessage).mkString

    val methodReplace = event.getMessage.replaceAll(method, Colors.yellow(method))
    val uriReplace    = methodReplace.replaceAll(uri, Colors.green(uri))
    val codeReplace   = uriReplace.replaceAll(code, Colors.cyan(code))
    val timeReplace   = codeReplace.replaceAll(time, Colors.magenta(time))
    timeReplace
  }
}

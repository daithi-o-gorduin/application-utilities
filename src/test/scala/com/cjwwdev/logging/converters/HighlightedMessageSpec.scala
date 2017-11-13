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

import ch.qos.logback.classic.spi.ILoggingEvent
import org.scalatestplus.play.PlaySpec
import play.utils.Colors

class HighlightedMessageSpec extends PlaySpec {

  class Event(message: String) extends ILoggingEvent {
    override def hasCallerData = ???
    override def prepareForDeferredProcessing() = ???
    override def getMarker = ???
    override def getMDCPropertyMap = ???
    override def getLoggerName = ???
    override def getFormattedMessage = ???
    override def getMessage = message
    override def getLoggerContextVO = ???
    override def getLevel = ???
    override def getTimeStamp = ???
    override def getThreadName = ???
    override def getThrowableProxy = ???
    override def getArgumentArray = ???
    override def getCallerData = ???
    override def getMdc = ???
  }

  val testConverter = new HighlightedMessage

  "getting a message from a ILoggingEvent" should {
    "return a highlighted message" in {
      val getEvent  = new Event("GET request to /test/uri returned a 404 and took 12345ms")
      val postEvent = new Event("POST request to /test/uri returned a 404 and took 12345ms")

      println(testConverter.convert(getEvent))
      println(testConverter.convert(postEvent))
    }
  }
}

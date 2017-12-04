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

package com.cjwwdev.regex

import scala.util.matching.Regex

trait RegexPack {
  val emailRegex: Regex             = """[A-Za-z0-9\-_.]{1,126}@[A-Za-z0-9\-_.]{1,126}""".r
  val userNameRegex: Regex          = """^\w[A-Za-z0-9]{0,10}$""".r
  val initialsRegex: Regex          = """^[A-Z]{1,5}$""".r
  val locationRegex: Regex          = """^\w[A-Za-z- ]{0,49}$""".r
  val orgNameRegex: Regex           = """^\w[A-Za-z- ]{0,49}$""".r
  val firstNameRegex: Regex         = """^\w[A-Za-z ]{0,29}$""".r
  val lastNameRegex: Regex          = """^\w[A-Za-z- ]{0,49}$""".r
  val hexadecimalColourRegex: Regex = """^#(?:[0-9a-fA-F]{3}){1,2}$""".r
  val urlRegex: Regex               = """https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)""".r
  val passwordRegex: Regex          = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{10,}$""".r
  val defaultUrl                    = "/account-services/assets/images/background.jpg"
}

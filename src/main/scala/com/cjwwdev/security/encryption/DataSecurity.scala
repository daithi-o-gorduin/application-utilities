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

package com.cjwwdev.security.encryption

import java.security.MessageDigest
import java.util
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import com.typesafe.config.ConfigFactory
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsResult, Json, Reads, Writes}

import scala.util.{Failure, Success, Try}

object DataSecurity extends DataSecurity

trait DataSecurity extends DataCommon {

  private val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

  private val logger = LoggerFactory.getLogger(getClass)
  
  def encryptType[T: Writes](data: T): String = {
    val json = Json.toJson(data).toString
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec)
    Base64.encodeBase64URLSafeString(cipher.doFinal(json.getBytes("UTF-8")))
  }

  def decryptIntoType[T: Reads](data: String): JsResult[T] = {
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec)
    Try(cipher.doFinal(Base64.decodeBase64(data))) match {
      case Success(decrypted) => Json.parse(new String(decrypted)).validate[T]
      case Failure(e)         =>
        logger.error("[decryptIntoType] : the input string has failed decryption")
        throw e
    }
  }

  def encryptString(data: String): String = {
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec)
    Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes("UTF-8")))
  }

  def decryptString(data: String): String = {
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec)
    Try(cipher.doFinal(Base64.decodeBase64(data))) match {
      case Success(decrypted) => new String(decrypted)
      case Failure(e)         =>
        logger.error("[decryptString] - the input string has failed decryption")
        throw e
    }
  }
}

trait DataCommon {
  private val LENGTH = 16

  private val KEY : String  = ConfigFactory.load.getString("data-security.key")
  private val SALT : String = ConfigFactory.load.getString("data-security.salt")

  protected val keyToSpec: SecretKeySpec = {
    val sha512 = MessageDigest.getInstance("SHA-512").digest((SALT + KEY).getBytes("UTF-8"))
    new SecretKeySpec(util.Arrays.copyOf(sha512, LENGTH), "AES")
  }
}

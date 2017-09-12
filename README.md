[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/application-utilities/images/download.svg) ](https://bintray.com/cjww-development/releases/application-utilities/_latestVersion)

application-utilities
=====================

This library contains common configuration for microservices and utilities for validation, regex, json formatting, loading configuration and request body processing.

To utilise this library add this to your sbt build file

```sbtshell
"com.cjww-dev.libs" % "application-utilities_2.11" % "2.2.0" 
```

## About
#### resources/common.conf
Contains common configuration strings for CJWW scala play apps. To utilise add the following snippet to your application.conf file. 
```hocon
    include "common.conf"
```

<br>

#### ConfigurationLoader.scala
Contains functions to pull a domain for a microservice from configuration and to pull an applications applicationId. Throws **MissingConfigurationException** if configuration value is not found. 

```scala
    val configLoader = new ConfigurationLoader()
    
    configLoader.buildServiceUrl("test-app")
    
    configLoader.getApplicationId("test-app")
```

**Note: ConfigurationLoader needs a play.api.Configuration class to be injected to work.**

<br>

#### IdentifierValidation.scala
Validates if the given string is prefixed correctly and if it a UUID. Returns a NotAcceptable response code if the given string is not in the correct format. 

```scala
    class ExampleController extends Controller with IdentifierValidation {
      def exampleAction(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          validateAs(USER, id) {
            Future.successful(Ok)
          }
      }
    }
```

<br>

#### com.cjwwdev.json
This package contains traits to ensure case classes have a Json formatter. Also contains Json reads and writes for **DateTime**

<br>

#### RequestParsers.scala
Contains functions to decrypt either part of the url or the request body into type **T**.

```scala
    //Example decrypting to into type T
    case class ExampleModel(str: String, int: Int)
    implicit val format = Json.format[ExampleModel]
    
    class ExampleController extends Controller with IdentifierValidation {
      def exampleActionDecryptingRequestBody(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          withJsonBody[ExampleModel] { decryptedModel =>
            Ok(decryptedModel)
          }
      }
      
      def exampleActionDecryptingUrl(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          withEncryptedUrl(id) { decryptedUrl =>
            Ok(decryptedUrl)
          }
      }
    }
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
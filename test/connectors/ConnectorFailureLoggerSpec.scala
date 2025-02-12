/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import uk.gov.hmrc.http.{HeaderCarrier, JsValidationException, UpstreamErrorResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConnectorFailureLoggerSpec extends AnyFreeSpec with Matchers with ScalaFutures with OptionValues with TryValues {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "ConnectorFailureLogger" - {

    "logFailureReason" - {

      "should log an UpstreamErrorResponse" in {
        val connectorName         = "TestConnector"
        val futureFailingResponse = Future.failed[Nothing](UpstreamErrorResponse("Error", 500))

        val loggedFuture = futureFailingResponse.logFailureReason(connectorName)

        whenReady(loggedFuture.failed) { exception =>
          exception `mustBe` an[UpstreamErrorResponse]
          exception.asInstanceOf[UpstreamErrorResponse].statusCode `mustBe` 500
        }
      }

      "should log a JsValidationException" in {
        val connectorName         = "TestConnector"
        val futureFailingResponse = Future.failed[Nothing](
          new JsValidationException("GET", "http://test.url", classOf[SomeResponseType], "Invalid format")
        )

        val loggedFuture = futureFailingResponse.logFailureReason(connectorName)

        whenReady(loggedFuture.failed) { exception =>
          exception `mustBe` a[JsValidationException]
          exception.getMessage `must` `include`("GET of 'http://test.url' returned invalid json. Attempting to convert to")
          exception.getMessage `must` `include`("SomeResponseType")
          exception.getMessage `must` `include`("gave errors: Invalid format")
        }
      }

      "should log a generic exception" in {
        val connectorName         = "TestConnector"
        val futureFailingResponse = Future.failed[Nothing](new Exception("Generic exception"))

        val loggedFuture = futureFailingResponse.logFailureReason(connectorName)

        whenReady(loggedFuture.failed) { exception =>
          exception `mustBe` an[Exception]
          exception.getMessage `mustBe` "Generic exception"
        }
      }
    }
  }
}

case class SomeResponseType()

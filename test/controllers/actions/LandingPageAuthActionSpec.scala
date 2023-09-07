/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.{BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{running, _}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Name, ~}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LandingPageAuthActionSpec extends SpecBase {

  class Harness(authAction: LandingPageIdentifierAction) {
    def onPageLoad() = authAction(_ => Results.Ok)
  }

  "Auth Action" - {

    "when the user hasn't logged in" - {

      "must redirect the user to log in" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new MissingBearerToken),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     =
            controller.onPageLoad()(FakeRequest(GET, "?submissionUniqueId=12341234-1234-1234-1234-123412341234"))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.loginUrl)
        }
      }
    }

    "the user's session has expired" - {

      "must redirect the user to log in when a submissionUniqueId is specified" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new BearerTokenExpired),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     =
            controller.onPageLoad()(FakeRequest(GET, "?submissionUniqueId=12341234-1234-1234-1234-123412341234"))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.loginUrl)
        }
      }

      "must advise to be accessed from calculation service if no submissionUniqueId" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new BearerTokenExpired),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     =
            controller.onPageLoad()(FakeRequest(GET, ""))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith("/submit-public-pension-adjustment/calculation-prerequisite")
        }
      }

      "must be unauthorised if submissionUniqueId is not well formed" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new BearerTokenExpired),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     =
            controller.onPageLoad()(FakeRequest(GET, "?submissionUniqueId=someInvalidFormat"))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith("/submit-public-pension-adjustment/there-is-a-problem")
        }
      }
    }

    "the user doesn't have sufficient enrolments" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new InsufficientEnrolments),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user doesn't have sufficient confidence level" - {

      "must redirect the user to iv uplift journey" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new InsufficientConfidenceLevel),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest(GET, "?submissionUniqueId=123"))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must include("/iv-stub/uplift?origin=ppa&confidenceLevel=250")
        }
      }
    }

    "the user used an unaccepted auth provider" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new UnsupportedAuthProvider),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user has an unsupported affinity group" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new UnsupportedAffinityGroup),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }
    }

    "the user has an unsupported credential role" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            new FakeFailingAuthConnector(new UnsupportedCredentialRole),
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }
    }

    "controller should be accessed and return successfully" - {

      "when name, saUtr and date of birth are retrieved" in {

        val mockAuthConnector = mock[AuthConnector]

        val name        = Some(Name(Some("first"), Some("last")))
        val saUtr       = Some("saUtr")
        val dateOfBirth = Some(LocalDate.now())

        val retrievals = new ~(
          new ~(
            new ~(
              new ~(new ~(new ~(Some("nino"), Some("internalId")), Some(AffinityGroup.Individual)), Some(User)),
              name
            ),
            saUtr
          ),
          dateOfBirth
        )
        whenRetrievalsAre(mockAuthConnector, retrievals)

        controllerShouldBeAccessed(mockAuthConnector)
      }

      "when name, saUtr but no date of birth are retrieved" in {

        val mockAuthConnector = mock[AuthConnector]

        val name        = Some(Name(Some("first"), Some("last")))
        val saUtr       = Some("saUtr")
        val dateOfBirth = None

        val retrievals = new ~(
          new ~(
            new ~(
              new ~(new ~(new ~(Some("nino"), Some("internalId")), Some(AffinityGroup.Individual)), Some(User)),
              name
            ),
            saUtr
          ),
          dateOfBirth
        )
        whenRetrievalsAre(mockAuthConnector, retrievals)

        controllerShouldBeAccessed(mockAuthConnector)
      }

      "when name, but no saUtr or date of birth are retrieved" in {

        val mockAuthConnector = mock[AuthConnector]

        val name        = Some(Name(Some("first"), Some("last")))
        val saUtr       = None
        val dateOfBirth = None

        val retrievals = new ~(
          new ~(
            new ~(
              new ~(new ~(new ~(Some("nino"), Some("internalId")), Some(AffinityGroup.Individual)), Some(User)),
              name
            ),
            saUtr
          ),
          dateOfBirth
        )
        whenRetrievalsAre(mockAuthConnector, retrievals)

        controllerShouldBeAccessed(mockAuthConnector)
      }
    }

    "should be unauthorised" - {

      "if mandatory value cannot be retrieved" in {
        val mockAuthConnector = mock[AuthConnector]

        val name        = None
        val saUtr       = None
        val dateOfBirth = None

        val retrievals = new ~(
          new ~(
            new ~(
              new ~(new ~(new ~(Some("nino"), Some("internalId")), Some(AffinityGroup.Individual)), Some(User)),
              name
            ),
            saUtr
          ),
          dateOfBirth
        )
        whenRetrievalsAre(mockAuthConnector, retrievals)

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            mockAuthConnector,
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }

      "if nino cannot be retrieved" in {
        val mockAuthConnector = mock[AuthConnector]

        val name        = Some(Name(Some("first"), Some("last")))
        val saUtr       = Some("saUtr")
        val dateOfBirth = Some(LocalDate.now())

        val retrievals = new ~(
          new ~(
            new ~(
              new ~(new ~(new ~(None, Some("internalId")), Some(AffinityGroup.Individual)), Some(User)),
              name
            ),
            saUtr
          ),
          dateOfBirth
        )
        whenRetrievalsAre(mockAuthConnector, retrievals)

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedLandingPageIdentifierAction(
            mockAuthConnector,
            appConfig,
            bodyParsers
          )
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }

    }
  }

  private def controllerShouldBeAccessed(mockAuthConnector: AuthConnector) = {
    val application = applicationBuilder(userAnswers = None).build()

    running(application) {
      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
      val appConfig   = application.injector.instanceOf[FrontendAppConfig]

      val authAction = new AuthenticatedLandingPageIdentifierAction(
        mockAuthConnector,
        appConfig,
        bodyParsers
      )
      val controller = new Harness(authAction)
      val result     = controller.onPageLoad()(FakeRequest())

      status(result) mustBe OK
    }
  }

  private def whenRetrievalsAre(
    mockAuthConnector: AuthConnector,
    retrievals: Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Some[User.type] ~
      Option[Name] ~ Option[String] ~ Option[LocalDate]
  ) =
    when(
      mockAuthConnector.authorise[
        Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[CredentialRole] ~
          Option[Name] ~ Option[String] ~ Option[LocalDate]
      ](
        any(),
        any()
      )(any(), any())
    )
      .thenReturn(
        Future.successful(
          retrievals
        )
      )
}

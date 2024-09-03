package controllers

import base.SpecBase
import models.{Done, NormalMode, Period}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.SchemeElectionConsentView

import scala.concurrent.Future

class SchemeElectionConsentControllerSpec extends SpecBase {

  "SchemeCreditConsent Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SchemeElectionConsentController.onPageLoad(NormalMode, Period._2020).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SchemeElectionConsentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(NormalMode, Period._2020)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.SchemeElectionConsentController.onPageLoad(NormalMode, Period._2020).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }
  }
}

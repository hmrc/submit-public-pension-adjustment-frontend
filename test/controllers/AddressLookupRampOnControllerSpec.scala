package controllers

import base.SpecBase
import connectors.AddressLookupConnector
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimOnBehalfPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AddressLookupRampOnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val claimOnBhealfHandlerRoute = routes.AddressLookupRampOnController.existingUrlFragmentClaimOnBehalfHandler(NormalMode).url
  lazy val userAddressHandlerRoute = routes.AddressLookupRampOnController.existingUrlFragmentUserAddressHandler(NormalMode).url
  lazy val claimOnBehalfRoute = routes.AddressLookupRampOnController.rampOnClaimOnBehalf(NormalMode).url
  lazy val userAddressRoute = routes.AddressLookupRampOnController.rampOnClaimOnBehalf(NormalMode).url


  "AddressLookupRampOnController" - {

    "must redirect to ALF in a claim on behalf scenario " in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.start(any())(any())) thenReturn Future.successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission))
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, claimOnBehalfRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to ALF in a claim on behalf scenario when user accesses deprecated URL fragments " in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.start(any())(any())) thenReturn Future.successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission))
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, claimOnBhealfHandlerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to ALF in user address scenario" in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.start(any())(any())) thenReturn Future.successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission))
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, userAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"

      }
    }

    "must redirect to ALF in a user address scenario when user accesses deprecated URL fragments " in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.start(any())(any())) thenReturn Future.successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission))
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, userAddressHandlerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }
  }
}

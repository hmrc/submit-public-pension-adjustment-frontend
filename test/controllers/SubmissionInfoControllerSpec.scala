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

package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.SubmissionInfoView

class SubmissionInfoControllerSpec extends SpecBase {

  lazy val submissionInfoRoute = routes.SubmissionInfoController.onPageLoad().url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  "SubmissionInfo Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, submissionInfoRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SubmissionInfoView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view()(request, messages(application)).toString
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, submissionInfoRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` calculationPrerequisiteRoute
      }
    }
  }
}

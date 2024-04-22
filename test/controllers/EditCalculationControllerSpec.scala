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
import play.api.test.Helpers.{GET, redirectLocation, route, running}
import play.api.test.Helpers._

class EditCalculationControllerSpec extends SpecBase {

  "EditCalculationController Controller" - {

    "must redirect to calculation task list" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.EditCalculationController.editCalculation.url)

        val result = route(application, request).value

        redirectLocation(
          result
        ).value must endWith("/public-pension-adjustment/task-list")
      }
    }
  }
}

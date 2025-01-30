/*
 * Copyright 2025 HM Revenue & Customs
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
import models.calculation.inputs.CalculationInputs
import models.submission.Submission
import pages.TestData
import play.api.Application
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.lifetimeallowance._
import viewmodels.govuk.all.SummaryListViewModel
import views.html.ViewYourLTAAnswersView

class ViewYourLTAAnswersControllerSpec extends SpecBase {

  "ViewYourLTAAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      val calculationInputs: CalculationInputs = TestData.calculationInputsLTAOnly
      val submissionLTAOnly                    = Submission("", "", calculationInputs, None)
      val application                          =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submissionLTAOnly)).build()

      running(application) {
        val request                          = FakeRequest(GET, routes.ViewYourLTAAnswersController.onPageLoad().url)
        val result                           = route(application, request).value
        val view                             = application.injector.instanceOf[ViewYourLTAAnswersView]
        val expectedSeq: Seq[SummaryListRow] = summaryRowsLTA(application, submissionLTAOnly)
        val list                             = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }
  }

  private def summaryRowsLTA(application: Application, submission: Submission) = {
    val expectedSeq = Seq(
      DateOfBenefitCrystallisationEventSummary.row(submission)(messages(application)) ++
        LtaProtectionOrEnhancementsSummary.row(submission)(messages(application)) ++
        ProtectionTypeSummary.row(submission)(messages(application)) ++
        ProtectionReferenceSummary.row(submission)(messages(application)) ++
        EnhancementTypeSummary.row(submission)(messages(application)) ++
        InternationalEnhancementReferenceSummary.row(submission)(messages(application)) ++
        PensionCreditReferenceSummary.row(submission)(messages(application)) ++
        ProtectionEnhancedChangedSummary.row(submission)(messages(application)) ++
        WhatNewProtectionTypeEnhancementSummary.row(submission)(messages(application)) ++
        ReferenceNewProtectionTypeEnhancementSummary.row(submission)(messages(application)) ++
        NewEnhancementTypeSummary.row(submission)(messages(application)) ++
        NewInternationalEnhancementReferenceSummary.row(submission)(messages(application)) ++
        NewPensionCreditReferenceSummary.row(submission)(messages(application)) ++
        LifetimeAllowanceChargeSummary.row(submission)(messages(application)) ++
        ExcessLifetimeAllowancePaidSummary.row(submission)(messages(application)) ++
        LumpSumValueSummary.row(submission)(messages(application)) ++
        AnnualPaymentValueSummary.row(submission)(messages(application)) ++
        WhoPaidLTAChargeSummary.row(submission)(messages(application)) ++
        SchemeNameAndTaxRefSummary.row(submission)(messages(application)) ++
        UserSchemeDetailsSummary.row(submission)(messages(application)) ++
        QuarterChargePaidSummary.row(submission)(messages(application)) ++
        YearChargePaidSummary.row(submission)(messages(application)) ++
        NewExcessLifetimeAllowancePaidSummary.row(submission)(messages(application)) ++
        NewLumpSumValueSummary.row(submission)(messages(application)) ++
        NewAnnualPaymentValueSummary.row(submission)(messages(application)) ++
        WhoPayingExtraLtaChargeSummary.row(submission)(messages(application)) ++
        LtaPensionSchemeDetailsSummary.row(submission)(messages(application))
    ).flatten
    expectedSeq
  }
}

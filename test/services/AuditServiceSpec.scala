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

package services

import org.apache.pekko.util.Timeout
import base.SpecBase
import models.{SubmissionSaveAndReturnAuditEvent, SubmissionStartAuditEvent}
import org.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.await
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class AuditServiceSpec extends SpecBase with MockitoSugar {

  private val mockAuditConnector = mock[AuditConnector]

  implicit val ec: ExecutionContext.type = ExecutionContext

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[AuditConnector].toInstance(mockAuditConnector)
    )
    .configure(
      "auditing.submission-start-event-name" -> "SubmissionStart"
    )
    .build()

  private val service = app.injector.instanceOf[AuditService]

  implicit def defaultAwaitTimeout: Timeout = 60.seconds

  "AuditService" - {

    "auditSubmissionStart" - {
      "should call the audit connector with a SubmissionStartAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val submissionStartAuditEvent =
          SubmissionStartAuditEvent(
            "8453ea66-e3fe-4f35-b6c2-a6aa87482661",
            true
          )

        await(service.auditSubmissionStart(submissionStartAuditEvent)(hc)) mustBe ()
      }
    }

    "auditSubmissionUserSelectionContinue" - {
      "should call the audit connector with a SubmissionSaveAndReturnAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val submissionSaveAndReturnAuditEvent =
          SubmissionSaveAndReturnAuditEvent(
            true,
            "uniqueId",
            "userId"
          )

        await(service.auditSubmissionUserSelectionContinue(submissionSaveAndReturnAuditEvent)(hc)) mustBe ()
      }
    }

    "auditSubmissionUserSelectionEdit" - {
      "should call the audit connector with a SubmissionSaveAndReturnAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val submissionSaveAndReturnAuditEvent =
          SubmissionSaveAndReturnAuditEvent(
            true,
            "uniqueId",
            "userId"
          )

        await(service.auditSubmissionUserSelectionEdit(submissionSaveAndReturnAuditEvent)(hc)) mustBe ()
      }
    }

    "auditSubmissionUserSelectionRestart" - {
      "should call the audit connector with a SubmissionSaveAndReturnAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val submissionSaveAndReturnAuditEvent =
          SubmissionSaveAndReturnAuditEvent(
            true,
            "uniqueId",
            "userId"
          )

        await(service.auditSubmissionUserSelectionRestart(submissionSaveAndReturnAuditEvent)(hc)) mustBe ()
      }
    }

  }

}

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

import config.FrontendAppConfig
import models.{AuthenticatedUserSaveAndReturnAuditEvent, SubmissionSaveAndReturnAuditEvent, SubmissionStartAuditEvent}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject() (
  auditConnector: AuditConnector,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext) {

  def auditSubmissionStart(event: SubmissionStartAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.submissionStartAuditEventName, event))

  def auditSubmissionUserSelectionContinue(event: SubmissionSaveAndReturnAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.submissionUserSelectionContinueAuditEventName, event))

  def auditSubmissionUserSelectionEdit(event: SubmissionSaveAndReturnAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.submissionUserSelectionEditAuditEventName, event))

  def auditSubmissionUserSelectionRestart(event: SubmissionSaveAndReturnAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.submissionUserSelectionRestartAuditEventName, event))

  def auditAuthenticatedUserSignOut(event: AuthenticatedUserSaveAndReturnAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.authenticatedUserSignOut, event))

  def auditAuthenticatedUserSaveAndReturn(event: AuthenticatedUserSaveAndReturnAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.authenticatedUserSaveAndReturn, event))
}

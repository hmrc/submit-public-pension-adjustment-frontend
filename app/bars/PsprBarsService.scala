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

package bars

import bars.PsprBarsService._
import bars.barsmodel.request.{BarsBankAccount, BarsSubject}
import bars.barsmodel.response.VerifyResponse.accountDoesNotExist
import bars.barsmodel.response._
import models.BankDetails
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PsprBarsService @Inject() (
  barsService: BarsService
) {

  def verifyBankDetails(
    bankAccountDetails: BankDetails
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[BarsError, VerifyResponse]] = {

    val resp =
      barsService
        .verifyBankDetails(
          bankAccount = toBarsBankAccount(bankAccountDetails),
          subject = toBarsSubject(bankAccountDetails)
        )
        .flatMap {
          case result @ (Right(_) | Left(_: BarsValidateError)) =>
            // don't update the verify count on success or validate error
            Future.successful(result)
          case result @ Left(bve: BarsVerifyError)              =>
            Future.successful(result)
        }
    resp
  }

  def verifyPersonal(
    bankAccountDetails: BankDetails
  )(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[ValidationResult] = {

    val resp =
      barsService.verifyPersonal(
        bankAccount = BarsBankAccount
          .normalise(sortCode = bankAccountDetails.sortCode, accountNumber = bankAccountDetails.accountNumber),
        subject = toBarsSubject(bankAccountDetails)
      )

    resp.map {
      case accountDoesNotExist() => Invalid
      case VerifyResponse(_)     => Valid
    }
  }
}

object PsprBarsService {

  def toBarsBankAccount(bankDetails: BankDetails): BarsBankAccount =
    BarsBankAccount.normalise(bankDetails.sortCode, bankDetails.accountNumber)

  def toBarsSubject(bankDetails: BankDetails): BarsSubject = BarsSubject(
    title = None,
    name = Some(bankDetails.accountName),
    firstName = None,
    lastName = None,
    dob = None,
    address = None
  )

  sealed trait ValidationResult

  case object Valid extends ValidationResult
  case object Invalid extends ValidationResult
  case object InvalidAsSortCodeOnDenyList extends ValidationResult
}

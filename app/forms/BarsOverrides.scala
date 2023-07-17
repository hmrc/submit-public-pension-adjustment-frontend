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

package forms

import forms.helper.FormErrorWithFieldMessageOverrides
import play.api.data.FormError

object BarsOverrides {

  private val sortCodeAndAccountNumberOverrides: Seq[FormError]              = Seq(
    FormError("bank-account.sortcode", ""), // 'turns off' the sortCode field error
    FormError("bank-account.account-number", ""), // 'turns off' the accountNumber field error
    FormError("sortCodeAndAccountNumber", "bankDetails.bars.account.number.not.well.formatted")
  )
  val accountNumberNotWellFormatted: FormErrorWithFieldMessageOverrides      =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.account.number.not.well.formatted"),
      fieldMessageOverrides = sortCodeAndAccountNumberOverrides
    )
  val sortCodeNotPresentOnEiscd: FormErrorWithFieldMessageOverrides          =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.sortcode.not.present.on.eiscd"),
      fieldMessageOverrides = sortCodeAndAccountNumberOverrides
    )
  val sortCodeDoesNotSupportsDirectDebit: FormErrorWithFieldMessageOverrides =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.sortcode.does.not.support.direct.debit")
    )
  val nameDoesNotMatch: FormErrorWithFieldMessageOverrides                   =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.account-name", "bankDetails.bars.account.name.no.match")
    )
  val accountDoesNotExist: FormErrorWithFieldMessageOverrides                =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.account.does.not.exist"),
      fieldMessageOverrides = sortCodeAndAccountNumberOverrides
    )
  val sortCodeOnDenyList: FormErrorWithFieldMessageOverrides                 =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.sortcode.on.deny.list"),
      fieldMessageOverrides = sortCodeAndAccountNumberOverrides
    )
  val otherBarsError: FormErrorWithFieldMessageOverrides                     =
    FormErrorWithFieldMessageOverrides(
      formError = FormError("bank-account.sortcode", "bankDetails.bars.other.error"),
      fieldMessageOverrides = sortCodeAndAccountNumberOverrides
    )

}

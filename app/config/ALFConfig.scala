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

package config

import play.api.Configuration

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
case class ALFConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  val baseUrl: String     = servicesConfig.baseUrl("alf")
  val startURL: String    = s"$baseUrl/api/init"
  val retrieveURL: String = s"$baseUrl/api/confirmed"

  val keepAlive: String                                  = configuration.get[String]("urls.keepAlive")
  val addressLookupReturnClaimOnBehalfNormalMode: String =
    configuration.get[String]("urls.addressLookupReturnClaimOnBehalfNormalMode")
  val addressLookupReturnClaimOnBehalfCheckMode: String  =
    configuration.get[String]("urls.addressLookupReturnClaimOnBehalfCheckMode")
  val addressLookupReturnUserAddressNormalMode: String   =
    configuration.get[String]("urls.addressLookupReturnUserAddressNormalMode")
  val addressLookupReturnUserAddressCheckMode: String    =
    configuration.get[String]("urls.addressLookupReturnUserAddressCheckMode")
}

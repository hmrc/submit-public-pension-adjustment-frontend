package pages

import pages.behaviours.PageBehaviours

class ClaimingHigherOrAdditionalTaxRateReliefPageSpec extends PageBehaviours {

  "ClaimingHigherOrAdditionalTaxRateReliefPage" - {

    beRetrievable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beSettable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beRemovable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)
  }
}

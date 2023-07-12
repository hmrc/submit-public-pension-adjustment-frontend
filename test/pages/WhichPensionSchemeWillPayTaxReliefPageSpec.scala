package pages

import models.WhichPensionSchemeWillPayTaxRelief
import pages.behaviours.PageBehaviours

class WhichPensionSchemeWillPayTaxReliefSpec extends PageBehaviours {

  "WhichPensionSchemeWillPayTaxReliefPage" - {

    beRetrievable[WhichPensionSchemeWillPayTaxRelief](WhichPensionSchemeWillPayTaxReliefPage)

    beSettable[WhichPensionSchemeWillPayTaxRelief](WhichPensionSchemeWillPayTaxReliefPage)

    beRemovable[WhichPensionSchemeWillPayTaxRelief](WhichPensionSchemeWillPayTaxReliefPage)
  }
}

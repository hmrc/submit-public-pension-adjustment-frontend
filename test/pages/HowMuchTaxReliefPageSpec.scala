package pages

import pages.behaviours.PageBehaviours

class HowMuchTaxReliefPageSpec extends PageBehaviours {

  "HowMuchTaxReliefPage" - {

    beRetrievable[Int](HowMuchTaxReliefPage)

    beSettable[Int](HowMuchTaxReliefPage)

    beRemovable[Int](HowMuchTaxReliefPage)
  }
}

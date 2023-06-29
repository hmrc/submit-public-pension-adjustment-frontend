package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary

class PensionSchemeMemberDOBPageSpec extends PageBehaviours {

  "PensionSchemeMemberDOBPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](PensionSchemeMemberDOBPage)

    beSettable[LocalDate](PensionSchemeMemberDOBPage)

    beRemovable[LocalDate](PensionSchemeMemberDOBPage)
  }
}

#!/bin/bash

echo ""
echo "Applying migration MemberDateOfDeath"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /memberDateOfDeath                  controllers.MemberDateOfDeathController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /memberDateOfDeath                  controllers.MemberDateOfDeathController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeMemberDateOfDeath                        controllers.MemberDateOfDeathController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeMemberDateOfDeath                        controllers.MemberDateOfDeathController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "memberDateOfDeath.title = MemberDateOfDeath" >> ../conf/messages.en
echo "memberDateOfDeath.heading = MemberDateOfDeath" >> ../conf/messages.en
echo "memberDateOfDeath.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "memberDateOfDeath.checkYourAnswersLabel = MemberDateOfDeath" >> ../conf/messages.en
echo "memberDateOfDeath.error.required.all = Enter the memberDateOfDeath" >> ../conf/messages.en
echo "memberDateOfDeath.error.required.two = The memberDateOfDeath" must include {0} and {1} >> ../conf/messages.en
echo "memberDateOfDeath.error.required = The memberDateOfDeath must include {0}" >> ../conf/messages.en
echo "memberDateOfDeath.error.invalid = Enter a real MemberDateOfDeath" >> ../conf/messages.en
echo "memberDateOfDeath.change.hidden = MemberDateOfDeath" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMemberDateOfDeathUserAnswersEntry: Arbitrary[(MemberDateOfDeathPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[MemberDateOfDeathPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMemberDateOfDeathPage: Arbitrary[MemberDateOfDeathPage.type] =";\
    print "    Arbitrary(MemberDateOfDeathPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(MemberDateOfDeathPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration MemberDateOfDeath completed"

#!/bin/bash

echo ""
echo "Applying migration CannotUseServiceNotIndividual"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotUseServiceNotIndividual                       controllers.CannotUseServiceNotIndividualController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotUseServiceNotIndividual.title = cannotUseServiceNotIndividual" >> ../conf/messages.en
echo "cannotUseServiceNotIndividual.heading = cannotUseServiceNotIndividual" >> ../conf/messages.en

echo "Migration CannotUseServiceNotIndividual completed"

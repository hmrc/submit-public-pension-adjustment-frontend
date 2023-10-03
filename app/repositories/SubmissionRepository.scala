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

package repositories

import config.FrontendAppConfig
import models.submission.Submission
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionRepository @Inject() (
  mongoComponent: MongoComponent,
  appConfig: FrontendAppConfig,
  clock: Clock
)(implicit ec: ExecutionContext, crypto: Encrypter with Decrypter)
    extends PlayMongoRepository[Submission](
      collectionName = "submissions",
      mongoComponent = mongoComponent,
      replaceIndexes = true,
      domainFormat = Submission.encryptedFormat,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("lastUpdated"),
          IndexOptions()
            .name("lastUpdatedIdx")
            .expireAfter(appConfig.cacheTtl, TimeUnit.SECONDS)
        ),
        IndexModel(
          Indexes.ascending("uniqueId"),
          IndexOptions()
            .name("uniqueIdx")
        ),
        IndexModel(
          Indexes.ascending("sessionId"),
          IndexOptions()
            .name("sessionIdx")
        )
      )
    ) {

  def insert(item: Submission): Future[Done] =
    collection
      .replaceOne(
        filter = bySessionId(item.sessionId),
        replacement = item.copy(lastUpdated = clock.instant()),
        options = ReplaceOptions().upsert(true)
      )
      .toFuture
      .map(_ => Done)

  private def byUniqueId(uniqueId: String): Bson = Filters.equal("uniqueId", uniqueId)

  private def bySessionId(sessionId: String): Bson = Filters.equal("sessionId", sessionId)

  def keepAlive(sessionId: String): Future[Boolean] =
    collection
      .updateOne(
        filter = bySessionId(sessionId),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture
      .map(_ => true)

  def get(uniqueId: String): Future[Option[Submission]] =
    collection
      .find(byUniqueId(uniqueId))
      .headOption()

  def getBySessionId(sessionId: String): Future[Option[Submission]] =
    collection
      .find(bySessionId(sessionId))
      .headOption()

  def clear(sessionId: String): Future[Boolean] =
    collection
      .deleteOne(bySessionId(sessionId))
      .toFuture
      .map(_ => true)
}

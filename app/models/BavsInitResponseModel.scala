package models

import play.api.libs.json.{Json, OFormat}

case class BavsInitResponseModel(
                                  journeyId: String,
                                  startUrl: String,
                                  completeUrl: String,
                                  detailsUrl: Option[String])

object BavsInitResponseModel{
  implicit val format: OFormat[BavsInitResponseModel] = Json.format[BavsInitResponseModel]
}
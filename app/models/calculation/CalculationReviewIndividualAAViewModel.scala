package models.calculation

final case class CalculationReviewIndividualAAViewModel(
  outDates: Seq[Seq[RowViewModel]],
  inDates: Seq[Seq[RowViewModel]]
) {
  def annualResultsData: Seq[RowViewModel] = (outDates ++ inDates).flatten

  def inDatesData: Seq[Seq[RowViewModel]] = inDates

  def outDatesData: Seq[Seq[RowViewModel]] = outDates

}

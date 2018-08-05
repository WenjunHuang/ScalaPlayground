trait TabularDataSource {
  val numberOfRows   : Int
  val numberOfColumns: Int

  def label(column: Int): String

  def itemFor(row: Int, column: Int): String
}


class PrintTable {

}

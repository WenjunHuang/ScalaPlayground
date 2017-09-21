package scala_lang.nameparam

import scala.collection.mutable.ArrayBuffer

class Matrix(private val repr: Array[Array[Double]]) {
  def row(idx: Int): Seq[Double] = {
    repr(idx)
  }

  def col(idx: Int): Seq[Double] = {
    repr.foldLeft(ArrayBuffer[Double]()) { (buffer, currentRow) ⇒
      buffer.append(currentRow(idx))
      buffer
    } toArray
  }

  lazy val rowRank = repr.size
  lazy val colRank = if (rowRank > 0) repr(0).size else 0

  override def toString = "Matrix" + repr.foldLeft("") { (msg, row) ⇒
    msg + row.mkString("\n|", " | ", "|")
  }

}

trait ThreadStrategy {
  def execute[A](func: () ⇒ A): () ⇒ A
}

object MatrixUtils {
  def multiply(a: Matrix,
               b: Matrix)(implicit threading: ThreadStrategy = SameThreadStrategy): Matrix = {
    assert(a.colRank == b.rowRank)
    val buffer = new Array[Array[Double]](a.rowRank)
    for (i ← 0 until a.rowRank) {
      buffer(i) = new Array[Double](b.colRank)
    }

    def computeValue(row: Int, col: Int): Unit = {
      val pairwiseElements =
        a.row(row).zip(b.col(col))
      val products =
        for ((x, y) ← pairwiseElements)
          yield x * y
      val result = products.sum
      buffer(row)(col) = result
    }

    val computations = for {
      i ← 0 until a.rowRank
      j ← 0 until b.colRank
    } yield threading.execute { () ⇒ computeValue(i, j) }
    computations.foreach(_ ())

    new Matrix(buffer)
  }
}

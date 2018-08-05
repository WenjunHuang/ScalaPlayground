package bench


object ToBenchmark extends App {
  def merge[T:Ordering](itr1: Iterator[T], itr2: Iterator[T]): Iterator[T] = {
    val o = implicitly[Ordering[T]]
    import o._
    def mergeStream(s1: Stream[T], s2: Stream[T]): Stream[T] = {

      if (s1.head < s2.head) s1.head #:: mergeStream(s1.tail, s2)
      else
        s1
    }

    mergeStream(itr1.toStream, itr2.toStream).toIterator
  }

}

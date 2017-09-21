package cats

import simulacrum._


@typeclass trait CanAppend[A] {
  @op("|+|") def append(a1: A, a2: A): A
}



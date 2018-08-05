package tutorial.webapp.binding.calc

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait Priority {
  val priority: Int
}

sealed trait Token

final case class NoOp() extends Token

final case class Clear() extends Token

trait Decimal

final case class Digit(v: Int) extends Token with Decimal

final case class Dot() extends Token with Decimal

trait Op extends Priority

final case class Plus() extends Token with Op {
  val priority = 1
}

final case class Minus() extends Token with Op {
  val priority = 1
}

final case class Multiply() extends Token with Op {
  val priority = 2
}

final case class Divide() extends Token with Op {
  val priority = 2
}

final case class Result() extends Token with Op {
  val priority = 0
}

trait Memory

final case class MR() extends Token with Memory

final case class MC() extends Token with Memory

final case class MS() extends Token with Memory

case class CalcModel(outputs    : Seq[Double],
                     operators  : Seq[Op],
                     state      : Symbol,
                     accumulator: String,
                     memory     : Option[Double]) extends Immutable with PartialFunction[Token, CalcModel] {
  def result: Try[Double] = state match {
    case 'error ⇒ Failure(new IllegalStateException("error"))
    case _ ⇒ accumulator match {
      case "" ⇒ Success(0d)
      case x ⇒ Success(x.toDouble)
    }
  }

  def apply(t: Token): CalcModel = {
    state match {
      case 'accum ⇒ t match {
        case NoOp() ⇒ doNothing()
        case Clear() ⇒ doClear()
        case d@Digit(_) ⇒ doAccumulate(d)
        case d@Dot() ⇒ doAccumulate(d).go('dot)
        case op: Op ⇒ doOperator(op).go('operator)
        case MS() ⇒ doMS().go('operator)
        case MR() ⇒ doMR().go('operator)
        case MC() ⇒ doMC().go('operator)
      }
      case 'operator ⇒ t match {
        case NoOp() ⇒ doNothing()
        case Clear() ⇒ doClear()
        case d@Digit(_)⇒doResetAccu().doAccumulate(d).go('accum)
        case d@Dot() ⇒ doResetAccu().doAccumulate(d).go('dot)
        case op:Op ⇒ doOperator(op)
        case MS() ⇒ doMS()
        case MR() ⇒ doMR()
        case MC() ⇒ doMC()
      }
      case 'dot ⇒ t match {
        case NoOp() ⇒ doNothing()
        case Clear() ⇒ doClear()
        case d@Digit(_) ⇒ doAccumulate(d)
        case Dot() ⇒ doNothing()
        case op: Op ⇒ doOperator(op).go('operator)
        case MS() ⇒ doMS().go('operator)
        case MR() ⇒ doMR().go('operator)
        case MC() ⇒ doMC().go('operator)
      }
      case 'error ⇒ t match {
        case Clear() ⇒ doClear()
        case _⇒doNothing()
      }
    }
  }

  private def doAccumulate(d: Decimal):CalcModel = copy(accumulator = d match {
    case Digit(0) if accumulator.isEmpty ⇒ ""
    case Digit(n) ⇒ accumulator + n.toString
    case Dot() ⇒ accumulator + '.'
  })

  private def doOperator(op:Op): CalcModel = {
    @tailrec
    def reduce(c:CalcModel,op:Op):CalcModel = {
      c.operators match {
        case head +: tail if op.priority <= head.priority ⇒
          val args = c.outputs.take(2)
          val result = head match {
            case Plus() ⇒ args(1) + args(0)
            case Minus() ⇒ args(1) - args(0)
            case Multiply() ⇒ args(1) * args(0)
            case Divide() ⇒ args(1) / args(0)
          }
          reduce(c.copy(operators = tail,outputs = result +: c.outputs.drop(2),accumulator = result.toString),op)
        case _ ⇒ c
      }
    }
    val c2 = copy(outputs = result.getOrElse(0d) +: outputs)

    val c = reduce(c2,op)
    op match {
      case Result() ⇒ c
      case _ ⇒ c.copy(operators = op +: c.operators)
    }
  }

  private def doResetAccu() = copy(accumulator = "")

  private def doClear() = CalcModel()

  private def doMS() = copy(memory = Some(result.getOrElse(0d)))
  private def doMC() = copy(memory = None)
  private def doMR() = memory.map(it ⇒ copy(accumulator = it.toString)).getOrElse(doNothing())
  private def doNothing() = this

  private def go(s:Symbol) = copy(state = s)

  override def isDefinedAt(t:Token):Boolean = {
    if (state == 'error) t == Clear() else
      t match {
        case NoOp() ⇒ true
        case Clear() ⇒ true
        case d@Digit(_) ⇒ true
        case d@Dot() ⇒ state != 'dot
        case op: Op ⇒ true
        case MS() ⇒ true
        case MR() ⇒ memory.isDefined
        case MC() ⇒ memory.isDefined
      }
  }

  override def toString(): String = s"s: $state - accum: $accumulator - outputs: $outputs - operators: $operators - memory: $memory"
}

object CalcModel {
  def apply():CalcModel = {
    CalcModel(Seq.empty,Seq.empty,'accum,"",None)
  }
}


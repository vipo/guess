package vipo.guess.domain

import scala.collection.immutable.ListMap
import scala.collection.immutable.Map

sealed trait Token

abstract class Operand extends Token
case class Constant(val value: Int) extends Operand
case class Value extends Operand

abstract class Operator(val name: String, val view: String, val prio: Int, op: (Int, Int) => Int) extends Token {
  override def toString(): String = view
  def fullDescription: String = s"${view} (${name})"
  def evaluate(a: Int, b: Int) = op(a, b)
}
case object Plus extends Operator("Addition", "+", 1, (a: Int, b: Int) => a + b)
case object Minus extends Operator("Subtraction", "-", 1, (a: Int, b: Int) => a - b)
case object Mul extends Operator("Multiplication", "*", 2, (a: Int, b: Int) => a * b)
case object Div extends Operator("Division", "/", 2, (a: Int, b: Int) => a / b)
case object Mod extends Operator("Remainder of division", "%", 2, (a: Int, b: Int) => a % b)
case object ShiftLeft extends Operator("Shift left", "<<", 3, (a: Int, b: Int) => a << b)
case object ShiftRight extends Operator("Shift right", ">>", 3, (a: Int, b: Int) => a >> b)

object Language {

  val Operators: List[Operator] = List(Plus, Minus, Mul, Div, Mod, ShiftLeft, ShiftRight)
  val MinValue: Int = -500
  val MaxValue: Int = 500
  val ConstMinValue: Int = 1
  val ConstMaxValue: Int = 10
  
  // grouping operators by priority to have operators with different priorities in language
  private val prioToOp: Map[Int,List[Operator]] = Operators.groupBy(_.prio)  
  private val pairs: Iterator[(Operator, Operator)] = for {
      comb <- prioToOp.keys.toList.sorted.combinations(2)
      a <- prioToOp(comb(0))
      b <- prioToOp(comb(1))
    } yield(a,b)
  
  val AllLanguages: Map[Int, (Operator, Operator)] = ListMap.empty ++ pairs
    .zipWithIndex
    .map(t => (t._2+1, t._1))

}
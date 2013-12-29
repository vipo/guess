package vipo.guess.domain

import scala.collection.immutable.ListMap

sealed abstract class Operator(val name: String, val view: String, val prio: Int) {
  override def toString(): String = view
  def fullDescription: String = s"${view} (${name})"
}
case object Plus extends Operator("Addition", "+", 1)
case object Minus extends Operator("Subtraction", "-", 1)
case object Mul extends Operator("Multiplication", "*", 2)
case object Div extends Operator("Division", "/", 2)
case object Mod extends Operator("Remainder of division", "%", 2)
case object ShiftLeft extends Operator("Shift left", "<<", 3)
case object ShiftRight extends Operator("Shift right", ">>", 3)

object Language {

  val Operators: List[Operator] = List(Plus, Minus, Mul, Div, Mod, ShiftLeft, ShiftRight)
  
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
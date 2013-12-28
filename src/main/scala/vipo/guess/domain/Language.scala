package vipo.guess.domain

import scala.collection.immutable.ListMap

sealed abstract class Operator(val view: String, val prio: Int) {
  override def toString(): String = view
}
case object Plus extends Operator("+", 1)
case object Minus extends Operator("-", 1)
case object Mul extends Operator("*", 2)
case object Div extends Operator("/", 2)
case object Mod extends Operator("%", 2)
case object ShiftLeft extends Operator("<<", 3)
case object ShiftRight extends Operator(">>", 3)

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
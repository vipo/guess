package vipo.guess.domain

import scala.collection.immutable.ListMap
import scala.collection.immutable.Map
import scala.util.Random

sealed trait Token

abstract class Operand extends Token

case class Constant(val value: Int) extends Operand {
  override def toString(): String = value.toString
}
case class Argument() extends Operand

abstract class Operator(val name: String, val view: String, val prio: Int, op: (Int, Int) => Int) extends Token {
  override def toString(): String = view
  def fullDescription: String = s"${view} (${name})"
  def apply(a: Int, b: Int) = op(a, b)
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
  val MinValue: Int = -100
  val MaxValue: Int = 100
  val ConstMinValue: Int = 1
  val ConstMaxValue: Int = 10
  
  private val arguments = (MinValue to MaxValue).toList
  private val random = new Random(System.currentTimeMillis)
  // grouping operators by priority to have operators with different priorities in language
  private val prioToOp: Map[Int,List[Operator]] = Operators.groupBy(_.prio)  
  private val pairs: Iterator[(Operator, Operator)] = for {
      comb <- prioToOp.keys.toList.sorted.combinations(2)
      a <- prioToOp(comb(0))
      b <- prioToOp(comb(1))
    } yield(a,b)
  
  val AllLanguages: Map[Int, (Operator, Operator)] = ListMap.empty ++ pairs
    .zipWithIndex
    .map(t => (t._2 + 1, t._1))

  def randomFunction(no: Int): (Function, List[(Int, Int)]) = {
    def constVal() = ConstMinValue + random.nextInt(ConstMaxValue - ConstMinValue + 1)
    val argName: Char = ('a' + random.nextInt('z' - 'a' + 1)).toChar
    val ops =
      if (random.nextBoolean) AllLanguages(no)
      else AllLanguages(no).swap
    val (const1, const2) = (constVal(), constVal())
    val confs =
      if (random.nextBoolean) List(Argument(), Argument(), Constant(const1))
      else List(Constant(const1), Constant(const2), Argument())
    val confsPermutations = confs.permutations.toList
    val conf = confsPermutations(random.nextInt(confsPermutations.size))
    val f = new Function(argName.toString, (conf(0), ops._1, conf(1), ops._2, conf(2)))
    (f, arguments.map(v => (v, f(v))).toList)
  }

}
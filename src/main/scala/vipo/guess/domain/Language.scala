package vipo.guess.domain

import scala.collection.immutable.ListMap
import scala.collection.immutable.Map
import scala.util.Random

sealed trait Token extends Serializable

abstract class Operand extends Token

case class Constant(val value: Int) extends Operand {
  override def toString(): String = value.toString
}
case class Argument() extends Operand

abstract class Operator(val name: String, val view: String, val prio: Int, val safe: Boolean,
    op: (Int, Int) => Int, validation: (Int, Int) => Boolean) extends Token {
  override def toString(): String = view
  def fullDescription: String = s"${view} (${name})"
  def apply(a: Int, b: Int) = op(a, b)
  def validate = validation
}
case object Plus extends Operator("Addition", "+", 1, true, (a: Int, b: Int) => a + b, (_, _) => true)
case object Minus extends Operator("Subtraction", "-", 1, true, (a: Int, b: Int) => a - b, (_, _) => true)
case object Mul extends Operator("Multiplication", "*", 2, true, (a: Int, b: Int) => a * b, (_, _) => true)
case object Div extends Operator("Division", "/", 2, false, (a: Int, b: Int) => a / b, (_, b: Int) => b != 0)
case object Mod extends Operator("Remainder of division", "%", 2, false, (a: Int, b: Int) => a % b, (_, b: Int) => b != 0)
case object LeftShift extends Operator("Left-shift", "<<", 0, false, (a: Int, b: Int) => a << b, (_, b: Int) => b >= 0)
case object ARightShift extends Operator("Arithmetic right-shift", ">>", 0, false, (a: Int, b: Int) => a >> b, (_, b: Int) => b >= 0)
case object URightShift extends Operator("Unsigned right-shift", ">>>", 0, false, (a: Int, b: Int) => a >>> b, (_, b: Int) => b >= 0)

object Language {

  type LangNo = Int
  
  val MinValue: Int = -100
  val MaxValue: Int = 100
  val ConstMinValue: Int = 1
  val ConstMaxValue: Int = 10
  
  val OperatorList: List[Operator] = Plus :: Minus :: Mul :: Div :: Mod ::
	  	LeftShift :: ARightShift :: URightShift :: Nil
  
  private val arguments = (MinValue to MaxValue).toList
  private val random = new Random(System.currentTimeMillis)

  private val pairs: Iterator[(Operator, Operator)] = for {
      comb <- Language.OperatorList.combinations(2)
      a <- comb.headOption
      b <- comb.tail.headOption
      if (a.prio != b.prio && !(a.safe == true && b.safe == true))
    } yield (a,b)
  
  val AllLanguages: Map[Int, (Operator, Operator)] = ListMap.empty ++ pairs
    .zipWithIndex
    .map(t => (t._2 + 1, t._1))

  def randomFunction(no: LangNo): (Function, List[(Int, Int)]) = {
    def constVal() = ConstMinValue + random.nextInt(ConstMaxValue - ConstMinValue + 1)
    def genAscii(from: Char, to: Char) = (from + random.nextInt(to - from + 1)).toChar
    def genChar: Char = genAscii('a', 'z')
    def genNum: Char = genAscii('0', '9')
    val argName: String =
      ((1 to (1 + random.nextInt(3))).
      	foldRight(List(genChar))((_, acc) =>
      	  acc ++ List(if (random.nextBoolean) genChar else genNum))).mkString
    val ops =
      if (random.nextBoolean) AllLanguages(no)
      else AllLanguages(no).swap
    val (const1, const2) = (constVal(), constVal())
    val confs =
      if (random.nextBoolean) List(Argument(), Argument(), Constant(const1))
      else List(Constant(const1), Constant(const2), Argument())
    val confsPermutations = confs.permutations.toList
    val conf = confsPermutations(random.nextInt(confsPermutations.size))
    val f = new Function(argName, (conf(0), ops._1, conf(1), ops._2, conf(2)))
    val values = arguments.
      map(v => (v, f(v))).
      flatMap({
        case (a, Some(v)) => List((a, v))
        case _ => List()
      }).toList
    (f, values)
  }

}
package vipo.guess.domain

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalacheck.Prop.{forAll, BooleanOperators}
import vipo.guess.domain._
import vipo.guess.domain.Language._
import org.scalacheck.Arbitrary
import org.scalacheck.Properties

class Language19Properties extends Properties("Function") {

  val ops = AllLanguages(19)
  
  implicit val constGenerator: Arbitrary[Constant] = Arbitrary {
    for {
      c <- choose(0, 10)
    } yield (new Constant(c))
  }

  implicit val operatorPairGenerator: Arbitrary[(Operator, Operator)] = Arbitrary {
    for {
      b <- arbitrary[Boolean]
    } yield(if (b) ops else ops.swap)
  }
  
  implicit val functionGenerator: Arbitrary[Function] = Arbitrary {
    def containsArgumentAndConstant(conf: List[Boolean]) =
      conf.contains(true) && conf.contains(false)
    val arg = Argument()
    for {
      (op1, op2) <- arbitrary[(Operator, Operator)]
      consts <- containerOfN[List, Constant](3, arbitrary[Constant])
      conf <- containerOfN[List, Boolean](3, arbitrary[Boolean]) suchThat containsArgumentAndConstant
    } yield {
      val operands: List[Operand] = conf.zip(consts).map(a => if (a._1) a._2 else Argument())
      new Function("x", (operands(0), op1, operands(1), op2, operands(2)))
    }
  }

  def hasDivByZero(f: Function): Boolean = f.exp match {
    case (_, _, _, Mod, Constant(0)) => true
    case (_, Mod, Constant(0), _, _) => true
    case _ => false
  }
  
  property("parser") = forAll { (a: Int, f: Function) =>
    val p = Function.parse(f.toString).right.get
    p(a) == f(a)
  }

  property("divByZero") = forAll { (a: Int, f: Function) =>
    (hasDivByZero(f)) ==> (f(a) == None)
  }
  
}
package vipo.guess.domain

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalatest.FlatSpec

object FunctionSpec {
  val Tries = 100;
}

class FunctionSpec extends FlatSpec {
  import FunctionSpec._
  
  "Any function" should "be printed and parsed back" in {
  
    for (lang <- Language.AllLanguages.keySet) {
      for (_ <- 1 to Tries) {
        val original: Function = Language.randomFunction(lang)._1
        val parsed: Function = Function.parse(original.toString).right.get
        println(parsed)
      }
    }
  }

}
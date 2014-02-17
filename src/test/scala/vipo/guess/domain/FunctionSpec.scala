package vipo.guess.domain

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalatest.FlatSpec
import org.scalautils.Pass
import org.scalatest.Matchers

object FunctionSpec {
  val Tries = 10;
}

class FunctionSpec extends FlatSpec with Matchers {
  import FunctionSpec._
  import vipo.guess.domain.{Language, Challenge}
    
  for (lang <- Language.AllLanguages.keySet) {
    s"Any function of language ${lang}" should "be parsed back and evaluated correctly" in {
      for (_ <- 1 to Tries) {
        val original: Function = Language.randomFunction(lang)
        val parsed: Function = Function.parse(original.toString).right.get
        for (arg <- Language.MinValue to Language.MaxValue) {
          original(arg) should be (parsed(arg))
          Challenge.checkGuess(original.toString, original).right.get should be (true)
          Challenge.checkGuess(parsed.toString, parsed).right.get should be (true)
        }
      }
    }
  }

}
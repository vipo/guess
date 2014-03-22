package vipo.guess.domain

import org.scalatest._

class Language19Behavior extends FeatureSpec with GivenWhenThen {
  
  feature(s"Function acts correctly in acceptable argument range") {
    scenario("Argument 1 is passed to function") {

      Given("a function is parsed correctly")
      val f: Either[String, Function] = Function.parse("(gv: Int) => 1 >>> 8 % gv")
      assert(f.isRight)

      When("I pass it an argument 1")
      val v: Option[Int] = f.right.get(1)

      Then("Returned value is 1")
      assert(v == Some(1))
    }
    scenario("Argument 0 is passed to function") {

      Given("a function is parsed correctly")
      val f: Either[String, Function] = Function.parse("(gv: Int) => 1 >>> 8 % gv")
      assert(f.isRight)

      When("I pass it an argument 0")
      val v: Option[Int] = f.right.get.apply(0)

      Then("Returned value is None")
      assert(v == None)
    }
  }
}
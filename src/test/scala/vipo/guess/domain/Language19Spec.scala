package vipo.guess.domain

import org.scalatest._

import vipo.guess.domain.Function._

class Language19Spec extends FlatSpec with Matchers {
 
  val f1: Function = parse("(g5d: Int) => 7 >>> g5d % 4").right.get
  val f2: Function = parse("(wvb: Int) => wvb % wvb >>> 8").right.get
  
  s"Function $f1" should "not be defined for argument = -1" in {
    f1(-1) shouldBe empty
  }
  
  it should "be 7 for argument = -4" in {
    f1(-4) shouldBe Some(7)
    
  }
  
  s"Function $f2" should "not be defined for argument = 0" in {
    f2(0) shouldBe empty
  }

}
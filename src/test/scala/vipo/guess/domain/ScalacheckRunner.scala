package vipo.guess.domain

import org.scalatest.FunSuite

class ScalacheckRunner extends FunSuite {

  test("Scalacheck: Language19Properties") {
    new Language19Properties().check(1000)
  }
}
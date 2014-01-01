package vipo.guess.domain

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import scala.collection.immutable.ListMap

import vipo.guess.domain.Language._

class LanguageSpec extends FlatSpec with Matchers {

  "AllLanguages" should "return a map with fixed values" in {
    val map = Language.AllLanguages
    map should be (
      ListMap(
        1 -> (Plus, Mul),
        2 -> (Plus, Div),
        3 -> (Plus, Mod),
        4 -> (Minus, Mul),
        5 -> (Minus, Div),
        6 -> (Minus, Mod),
        7 -> (Plus, ShiftLeft),
        8 -> (Plus, ShiftRight),
        9 -> (Minus, ShiftLeft),
        10 -> (Minus, ShiftRight),
        11 -> (Mul, ShiftLeft),
        12 -> (Mul, ShiftRight),
        13 -> (Div, ShiftLeft),
        14 -> (Div, ShiftRight),
        15 -> (Mod, ShiftLeft),
        16 -> (Mod, ShiftRight)
      )
    )
  }

}
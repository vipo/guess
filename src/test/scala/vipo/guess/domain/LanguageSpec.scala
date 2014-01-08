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
        7 -> (Plus, LeftShift),
        8 -> (Plus, ARightShift),
        9 -> (Minus, LeftShift),
        10 -> (Minus, ARightShift),
        11 -> (Mul, LeftShift),
        12 -> (Mul, ARightShift),
        13 -> (Div, LeftShift),
        14 -> (Div, ARightShift),
        15 -> (Mod, LeftShift),
        16 -> (Mod, ARightShift)
      )
    )
  }

}
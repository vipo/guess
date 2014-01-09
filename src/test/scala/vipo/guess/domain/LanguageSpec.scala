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
        1 -> (Plus, Div),
        2 -> (Plus, Mod),
        3 -> (Minus, Div),
        4 -> (Minus, Mod),
        5 -> (Plus, LeftShift),
        6 -> (Plus, ARightShift),
        7 -> (Plus, URightShift),
        8 -> (Minus, LeftShift),
        9 -> (Minus, ARightShift),
        10 -> (Minus, URightShift),
        11 -> (Div, LeftShift),
        12 -> (Div, ARightShift),
        13 -> (Div, URightShift),
        14 -> (Mod, LeftShift),
        15 -> (Mod, ARightShift),
        16 -> (Mod, URightShift)
      )
    )
  }

}
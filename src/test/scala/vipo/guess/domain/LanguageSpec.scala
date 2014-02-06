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
        1  -> (Plus, Div),
        2  -> (Plus, Mod),
        3  -> (Plus, LeftShift),
        4  -> (Plus, ARightShift),
        5  -> (Plus, URightShift),
        6  -> (Minus, Div),
        7  -> (Minus, Mod),
        8  -> (Minus, LeftShift),
        9  -> (Minus, ARightShift),
        10 -> (Minus, URightShift),
        11 -> (Mul, LeftShift),
        12 -> (Mul, ARightShift),
        13 -> (Mul, URightShift),
        14 -> (Div, LeftShift),
        15 -> (Div, ARightShift),
        16 -> (Div, URightShift),
        17 -> (Mod, LeftShift),
        18 -> (Mod, ARightShift),
        19 -> (Mod, URightShift)
      )
    )
  }
}
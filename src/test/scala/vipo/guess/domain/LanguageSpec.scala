package vipo.guess.domain

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import scala.collection.immutable.ListMap

class LanguageSpec extends FlatSpec with Matchers {

  "AllLanguages" should "return a map with fixed values" in {
    val map = Language.AllLanguages
    map should be (
      ListMap(
        1 -> ("+","*"),
        2 -> ("+","/"),
        3 -> ("+","%"),
        4 -> ("-","*"),
        5 -> ("-","/"),
        6 -> ("-","%"),
        7 -> ("+","<<"),
        8 -> ("+",">>"),
        9 -> ("-","<<"),
        10 -> ("-",">>"),
        11 -> ("*","<<"),
        12 -> ("*",">>"),
        13 -> ("/","<<"),
        14 -> ("/",">>"),
        15 -> ("%","<<"),
        16 -> ("%",">>")
      )
    )
  }

}
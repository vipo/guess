import vipo.guess.domain.Language

object Playground {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val map = Language.AllLanguages                 //> map  : Map[Int,(vipo.guess.domain.Operator, vipo.guess.domain.Operator)] = M
                                                  //| ap(1 -> (+,*), 2 -> (+,/), 3 -> (+,%), 4 -> (-,*), 5 -> (-,/), 6 -> (-,%), 7
                                                  //|  -> (+,<<), 8 -> (+,>>), 9 -> (-,<<), 10 -> (-,>>), 11 -> (*,<<), 12 -> (*,>
                                                  //| >), 13 -> (/,<<), 14 -> (/,>>), 15 -> (%,<<), 16 -> (%,>>))

}
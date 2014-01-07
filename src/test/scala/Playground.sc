import vipo.guess.domain._

object Playground {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val map = Language.AllLanguages                 //> map  : scala.collection.immutable.Map[Int,(vipo.guess.domain.Operator, vipo.
                                                  //| guess.domain.Operator)] = Map(1 -> (+,*), 2 -> (+,/), 3 -> (+,%), 4 -> (-,*)
                                                  //| , 5 -> (-,/), 6 -> (-,%), 7 -> (+,<<), 8 -> (+,>>), 9 -> (-,<<), 10 -> (-,>>
                                                  //| ), 11 -> (*,<<), 12 -> (*,>>), 13 -> (/,<<), 14 -> (/,>>), 15 -> (%,<<), 16 
                                                  //| -> (%,>>))

  val f = new Function("a", (Argument(), Plus, Argument(), Mul, Constant(5)))
                                                  //> f  : vipo.guess.domain.Function = (a: Int) => a + a * 5
  f(4)                                            //> res0: Int = 24
}
import vipo.guess.domain._

object Playground {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val map = Language.AllLanguages                 //> map  : scala.collection.immutable.Map[Int,(vipo.guess.domain.Operator, vipo.
                                                  //| guess.domain.Operator)] = Map(1 -> (+,/), 2 -> (+,%), 3 -> (-,/), 4 -> (-,%)
                                                  //| , 5 -> (+,<<), 6 -> (+,>>), 7 -> (+,>>>), 8 -> (-,<<), 9 -> (-,>>), 10 -> (-
                                                  //| ,>>>), 11 -> (*,<<), 12 -> (*,>>), 13 -> (*,>>>), 14 -> (/,<<), 15 -> (/,>>)
                                                  //| , 16 -> (/,>>>), 17 -> (%,<<), 18 -> (%,>>), 19 -> (%,>>>))
  
  

  val f = new Function("a", (Argument(), Div, Argument(), Mul, Constant(5)))
                                                  //> f  : vipo.guess.domain.Function = (a: Int) => a / a * 5
  f(1)                                            //> res0: Option[Int] = Some(5)
}
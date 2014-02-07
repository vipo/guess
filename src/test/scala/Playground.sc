import vipo.guess.domain._

object Playground {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val map = Language.AllLanguages                 //> map  : scala.collection.immutable.Map[Int,(vipo.guess.domain.Operator, vipo.
                                                  //| guess.domain.Operator)] = Map(1 -> (+,/), 2 -> (+,%), 3 -> (+,<<), 4 -> (+,>
                                                  //| >), 5 -> (+,>>>), 6 -> (-,/), 7 -> (-,%), 8 -> (-,<<), 9 -> (-,>>), 10 -> (-
                                                  //| ,>>>), 11 -> (*,<<), 12 -> (*,>>), 13 -> (*,>>>), 14 -> (/,<<), 15 -> (/,>>)
                                                  //| , 16 -> (/,>>>), 17 -> (%,<<), 18 -> (%,>>), 19 -> (%,>>>))
  val f = Function.parse(" ( d: Int ) => d + 7 >>> 9")
                                                  //> f  : Either[String,vipo.guess.domain.Function] = Right((d: Int) => d + 7 >>>
                                                  //|  9)
}
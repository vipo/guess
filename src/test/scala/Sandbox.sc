object Sandbox {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  trait Queue[+A] {
    def put[T >: A](el: T): Queue[T]
    def head: A
    def tail: Queue[A]
  }

	class BatchQueue[+A] private (_f: List[A], _r: List[A]) extends Queue[A]{
	  
	  val (f, r) = if (_f.isEmpty) (_r.reverse, List()) else (_f, _r)
	  
	  def this() = this (List(), List())
	  
	  def put[T >: A](el: T): Queue[T] = new BatchQueue(f, el :: r)
	  
	  def head: A =
	    if (f.isEmpty) throw new NoSuchElementException
	    else f.head

	  def tail: Queue[A] =
	    if (f.isEmpty) throw new NoSuchElementException
	    else new BatchQueue(f.tail, r)
	    
	  override def toString: String = (f, r).toString
	}

  val q = new BatchQueue[Int]().put(1).put(2).put(3)
                                                  //> q  : Sandbox.Queue[Int] = (List(1),List(3, 2))
  q.tail                                          //> res0: Sandbox.Queue[Int] = (List(2, 3),List())
  
  
  trait Human
  class Programmer extends Human
  class ScalaProgrammer extends Programmer
  class ClojureProgrammer extends Programmer
  
  class ScalaProgrammerQueue[A <: ScalaProgrammer] extends BatchQueue[A]
  
  new ScalaProgrammerQueue().put(new ScalaProgrammer).put(new Programmer)
                                                  //> res1: Sandbox.Queue[Sandbox.Programmer] = (List(Sandbox$$anonfun$main$1$Sca
                                                  //| laProgrammer$1@6a8ff0ca),List(Sandbox$$anonfun$main$1$Programmer$1@1009946e
                                                  //| ))
  
  val spq = new BatchQueue[ScalaProgrammer]().put(new ScalaProgrammer).put(new ScalaProgrammer())
                                                  //> spq  : Sandbox.Queue[Sandbox.ScalaProgrammer] = (List(Sandbox$$anonfun$main
                                                  //| $1$ScalaProgrammer$1@64efc9fb),List(Sandbox$$anonfun$main$1$ScalaProgrammer
                                                  //| $1@3ab5b182))
  val pq: Queue[Programmer] = new BatchQueue[Programmer]().put(new Programmer).put(new ScalaProgrammer)
                                                  //> pq  : Sandbox.Queue[Sandbox.Programmer] = (List(Sandbox$$anonfun$main$1$Pro
                                                  //| grammer$1@6e98ebea),List(Sandbox$$anonfun$main$1$ScalaProgrammer$1@79a8885f
                                                  //| ))
  val hq: Queue[Human] = pq                       //> hq  : Sandbox.Queue[Sandbox.Human] = (List(Sandbox$$anonfun$main$1$Programm
                                                  //| er$1@6e98ebea),List(Sandbox$$anonfun$main$1$ScalaProgrammer$1@79a8885f))
  new Programmer :: List(new ScalaProgrammer)     //> res2: List[Sandbox.Programmer] = List(Sandbox$$anonfun$main$1$Programmer$1@
                                                  //| 7842f3a9, Sandbox$$anonfun$main$1$ScalaProgrammer$1@738b19ed)
  
}
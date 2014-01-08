package vipo.guess.domain

class Function(val argName: String,
    val exp: Tuple5[Operand, Operator, Operand, Operator, Operand]) {
  
  def apply(arg: Int): Option[Int] = {
    object SimpleExp {
      def value(o: Operand): Int = o match {
        case Constant(c) => c
        case Argument() => arg
      }
      def apply(exp: (Operand, Operator, Operand)): Option[Constant] =
        if (value(exp._3) == 0 && (exp._2 == Div || exp._2 == Mod)) None
        else Some(Constant(exp._2(value(exp._1), value(exp._3))))
    }
    exp match {
      case (v1, o1, v2, o2, v3) if o1.prio >= o2.prio => for {
        in <- SimpleExp(v1, o1, v2)
        out <- SimpleExp(in, o2, v3)
      } yield (out.value)
      case (v1, o1, v2, o2, v3) => for {
        in <- SimpleExp(v2, o2, v3)
        out <- SimpleExp(v1, o1, in)
      } yield (out.value)
    }
  }

  override def toString(): String = {
    def render(t: Any) = t match {
      case Argument() => argName
      case t => t.toString()
    }
    val asSeq = exp.productIterator.toSeq
    s"(${argName}: Int) => ${asSeq.map(render(_)).mkString(" ")}"
  }

}
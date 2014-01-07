package vipo.guess.domain

class Function(val argName: String,
    val exp: Tuple5[Operand, Operator, Operand, Operator, Operand]) {
  
  def apply(arg: Int): Int = {
    object SimpleExp {
      def value(o: Operand): Int = o match {
        case Constant(c) => c
        case Argument() => arg
      }
      def apply(exp: (Operand, Operator, Operand)): Constant =
        Constant(exp._2(value(exp._1), value(exp._3)))
    }
    exp match {
      case (v1, o1, v2, o2, v3) if o1.prio >= o2.prio =>
        SimpleExp(SimpleExp(v1, o1, v2), o2, v3).value
      case (v1, o1, v2, o2, v3) =>
        SimpleExp(v1, o1, SimpleExp(v2, o2, v3)).value
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
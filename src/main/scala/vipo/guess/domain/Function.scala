package vipo.guess.domain

class Function(val argName: String, val argValue: Int,
    val exp: Tuple5[Operand, Operator, Operand, Operator, Operand]) {

  private class SimpleExp(val exp: Tuple3[Operand, Operator, Operand]) {
    def evaluate(): Constant = Constant(exp._2.evaluate(value(exp._1), value(exp._3)))

    private def value(o: Operand): Int = o match {
      case Constant(c) => c
      case Value() => argValue
    }
  }
  
  def evaluate(): Int = 42
}
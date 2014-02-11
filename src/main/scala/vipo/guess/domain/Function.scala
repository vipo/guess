package vipo.guess.domain

import scala.util.parsing.combinator.JavaTokenParsers

class FunctionParser extends JavaTokenParsers {
  import vipo.guess.domain.Language.OperatorList

  def operator: Parser[Operator] = OperatorList.tail.foldRight(
    OperatorList.head.view ^^ (_ => OperatorList.head))(
    (el, acc) => acc | (el.view ^^ (_ => el)))

  def const: Parser[Constant] = """\d+""".r ^^ {case s => Constant(s.toInt)}
  
  def arg(Name: String): Parser[Operand] = ident ^? ({case Name => Argument()},
    (a => s"unknown identifier ${a}, known one is ${Name}"))

  def operand(argName: String): Parser[(String, Operand)] = (const | arg(argName)) ^^ {v => (argName, v)}

  def formalArg: Parser[String] = "(" ~> ident <~ ":"  ~ "Int" ~ ")" ~ "=>"
  
  def exp: Parser[Function] = (formalArg into { arg => operand(arg) ~ operator ~ operand(arg) ~ operator ~ operand(arg) } ) ^^ {
    case t1 ~ o2 ~ t3 ~ o4 ~ t5 => new Function(t1._1, (t1._2, o2, t3._2, o4, t5._2))
  }

}

object Function extends FunctionParser {
  
  def parse(str: String): Either[String, Function] = parseAll(exp, str) match {
    case Success(d, _) => Right(d)
    case Error(msg, _) => Left(msg)
    case Failure(msg, _) => Left(msg)
  }

}
@SerialVersionUID(42L)
class Function(val argName: String,
    val exp: Tuple5[Operand, Operator, Operand, Operator, Operand]) extends Serializable {
  
  def apply(arg: Int): Option[Int] = {
    object SimpleExp {
      def value(o: Operand): Int = o match {
        case Constant(c) => c
        case Argument() => arg
      }
      def apply(exp: (Operand, Operator, Operand)): Option[Constant] = {
        val v1 = value(exp._1)
        val v2 = value(exp._3)
        if (!exp._2.validate(v1, v2)) None
        else Some(Constant(exp._2(v1, v2)))
      }
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
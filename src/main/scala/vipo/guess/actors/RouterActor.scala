package vipo.guess.actors

import akka.actor._
import spray.routing._
import scala.xml._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.routing.directives.DetachMagnet.fromUnit
import vipo.guess.domain.Language
import vipo.guess.domain.Operator

object RouterActor {
  val Lang = "lang"
  val List = "list"
  val Gen = "gen"
  val LangListPath = s"/${Lang}/${List}"
  val LangPath = s"/${Lang}"
  val LangNoPath = {no: Int => s"/${Lang}/${no}"}
  val LangNoGenPath = {no: Int => s"${LangNoPath(no)}/${Gen}"}
}

class RouterActor extends HttpServiceActor with ActorLogging {
  import RouterActor._

  def receive = runRoute {
    get {
      pathSingleSlash {
        complete(index)
      } ~
      pathPrefix(Lang) {
        pathPrefix(IntNumber) { int =>
          path(Gen){
            complete(index)
          } ~
          pathEnd {
            complete(langNo(int))
          }
        } ~
        path(List) {
          complete(list)
        } ~
        pathEnd {
          complete(lang)
        }
      }
    }
  }

  val index = 
    <html>
      <body>
        <h1>Say hello</h1>
        <h2>...to this piece of software designed specially for Scala course
		  at <a href="http://mif.vu.lt">VU MIF</a>!</h2>
        <h3>Some usefull links to start with:</h3>
        <ul>
          <li>{<a>Common requirements for all languages</a> % Attribute(None, "href", Text(LangPath), Null)}</li>
          <li>{<a>List of all languages</a> % Attribute(None, "href", Text(LangListPath), Null)}</li>
        </ul>
      </body>
    </html>

  val list = 
    <html>
      <body>
        <h1>List of available languages:</h1>
        <ul>
          {Language.AllLanguages.map { case (k,_) =>
            <li>
              {<a>Language {k}</a> % Attribute(None, "href", Text(LangNoPath(k)), Null)}
            </li>
          }}
        </ul>
      </body>
    </html>

  def langNo(no: Int) = {
    val t = Language.AllLanguages(no)
    <html>
      <body>
        <h1>Language</h1>
        <h2>Operators:</h2>
        <ul>
          <li>{Text(t._1.fullDescription)}</li>
          <li>{Text(t._2.fullDescription)}</li>
        </ul>
        {<a>Here</a> % Attribute(None, "href", Text(LangNoGenPath(no)), Null)} you can find sample
          data to test your implementation with. It is generated
          on every request, so press F5 as often as you like.
          Page is machine semi-friendly: function text, empty line,
          space-separated arguments and values (pair per line).
      </body>
    </html>
  }
          
  val lang =
    <html>
      <body>
        <h1>Language</h1>
        <h2>... based on Scala syntax, not too complecated :)</h2>
        <h3>Requirements:</h3>
        <ul>
          <li>It is anonymous function with type "Int => Int"</li>
          <li>Types are not defined</li>
          <li>It starts with &#123; and ends with &#125;</li>
          <li>It is written in on line</li>
          <li>Argument's values must be in range [{Language.MinValue}; {Language.MaxValue}]</li>
          <li>Function's result must be in range [Int.MinValue; Int.MaxValue]</li>
          <li>The only type it is aware of is Int</li>
          <li>All the low-level integer overflows work as they do it in Scala</li>
          <li>Only 2 arithmetic operators (as in Scala) allowed on Ints are listed in your personal task,
            see {<a>this</a> % Attribute(None, "href", Text(LangListPath), Null)} for details</li>
          <li>Both of operators must be used</li>
          <li>There are no brackets in expressions</li>
          <li>At least one Int constant must be used from range [{Language.ConstMinValue}; {Language.ConstMaxValue}]</li>
          <li>Function's argument must be used at least once</li>
          <li>There must be 3 operands in a function</li>
          <li>Function is considered to illegal if division by zero is possible</li>
          <li>Word "return" is forbidden</li>
        </ul>
      </body>
    </html>
    
}

package vipo.guess

import akka.actor._
import spray.routing._
import scala.xml._
import spray.http.StatusCodes
import akka.util.Timeout

object Router {
  val Lang = "lang"
  val List = "list"
  val LangListPath = s"/${Lang}/${List}"
  val LangPath = s"/${Lang}"
}

class Router extends HttpServiceActor with ActorLogging {

  def receive = runRoute {
    get {
      pathSingleSlash {
        complete(index)
      } ~
      pathPrefix(Router.Lang) {
        path(IntNumber) { int =>
          complete(if (int % 2 == 0) "even ball" else "odd ball")
        } ~
        path(Router.List) {
          complete("PONG")
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
          <li>{<a>Common requirements for all languages</a> % Attribute(None, "href", Text(Router.LangPath), Null)}</li>
          <li>{<a>List of all languages</a> % Attribute(None, "href", Text(Router.LangListPath), Null)}</li>
        </ul>
      </body>
    </html>

  val lang =
    <html>
      <body>
        <h1>Language</h1>
        <h2>... based on Scala syntax, not too complecated :)</h2>
        <h3>Requirements:</h3>
        <ul>
          <li>It is anonymous function with type "Int => Int"</li>
          <li>It starts with &#123; and ends with &#125;</li>
          <li>Argument's values must be in range [-1000; 1000]</li>
          <li>Function's result must be in range [Int.MinValue; Int.MaxValue]</li>
          <li>The only type it is aware of is Int</li>
          <li>All the low-level integer overflows work as they do it in Scala</li>
          <li>The only 2 arithmetic operators (as in Scala) allowed on Ints are listed in your personal task,
            see {<a>this</a> % Attribute(None, "href", Text(Router.LangListPath), Null)} for details</li>
          <li>There are no brackets in expressions</li>
          <li>There can be locally defined values (<b>val</b>)</li>
          <li>Int-type constants can be used (i.e. -43, 0)</li>
          <li>Any constant can be used or function's argument can refered (together) only for 4 times in total</li>
          <li>There can be any quontity of operators in the function</li>
          <li>Word "return" is forbidden</li>
        </ul>
      </body>
    </html>
    
}

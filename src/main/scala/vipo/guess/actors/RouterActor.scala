package vipo.guess.actors

import scala.xml._
import akka.actor._
import akka.pattern.ask
import spray.routing._
import vipo.guess.Bootstrap.{MasterKey, Tokens, Stats, Challenges,
  ExecutionContext, DefaultTimeout}
import vipo.guess.domain.Language._
import vipo.guess.domain.Operator
import scala.concurrent.Promise
import scala.concurrent.Future
import vipo.guess.domain.Challenge.ChallengeId
import vipo.guess.domain.Function
import sun.org.mozilla.javascript.ast.Yield

object RouterActor {
  val Lang = "lang"
  val List = "list"
  val Gen = "gen"
  val Token = "token"
  val Challenge = "challenge"
  val LangListPath = s"/${Lang}/${List}"
  val LangPath = s"/${Lang}"
  val LangNoPath = {no: LangNo => s"/${Lang}/${no}"}
  val LangNoGenPath = {no: LangNo => s"${LangNoPath(no)}/${Gen}?${Token}=replace_me_with_your_token"}
}

class RouterActor extends HttpServiceActor with ActorLogging {
  import RouterActor._

  def receive = runRoute {
    get {
      pathSingleSlash {
        complete(index)
      } ~
      pathPrefix(Lang) {
        pathPrefix(IntNumber) { no =>
          pathPrefix(Gen) {
            path(Challenge) {
              pathEnd {
                parameter(Token) { token =>
                  if (token == MasterKey) complete(generateChallenge(no))
                  else reject()
                }
              }
            } ~
            pathEnd {
              parameter(Token) { token =>
                if (token == Tokens(no)) complete(generateSample(no))
                else reject()
              }
            }
          } ~
          pathEnd { ctx =>
            langNo(ctx, no)
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
          
  def generateSample(no: LangNo): String = {
    Stats ! SampleGenerated(no);
    val (f, values) = randomFunction(no)
    s"val f =\n  ${f}\n${values.map(t => s"f(${t._1}) == ${t._2}").mkString("\n")}"
  }

  def generateChallenge(no: LangNo): String = {
    val (f, _) = randomFunction(no)
    Challenges ! GenerateChallenge(no)
    s"${f}"
  }
  
  val list =
    <html>
      <body>
        <h1>List of available languages:</h1>
        <ul>
          {AllLanguages.map { case (k,_) =>
            <li>
              {<a>Language {k}</a> % Attribute(None, "href", Text(LangNoPath(k)), Null)}
            </li>
          }}
        </ul>
      </body>
    </html>

  def langNo(ctx: RequestContext, no: LangNo) = {
    def reply(times: Long, challenges: List[SingleChallengeData]) = {
      val t = AllLanguages(no)
      <html>
        <body>
          <h1>Language</h1>
          <h2>Operators:</h2>
          <ul>
            <li>{Text(t._1.fullDescription)}</li>
            <li>{Text(t._2.fullDescription)}</li>
          </ul>
          {<a>Here</a> % Attribute(None, "href", Text(LangNoGenPath(no)), Null)} you can find sample
            data to test your implementation with. Note, that you have to provide a token
            in the url. Data is generated on every request, so press F5 as often as you like.
            Page is machine semi-friendly, it was generated {times} times.
          <h2>Challenges:</h2>
          <ul>
            {challenges.map(d =>
              <li>ChallengeId: {d.challengeId}, solved: {d.solved}</li>
            )}
          </ul>
        </body>
      </html>
    }
    val fut = for {
      samples <- (Stats ? GetSampleGeneratedTimes(no)).mapTo[Long]
      challenges <- (Challenges ? GetChallengesForLanguage(no)).mapTo[List[SingleChallengeData]]
    } yield(samples, challenges)
    fut.onSuccess {
      case (s, c) => ctx.complete(reply(s, c))
    }
  }
          
  val lang =
    <html>
      <body>
        <h1>Language</h1>
        <h2>... based on Scala syntax, not too complecated :)</h2>
        <h3>Requirements:</h3>
        <ul>
          <li>It is anonymous function with type "Int => Int"</li>
          <li>It is written in on line</li>
          <li>Argument's values must be in range [{MinValue}; {MaxValue}]</li>
          <li>Function's result must be in range [Int.MinValue; Int.MaxValue]</li>
          <li>The only type it is aware of is Int</li>
          <li>All the low-level integer overflows work as they do it in Scala</li>
          <li>Operator priorities are as in Scala</li>
          <li>Only 2 arithmetic operators (as in Scala) allowed on Ints are listed in your personal task,
            see {<a>this</a> % Attribute(None, "href", Text(LangListPath), Null)} for details</li>
          <li>Both of operators must be used</li>
          <li>There are no brackets in expressions</li>
          <li>At least one Int constant must be used from range [{ConstMinValue}; {ConstMaxValue}]</li>
          <li>Function's argument must be used at least once</li>
          <li>There must be 3 operands in a function</li>
          <li>Division by zero is undefined</li>
          <li>Any Right-shift or left-shift by negative value is undefined</li>
          <li>Word "return" is forbidden</li>
        </ul>
      </body>
    </html>
    
}

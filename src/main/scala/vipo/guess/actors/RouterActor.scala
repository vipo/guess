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
import spray.http.StatusCode
import vipo.guess.domain.Challenge.checkGuess

object RouterActor {
  val Lang = "lang"
  val List = "list"
  val Gen = "gen"
  val Token = "token"
  val Arg = "arg"
  val Challenge = "challenge"
  val LangListPath = s"/${Lang}/${List}"
  val LangPath = s"/${Lang}"
  val LangNoPath = {no: LangNo => s"/${Lang}/${no}"}
  val LangNoGenPath = {no: LangNo => s"${LangNoPath(no)}/${Gen}?${Token}=replace_me_with_your_token"}
}

class RouterActor extends HttpServiceActor with ActorLogging {
  import RouterActor._

  def receive = runRoute {
    post {
      pathPrefix(Lang) {
        pathPrefix(IntNumber) { langNo =>
          pathPrefix(Gen) {
            path(Challenge) {
              pathEnd {
                parameter(Token) { token =>
                  if (token == MasterKey) complete(generateChallenge(langNo))
                  else badToken
                }
              }
            }
          } ~
          pathPrefix(Challenge) {
            pathPrefix(IntNumber) { challengeId =>
              parameter(Token) { token =>
                if (token == Tokens(langNo)) {
                  entity(as[String]) { body =>
                    pathEnd { ctx =>
                      tryChallenge(ctx, langNo, challengeId, body)
                    }
                  }
                } else badToken
              }
            }
          }
        }
      }
    } ~
    get {
      pathSingleSlash {
        complete(index)
      } ~
      pathPrefix(Lang) {
        pathPrefix(IntNumber) { langNo =>
          pathPrefix(Gen) {
            pathEnd {
              parameter(Token) { token =>
                if (token == Tokens(langNo)) complete(generateSample(langNo))
                else badToken
              }
            }
          } ~
          pathPrefix(Challenge) {
            pathPrefix(IntNumber) { challengeId =>
              parameter(Arg) { funArg =>
                parameter(Token) { token =>
                  pathEnd { ctx =>
                    if (token == Tokens(langNo)) challengeValue(ctx, langNo, challengeId, funArg.toInt)
                    else badToken(ctx)
                  }
                }
              }
            }
          } ~
          pathEnd { ctx =>
            langSummary(ctx, langNo)
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

  def badToken(ctx: RequestContext): Unit = ctx.complete(StatusCode.int2StatusCode(403), "Bad token")
  
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
    Stats ! SampleGenerated(no)
    val f = randomFunction(no)
    val allVals: List[(Int, Option[Int])] = valuesForFunction(f)
    val validVals: List[(Int, Int)] = allVals.flatMap(t => t._2 match {
      case Some(v) => (t._1, v) :: Nil
      case None => Nil
    })
    s"val f =\n  ${f}\n${validVals.map(t => s"f(${t._1}) == ${t._2}").mkString("\n")}"
  }

  def generateChallenge(no: LangNo): String = {
    Challenges ! GenerateChallenge(no)
    "OK"
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

  def withChallengeData(langNo: LangNo, challengeId: ChallengeId, f: (Option[SingleChallengeData]) => Unit): Unit = {
    ((Challenges ? GetChallengesForLanguage(langNo)).mapTo[List[SingleChallengeData]]).onSuccess {
      case challenges => f(challenges.find(_.challengeId == challengeId))
    }
  }
  
  def tryChallenge(ctx: RequestContext, langNo: LangNo, challengeId: ChallengeId, funBody: String): Unit =
    withChallengeData(langNo, challengeId, (challenge: Option[SingleChallengeData]) =>
      challenge.map(c => (c.solved, checkGuess(funBody, c.function))) match {
        case None => ctx.reject()
        case Some((true, _)) => ctx.complete(StatusCode.int2StatusCode(410), "Challenge already solved")
        case Some((false, Left(msg))) => ctx.complete(StatusCode.int2StatusCode(400), msg)
        case Some((false, Right(equal))) => {
          Stats ! ChallengeQueried(challengeId)
          if (equal) {
            Challenges ! MarkAsSolved(challengeId)
            ctx.complete("OK")
          } else ctx.complete(StatusCode.int2StatusCode(400), "Bad guess!")
        }
      }
    )
  
  def challengeValue(ctx: RequestContext, langNo: LangNo, challengeId: ChallengeId, funArg: Int): Unit =
    withChallengeData(langNo, challengeId, (challenge: Option[SingleChallengeData]) => challenge match {
      case None => ctx.reject()
      case Some(challenge) => {
        Stats ! ChallengeQueried(challengeId)
        challenge.function(funArg) match {
          case None => ctx.complete(StatusCode.int2StatusCode(400), "Function is undefined for this arg")
          case Some(v) => ctx.complete(v.toString)
        }
      }
    }
  )
  
  def langSummary(ctx: RequestContext, no: LangNo): Unit = {
    def reply(times: Long, challenges: List[(SingleChallengeData, Long)]) = {
      val t = AllLanguages(no)
      <html>
        <body>
          <h1>Language</h1>
          <h2>Operators</h2>
          <ul>
            <li>{Text(t._1.fullDescription)}</li>
            <li>{Text(t._2.fullDescription)}</li>
          </ul>
          {<a>Here</a> % Attribute(None, "href", Text(LangNoGenPath(no)), Null)} you can find sample
            data to test your implementation with. Note, that you have to provide a token
            in the url. Data is generated on every request, so press F5 as often as you like.
            Page is machine semi-friendly, it was generated {times} times.
          <h2>Challenges</h2>
          <ul>
            {challenges.map(d =>
              <li>ChallengeId: {d._1.challengeId}, {if (d._1.solved) s"${d._1.function}" else "not solved"}, times queried: {d._2}</li>
            )}
          </ul>
          <h3>Interface:</h3>
          <ul>
            <li>GET  /lang/$LANG/challenge/$CHALLENGE_ID?token=$TOKEN&amp;arg=$ARG &mdash; gets a value of
            challenge CHALLENGE_ID for language LANG for argument ARG. Curl command line example:
            <pre>curl -i "http://localhost:8888/lang/1/challenge/1?token=1&amp;arg=10"</pre>
            possible responses:<pre>
HTTP/1.1 200 OK
Server: spray-can/1.3-M2
Date: Mon, 17 Feb 2014 19:38:20 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 1

6</pre> or, (if function is not defined for the argument)<pre>
HTTP/1.1 400 Bad Request
Server: spray-can/1.3-M2
Date: Mon, 17 Feb 2014 20:19:45 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 34

Function is undefined for this arg
</pre></li>
            <li>POST /lang/$LANG/challenge/$CHALLENGE_ID?token=$TOKEN with function in request body  &mdash;
            tries to guess a function. Curl command line example:
            <pre>curl -i -X POST -d "(y: Int) => y + 6 / y" "http://localhost:8888/lang/1/challenge/24?token=1"</pre>
            possible responses: <pre>
HTTP/1.1 400 Bad Request
Server: spray-can/1.3-M2
Date: Mon, 17 Feb 2014 19:41:44 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 10

Bad guess!</pre> or (if you solved a challenge)<pre>
HTTP/1.1 200 OK
Server: spray-can/1.3-M2
Date: Mon, 17 Feb 2014 18:56:13 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 2

OK%
</pre>, or (if you try to solve a solved challenge)<pre>
HTTP/1.1 410 Gone
Server: spray-can/1.3-M2
Date: Mon, 17 Feb 2014 19:39:26 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 24

Challenge already solved
</pre></li>
          </ul>
        </body>
      </html>
    }
    (for {
      samples <- (Stats ? GetSampleGeneratedTimes(no)).mapTo[Long]
      challenges <- (Challenges ? GetChallengesForLanguage(no)).mapTo[List[SingleChallengeData]]
      times <- Future.sequence(challenges.map(c => (Stats ? GetChallengeQueriedTimes(c.challengeId)).mapTo[Long]))
    } yield(samples, challenges.zip(times))).onSuccess {
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
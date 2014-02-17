package vipo.guess.domain

import vipo.guess.domain.Language._

object Challenge {

  type ChallengeId = Int
  
  def checkGuess(chall: String, fun: Function): Either[String, Boolean] = Function.parse(chall) match {
    case Left(msg) => Left(msg)
    case Right(f) => {
      val funResults = valuesForFunction(fun)
      val challengeResults = valuesForFunction(f)
      Right(funResults == challengeResults)
    }
  }

}
package vipo.guess.domain

import vipo.guess.domain.Language._

object Challenge {

  type ChallengeId = Int
  
  def functionsAreEqual(fun: Function, body: String): Either[String, Boolean] = Function.parse(body) match {
    case Left(msg) => Left(msg)
    case Right(f) => Right(false)
  }

}
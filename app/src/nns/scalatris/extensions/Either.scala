package nns.scalatris.extensions

import indigo.shared.collections.Batch
import indigo.shared.{Outcome, Startup}

extension [L <: Throwable, R](e: Either[L, R])

  def toStartup: Startup[R] = e match
    case Left(l)  => Startup.Failure(l.toString)
    case Right(r) => Startup.Success(r)

  def toOutcome: Outcome[R] = e match
    case Left(l)  => Outcome.Error(l)
    case Right(r) => Outcome.Result(r, Batch.empty)

package nns.scalatris.extensions

import indigo.*
import indigo.shared.{ Startup, Outcome }

extension [T](o: Option[T])

  def toStartup: Startup[T] = o match {
    case Some(v) => Startup.Success(v)
    case None    => Startup.Failure("can't convert to StartupData")
  }

  def toOutcome: Outcome[T] = o match {
    case Some(v) => Outcome.Result(v, Batch.empty)
    case None => Outcome.Error(Exception("can't convert to Outcome"))
  }
  

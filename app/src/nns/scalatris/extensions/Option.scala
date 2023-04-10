package nns.scalatris.extensions

import indigo.shared.Startup

extension [T](o: Option[T])

  def toStartup: Startup[T] = o match {
    case Some(v) => Startup.Success(v)
    case None    => Startup.Failure("can't convert to StartupData")
  }

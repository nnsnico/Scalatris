package nns.scalatris.extensions

extension (b: Boolean)

  def fold[T](t: => T, f: => T): T = if (b) t else f

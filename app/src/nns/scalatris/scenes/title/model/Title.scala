package nns.scalatris.scenes.title.model

opaque type Title = String

object Title:
  def apply(s: String): Title = s

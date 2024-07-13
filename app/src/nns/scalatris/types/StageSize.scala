package nns.scalatris.types

import indigo.*

opaque type StageSize = BoundingBox

object StageSize:
  def apply(v: BoundingBox): StageSize = v

  extension (s: StageSize)
    def width = s.width
    def height = s.height
    def position = s.position
    def x = s.x
    def y = s.y

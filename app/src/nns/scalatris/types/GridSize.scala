package nns.scalatris.types

import indigo.*

opaque type GridSize = BoundingBox

object GridSize:
  def apply(v: BoundingBox): GridSize = v

package nns.scalatris.types

import indigoextras.geometry.BoundingBox

opaque type GridSize = BoundingBox

object GridSize:
  def apply(v: BoundingBox): GridSize = v

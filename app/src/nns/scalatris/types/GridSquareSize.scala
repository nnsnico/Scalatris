package nns.scalatris.types

opaque type GridSquareSize = Int

object GridSquareSize:
  def apply(i: Int): GridSquareSize = i

  extension (s: GridSquareSize)
    inline def toInt: Int = s

    inline def +(x: Double) = GridSquareSize(s + x.toInt)
    inline def *(x: Double) = GridSquareSize(s * x.toInt)

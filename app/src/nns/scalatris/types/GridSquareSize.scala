package nns.scalatris.types

opaque type GridSquareSize = Int

object GridSquareSize:
  def apply(i: Int): GridSquareSize = i

  extension (s: GridSquareSize)
    def toInt: Int       = s

    def +(x: GridSquareSize) = GridSquareSize(s + x)
    def +(x: Double)         = GridSquareSize(s + x.toInt)

    def *(x: GridSquareSize) = GridSquareSize(s * x)
    def *(x: Double)         = GridSquareSize(s * x.toInt)

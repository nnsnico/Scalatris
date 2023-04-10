package nns.scalatris.assets

import indigo.*
import indigo.shared.*
import indigoextras.geometry.BoundingBox
import nns.scalatris.GridSquareSize

final case class Grid(squareSize: GridSquareSize):

  def createStageBounds(
      gridSize: BoundingBox,
      offsetX: GridSquareSize = GridSquareSize(0),
      offsetY: GridSquareSize = GridSquareSize(0),
  ): Seq[Shape.Box] = for {
    x <- (0 to gridSize.width.toInt).toSeq
    y <- (0 to gridSize.height.toInt).toSeq
  } yield box((squareSize * x) + offsetX, (squareSize * y) + offsetY)

  private def box(x: GridSquareSize, y: GridSquareSize) = Shape.Box(
    Rectangle(Point(x.toInt, y.toInt), Size(squareSize.toInt, squareSize.toInt)),
    Fill.None,
    Stroke(1, RGBA.SeaGreen),
  )

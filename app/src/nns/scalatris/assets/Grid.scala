package nns.scalatris.assets

import indigo.*
import indigo.shared.*
import indigoextras.geometry.BoundingBox
import nns.scalatris.GridSquareSize
import indigo.logger.*

final case class Grid(
    x: Int = 0,
    y: Int = 0,
    gridSize: GridSquareSize,
    stageSize: BoundingBox,
):

  def createStageBounds: Seq[Shape.Box] = for {
    stagePosX <- (0 until stageSize.width.toInt).toSeq
    stagePosY <- (0 until stageSize.height.toInt).toSeq
  } yield drawBox(
    (gridSize * stagePosX) + x,
    (gridSize * stagePosY) + y,
  )

  private def drawBox(x: GridSquareSize, y: GridSquareSize) = Shape.Box(
    Rectangle(Point(x.toInt, y.toInt), Size(gridSize.toInt, gridSize.toInt)),
    Fill.None,
    Stroke(1, RGBA.SeaGreen),
  )

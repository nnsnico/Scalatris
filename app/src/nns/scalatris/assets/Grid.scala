package nns.scalatris.assets

import indigo.*
import indigo.logger.*
import indigo.shared.*
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.types.GridSquareSize.*
import nns.scalatris.types.{GridSquareSize, StageSize}

final case class Grid(
    gridSize: GridSquareSize,
    stageSize: StageSize,
):

  def createStageBounds: Seq[Shape.Box] = for {
    stagePosX <- (0 until stageSize.width.toInt).toSeq
    stagePosY <- (0 until stageSize.height.toInt).toSeq
  } yield drawBox(
    (gridSize * stagePosX) + stageSize.x,
    (gridSize * stagePosY) + stageSize.y,
  )

  private def drawBox(x: GridSquareSize, y: GridSquareSize) = Shape.Box(
    Rectangle(Point(x.toInt, y.toInt), Size(gridSize.toInt, gridSize.toInt)),
    Fill.None,
    Stroke(1, RGBA.SeaGreen),
  )

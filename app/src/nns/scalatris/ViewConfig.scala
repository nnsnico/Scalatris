package nns.scalatris

import indigo.*
import indigo.scenes.*
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.types.GridSquareSize.*
import nns.scalatris.types.{GridSize, GridSquareSize, StageSize}

final case class ViewConfig(
    gridSize: GridSize,
    stageSize: StageSize,
    gridSquareSize: GridSquareSize,
    magnificationLevel: Int,
    viewport: GameViewport,
):
  val horizontalCenter = (viewport.width / magnificationLevel) / 2
  val verticalCenter   = (viewport.height / magnificationLevel) / 2

object ViewConfig:
  final private val gridSquareSize     = GridSquareSize(12)
  final private val magnificationLevel = 2

  def default: ViewConfig =
    val gridSize                   = BoundingBox(width = 30, height = 30)
    val stageSize                  = BoundingBox(width = 10, height = 20)
    val viewport                   = GameViewport(
      width = ((gridSquareSize * gridSize.width) * magnificationLevel).toInt,
      height = ((gridSquareSize * gridSize.height) * magnificationLevel).toInt,
    )
    val horizontalCenter           = (viewport.width / magnificationLevel) / 2
    val stageHorizontalCenterStart = horizontalCenter - (
      gridSquareSize * stageSize.horizontalCenter
    ).toInt

    ViewConfig(
      gridSize = GridSize(gridSize),
      stageSize = StageSize(
        stageSize.copy(
          position = Vertex(
            x = stageHorizontalCenterStart,
            y = 0,
          ),
        ),
      ),
      gridSquareSize = gridSquareSize,
      magnificationLevel = magnificationLevel,
      viewport = viewport,
    )

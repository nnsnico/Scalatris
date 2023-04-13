package nns.scalatris

import indigo.*
import indigoextras.geometry.BoundingBox

opaque type GridSquareSize = Int

object GridSquareSize:
  def apply(i: Int): GridSquareSize = i

  extension (s: GridSquareSize)
    def toInt: Int                                 = identity(s)
    def *(v: GridSquareSize | Int): GridSquareSize = math.multiplyExact(s, v)
    def +(v: GridSquareSize | Int): GridSquareSize = math.addExact(s, v)

final case class ViewConfig(
    gridSize: BoundingBox,
    stageSize: BoundingBox,
    gridSquareSize: GridSquareSize,
    magnificationLevel: Int,
    viewport: GameViewport,
):
  val horizontalCenter = (viewport.width / magnificationLevel) / 2
  val verticalCenter   = (viewport.height / magnificationLevel) / 2

  val stageHorizontalCenter = horizontalCenter - (
    gridSquareSize * stageSize.horizontalCenter.toInt
  ).toInt

object ViewConfig:
  final private val gridSquareSize     = GridSquareSize(12)
  final private val magnificationLevel = 2

  def default: ViewConfig =

    val gridSize  = BoundingBox(width = 30, height = 20)
    val stageSize = BoundingBox(width = 10, height = 20)

    ViewConfig(
      gridSize = gridSize,
      stageSize = stageSize,
      gridSquareSize = gridSquareSize,
      magnificationLevel = magnificationLevel,
      viewport = GameViewport(
        ((gridSquareSize * gridSize.width.toInt) * magnificationLevel).toInt,
        ((gridSquareSize * gridSize.height.toInt) * magnificationLevel).toInt,
      ),
    )

package nns.scalatris

import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}

opaque type GridSquareSize = Int

object GridSquareSize:
  def apply(i: Int): GridSquareSize = i

  extension (s: GridSquareSize)

    def toInt: Int = identity(s)

    def *(v: GridSquareSize | Int | Double): GridSquareSize = v match
      case i: Int => math.multiplyExact(s, i)
      case d: Double => math.multiplyExact(s, d.toInt)

    def +(v: GridSquareSize | Int | Double): GridSquareSize = v match
      case i: Int => math.addExact(s, i)
      case d: Double => math.addExact(s, d.toInt)

final case class ViewConfig(
    gridSize: BoundingBox,
    stageSize: BoundingBox,
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

    val gridSize  = BoundingBox(width = 30, height = 20)
    val stageSize = BoundingBox(width = 10, height = 20)
    val viewport = GameViewport(
        ((gridSquareSize * gridSize.width.toInt) * magnificationLevel).toInt,
        ((gridSquareSize * gridSize.height.toInt) * magnificationLevel).toInt,
      )
    val horizontalCenter = (viewport.width / magnificationLevel) / 2
    val stageHorizontalCenterStart = horizontalCenter - (
      gridSquareSize * stageSize.horizontalCenter
    ).toInt

    ViewConfig(
      gridSize = gridSize,
      stageSize = stageSize.copy(
        position = Vertex(
          x = stageHorizontalCenterStart,
          y = 0,
        )
      ),
      gridSquareSize = gridSquareSize,
      magnificationLevel = magnificationLevel,
      viewport = GameViewport(
        ((gridSquareSize * gridSize.width.toInt) * magnificationLevel).toInt,
        ((gridSquareSize * gridSize.height.toInt) * magnificationLevel).toInt,
      ),
    )

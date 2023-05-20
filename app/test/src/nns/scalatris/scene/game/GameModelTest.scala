package nns.scalatris.scenes.game

import indigo.shared.{Outcome, Startup}
import munit.*
import nns.scalatris.{StartUpData, ViewConfig}
import nns.scalatris.model.Piece
import nns.scalatris.assets.Block
import nns.scalatris.assets.Block.materials
import indigoextras.geometry.Vertex

class GameModelTest extends munit.FunSuite {

  val viewConfig: ViewConfig = ViewConfig.default
  val blockMaterial          = Block.materials(viewConfig.gridSquareSize)

  val model = GameModel.init(
    viewConfig = viewConfig,
    blockMaterial = blockMaterial,
  )

  test("filterFilledPositionY should returned removable line position vertically if map equals with factorialWidth") {
    val map: Set[Piece] =
      Set(
        Piece.OKind(
          materials(viewConfig.gridSquareSize).head,
          position = Vertex(0, 0),
        ),
        Piece.OKind(
          materials(viewConfig.gridSquareSize).head,
          position = Vertex(2, 0),
        ),
        Piece.OKind(
          materials(viewConfig.gridSquareSize).head,
          position = Vertex(4, 0),
        ),
        Piece.OKind(
          materials(viewConfig.gridSquareSize).head,
          position = Vertex(6, 0),
        ),
        Piece.OKind(
          materials(viewConfig.gridSquareSize).head,
          position = Vertex(8, 0),
        ),
      )


    assertEquals(
      GameModel.filterFilledPositionY(map, viewConfig.stageSize.width),
      Set[Double](0, 1),
    )
  }

}

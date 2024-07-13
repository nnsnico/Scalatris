package nns.scalatris.scenes.game

import indigo.shared.{Outcome, Startup}
import indigoextras.geometry.Vertex
import munit.*
import nns.scalatris.assets.Block.materials
import nns.scalatris.assets.{Block, BlockMaterial}
import nns.scalatris.scenes.game.model.{Piece, PieceKind}
import nns.scalatris.{StartUpData, ViewConfig}

class GameModelTest extends munit.FunSuite {

  val viewConfig: ViewConfig = ViewConfig.default

  test(
    "filterFilledPositionY should returned removable line position vertically if map equals with factorialWidth",
  ) {
    val material        = BlockMaterial.Yellow(viewConfig.gridSquareSize)
    val map: Set[Piece] =
      Set(
        Piece
          .create(PieceKind.OKind(), material)
          .toOption
          .get
          .copy(position = Vertex(0, 0)),
        Piece
          .create(PieceKind.OKind(), material)
          .toOption
          .get
          .copy(position = Vertex(2, 0)),
        Piece
          .create(PieceKind.OKind(), material)
          .toOption
          .get
          .copy(position = Vertex(4, 0)),
        Piece
          .create(PieceKind.OKind(), material)
          .toOption
          .get
          .copy(position = Vertex(6, 0)),
        Piece
          .create(PieceKind.OKind(), material)
          .toOption
          .get
          .copy(position = Vertex(8, 0)),
      )

    assertEquals(
      GameModel.filterFilledPositionY(map, viewConfig.stageSize.width),
      Set[Double](0, 1),
    )
  }

}

package nns.scalatris.scenes.game.model

import cats.*
import cats.data.*
import cats.syntax.all.*
import munit.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.{Block, BlockMaterial}
import nns.scalatris.types.GridSquareSize

class PieceTest extends munit.FunSuite {
  val viewConfig = ViewConfig.default

  test(
    "createPieceFlow is `Right` when given materials and pieces are same length",
  ) {
    val materials = Block.materials(viewConfig.gridSquareSize)
    val p         = Piece.createPieceFlow(materials)

    assert(p.isRight)
  }

  test(
    "createPieceFlow is `Left` when given material is empty",
  ) {
    val materials = Seq.empty
    val p         = Piece.createPieceFlow(materials)

    assert(p.isLeft)
  }

  test(
    "createFromMaterials is not Empty when list of BlockMaterial is also not Empty",
  ) {
    val materials = Block.materials(viewConfig.gridSquareSize)
    val p         = Piece.createFromMaterials(materials)

    assert(!p.isEmpty)
  }

  test(
    "createFromMaterials is Empty when list of BlockMaterial is also Empty",
  ) {
    val p = Piece.createFromMaterials(Seq.empty)

    assert(p.isEmpty)
  }

  test(
    "create is `Left` when non match pattern with (PieceKind, BlockMaterial)",
  ) {
    val p = Piece.createPiece(
      PieceKind.I,
      BlockMaterial.Red(GridSquareSize(12)),
    )

    assert(p.isLeft)
  }

  test(
    "create is `Right` when match pattern with (PieceKind, BlockMaterial)",
  ) {
    val p = Piece.createPiece(
      PieceKind.I,
      BlockMaterial.SkyBlue(GridSquareSize(12)),
    )

    assert(p.isRight)
  }
}

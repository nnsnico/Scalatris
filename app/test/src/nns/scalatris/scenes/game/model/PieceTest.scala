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
    "init is `Left` when it is out of index",
  ) {
    val materials = Block.materials(viewConfig.gridSquareSize)
    val p         = Piece.init(materials, 999)

    assert(p.isLeft)
  }

  test(
    "init is `Right` when it is within index",
  ) {
    val materials = Block.materials(viewConfig.gridSquareSize)
    val p         = Piece.init(materials, 0)

    assert(p.isRight)
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
    val p = Piece.create(
      PieceKind.I,
      BlockMaterial.Red(GridSquareSize(12)),
    )

    assert(p.isLeft)
  }

  test(
    "create is `Right` when match pattern with (PieceKind, BlockMaterial)",
  ) {
    val p = Piece.create(
      PieceKind.I,
      BlockMaterial.SkyBlue(GridSquareSize(12)),
    )

    assert(p.isRight)
  }
}

package nns.scalatris.model

import munit.*
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.assets.Block
import nns.scalatris.ViewConfig

class PieceTest extends munit.FunSuite {
  val viewConfig = ViewConfig.default

  test(
    "createFromMaterials is not Empty when list of BlockMaterial is also not Empty"
  ) {
    val materials = Block.materials(viewConfig.gridSquareSize)
    val p = Piece.createFromMaterials(materials)

    assert(!p.isEmpty)
  }

  test(
    "createFromMaterials is Empty when list of BlockMaterial is also Empty"
  ) {
    val p = Piece.createFromMaterials(Seq.empty)

    assert(p.isEmpty)
  }
}

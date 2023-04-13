package nns.scalatris.assets

import indigo.*
import indigo.shared.*
import nns.scalatris.GridSquareSize

case class Position(val x: Int, val y: Int)

sealed trait Piece {
  piece =>

  val x: Int
  val y: Int
  val blockSize: GridSquareSize
  val material: BlockMaterial
  val localPos: Seq[Position]

  def createPiece(): Seq[Graphic[Material.Bitmap]] = for {
    pos <- localPos
  } yield material
    .bitmap
    .moveTo(
      x = ((blockSize * pos.x) + piece.x).toInt,
      y = ((blockSize * pos.y) + piece.y).toInt,
    )

}

final case class IKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material: BlockMaterial = SkyBlue(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(2, 0),
    Position(3, 0),
  )

final case class JKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material: BlockMaterial = Blue(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class LKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material: BlockMaterial = Orange(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class OKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material: BlockMaterial = Yellow(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class SKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material: BlockMaterial = Green(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(1, 0),
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class TKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material = Purple(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(0, 1),
    Position(1, 0),
    Position(1, 1),
    Position(2, 1),
  )

final case class ZKind(x: Int = 0, y: Int = 0, blockSize: GridSquareSize) extends Piece:
  override val material = Red(blockSize)

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(1, 1),
    Position(1, 2),
  )

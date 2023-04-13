package nns.scalatris.assets

import indigo.*
import indigo.shared.*
import nns.scalatris.GridSquareSize
import cats.implicits.*

final case class Position(val x: Int, val y: Int)

sealed trait Piece:
  val localPos: Seq[Position]

final case class IKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(2, 0),
    Position(3, 0),
  )

final case class JKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class LKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class OKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class SKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(1, 0),
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class TKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(0, 1),
    Position(1, 0),
    Position(1, 1),
    Position(2, 1),
  )

final case class ZKind(blockSize: GridSquareSize) extends Piece:

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(1, 1),
    Position(1, 2),
  )

object Piece:

  def fromBlockMaterial(
      initialPosition: Position,
      blockSize: GridSquareSize,
      blockMaterials: Seq[BlockMaterial],
  ): Seq[Graphic[Material.Bitmap]] = for {
    material <- blockMaterials
    piece    <- materialToPiece(material).toSeq
    localPos <- piece.localPos
  } yield material
    .bitmap
    .moveTo(
      x = ((blockSize * localPos.x) + initialPosition.x).toInt,
      y = ((blockSize * localPos.y) + initialPosition.y).toInt,
    )

  private def materialToPiece(material: BlockMaterial): Option[Piece] =
    material match {
      case Blue(size)    => JKind(blockSize = size).some
      case Green(size)   => SKind(blockSize = size).some
      case Red(size)     => ZKind(blockSize = size).some
      case Orange(size)  => LKind(blockSize = size).some
      case Purple(size)  => TKind(blockSize = size).some
      case SkyBlue(size) => IKind(blockSize = size).some
      case Yellow(size)  => OKind(blockSize = size).some
      case _             => none
    }

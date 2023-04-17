package nns.scalatris.model

import indigo.*
import indigo.shared.*
import nns.scalatris.GridSquareSize
import cats.syntax.all._
import cats.data._
import cats._
import scala.util.Random
import nns.scalatris.ViewConfig
import nns.scalatris.assets._

final case class Position(val x: Int, val y: Int)

sealed abstract class Piece(
    val position: Position,
    val direction: PieceDirection,
    val blockSize: GridSquareSize,
    val material: BlockMaterial,
):
  val localPos: Seq[Position]

  def update(gridSquareSize: GridSquareSize): Piece = this.direction match {
    case PieceDirection.Neutral => this
    case PieceDirection.Down    => this
    case PieceDirection.Left    =>
      val currentPosition = this.position
      Piece.move(
        piece = this,
        position = currentPosition.copy(
          x = currentPosition.x - gridSquareSize.toInt,
        ),
      )
    case PieceDirection.Right   =>
      val currentPosition = this.position
      Piece.move(
        piece = this,
        position = currentPosition.copy(
          x = currentPosition.x + gridSquareSize.toInt,
        ),
      )
  }

  def changeDirection(direction: PieceDirection): Piece = this match {
    case k: IKind =>
      k.copy(direction = direction)
    case k: JKind =>
      k.copy(direction = direction)
    case k: LKind =>
      k.copy(direction = direction)
    case k: OKind =>
      k.copy(direction = direction)
    case k: SKind =>
      k.copy(direction = direction)
    case k: TKind =>
      k.copy(direction = direction)
    case k: ZKind =>
      k.copy(direction = direction)
  }

final case class IKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(2, 0),
    Position(3, 0),
  )

final case class JKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class LKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class OKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class SKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(1, 0),
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class TKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 1),
    Position(1, 0),
    Position(1, 1),
    Position(2, 1),
  )

final case class ZKind(
    override val position: Position,
    override val direction: PieceDirection,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, direction, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(1, 1),
    Position(1, 2),
  )

object Piece:

  def init(x: Int, y: Int, blockMaterials: Seq[BlockMaterial]): Option[Piece] =
    for {
      index     <- Some(Random.nextInt(blockMaterials.length))
      pieceList <- fromBlockMaterial(x, y, blockMaterials)
      piece     <- pieceList.get(index)
    } yield piece

  def move(piece: Piece, position: Position): Piece = piece match {
    case k: IKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: JKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: LKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: OKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: SKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: TKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
    case k: ZKind =>
      k.copy(position = position, direction = PieceDirection.Neutral)
  }

  private def fromBlockMaterial(
      x: Int,
      y: Int,
      blockMaterials: Seq[BlockMaterial],
  ): Option[Seq[Piece]] =
    blockMaterials.traverse(m => materialToPiece(x, y, m))

  private def materialToPiece(
      x: Int,
      y: Int,
      material: BlockMaterial,
  ): Option[Piece] =
    val initPosition  = Position(x, y)
    val initDirection = PieceDirection.Neutral
    material match {
      case material @ Blue(size)    =>
        JKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ Green(size)   =>
        SKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ Red(size)     =>
        ZKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ Orange(size)  =>
        LKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ Purple(size)  =>
        TKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ SkyBlue(size) =>
        IKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case material @ Yellow(size)  =>
        OKind(
          position = initPosition,
          direction = initDirection,
          blockSize = size,
          material,
        ).some
      case _                        => none
    }

enum PieceState:
  case INITIALIZE, FALLING, LANDED

enum PieceDirection:
  case Neutral, Left, Right, Down

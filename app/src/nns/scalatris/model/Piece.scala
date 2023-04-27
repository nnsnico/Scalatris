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
    val blockSize: GridSquareSize,
    val material: BlockMaterial,
):
  val localPos: Seq[Position]

  def update(gridSquareSize: GridSquareSize, direction: PieceDirection): Piece =
    direction match
      case PieceDirection.Neutral => this
      // TODO: down
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

final case class IKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(2, 0),
    Position(3, 0),
  )

final case class JKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class LKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
    Position(2, 1),
  )

final case class OKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 0),
    Position(1, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class SKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(1, 0),
    Position(2, 0),
    Position(0, 1),
    Position(1, 1),
  )

final case class TKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

  override val localPos: Seq[Position] = Seq(
    Position(0, 1),
    Position(1, 0),
    Position(1, 1),
    Position(2, 1),
  )

final case class ZKind(
    override val position: Position,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
) extends Piece(position, blockSize, material):

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
      k.copy(position = position)
    case k: JKind =>
      k.copy(position = position)
    case k: LKind =>
      k.copy(position = position)
    case k: OKind =>
      k.copy(position = position)
    case k: SKind =>
      k.copy(position = position)
    case k: TKind =>
      k.copy(position = position)
    case k: ZKind =>
      k.copy(position = position)
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
          blockSize = size,
          material,
        ).some
      case material @ Green(size)   =>
        SKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case material @ Red(size)     =>
        ZKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case material @ Orange(size)  =>
        LKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case material @ Purple(size)  =>
        TKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case material @ SkyBlue(size) =>
        IKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case material @ Yellow(size)  =>
        OKind(
          position = initPosition,
          blockSize = size,
          material,
        ).some
      case _                        => none
    }

enum PieceState:
  case Initialize, Falling, Landed

enum PieceDirection:
  case Neutral
  case Left
  case Right
  case Down

object PieceDirection:

  enum ControlScheme:
    case Neutral
    case Turning(left: Key, right: Key)
    case Falling(down: Key)

  val turningKeys: ControlScheme.Turning =
    ControlScheme.Turning(Key.KEY_H, Key.KEY_L)

  val fallingKeys: ControlScheme.Falling = ControlScheme.Falling(Key.KEY_J)

  extension (cs: ControlScheme)

    def toPieceDirection(keyboardEvent: KeyboardEvent): PieceDirection =
      (cs, keyboardEvent) match
        case (ControlScheme.Turning(key, _), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Left
        case (ControlScheme.Turning(_, key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Right
        case (ControlScheme.Falling(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Down
        case _ =>
          PieceDirection.Neutral

package nns.scalatris.model

import cats._
import cats.data._
import cats.syntax.all._
import indigo.*
import indigo.shared.*
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.assets._
import nns.scalatris.extensions._
import nns.scalatris.{GridSquareSize, ViewConfig}

import scala.util.Random

sealed abstract class Piece(
    val position: Vertex,
    val blockSize: GridSquareSize,
    val material: BlockMaterial,
    val localPos: Seq[Vertex],
):

  def update(stageSize: BoundingBox, direction: PieceDirection): Piece =
    direction match
      case PieceDirection.Neutral => this
      // TODO: down
      case PieceDirection.Down    => this
      case PieceDirection.Left    =>
        val currentPosition = this.position
        val nextPos         = currentPosition.x - blockSize.toInt
        val leftBounds      = stageSize.position.x
        Piece.move(
          piece = this,
          position = currentPosition.copy(
            x = (nextPos >= leftBounds).fold(
              nextPos,
              leftBounds,
            ),
          ),
        )
      case PieceDirection.Right   =>
        val currentPosition = this.position
        val nextPos         = currentPosition.x + blockSize.toInt
        val rightBounds     =
          ((blockSize * stageSize.width) +
            stageSize.position.x).toInt -               // position from origin to stage
            localPos.maxBy(_.x).x * (blockSize.toInt) - // max block width
            blockSize.toInt
        Piece.move(
          piece = this,
          position = currentPosition.copy(
            x = (nextPos > rightBounds).fold(
              rightBounds,
              nextPos,
            ),
          ),
        )

final case class IKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(2, 0),
      Vertex(3, 0),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class JKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(0, 1),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class LKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(2, 0),
      Vertex(0, 1),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class OKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(0, 1),
      Vertex(1, 1),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class SKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(1, 0),
      Vertex(2, 0),
      Vertex(0, 1),
      Vertex(1, 1),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class TKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 1),
      Vertex(1, 0),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(position, blockSize, material, localPos)

final case class ZKind(
    override val position: Vertex,
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(1, 1),
      Vertex(1, 2),
    ),
) extends Piece(position, blockSize, material, localPos)

object Piece:

  def init(
      position: Vertex,
      blockMaterials: Seq[BlockMaterial],
  ): Option[Piece] =
    for {
      index     <- Some(Random.nextInt(blockMaterials.length))
      pieceList <- fromBlockMaterial(position.x, position.y, blockMaterials)
      piece     <- pieceList.get(index)
    } yield piece

  def move(piece: Piece, position: Vertex): Piece = piece match
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

  private def fromBlockMaterial(
      x: Double,
      y: Double,
      blockMaterials: Seq[BlockMaterial],
  ): Option[Seq[Piece]] =
    blockMaterials.traverse(m => materialToPiece(x, y, m))

  private def materialToPiece(
      x: Double,
      y: Double,
      material: BlockMaterial,
  ): Option[Piece] =
    val initPosition  = Vertex(x, y)
    val initDirection = PieceDirection.Neutral
    material match
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

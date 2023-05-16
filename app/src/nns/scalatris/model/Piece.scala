package nns.scalatris.model

import cats._
import cats.data._
import cats.syntax.all._
import indigo.*
import indigo.shared.*
import indigo.shared.events.KeyboardEvent
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.assets._
import nns.scalatris.extensions._
import nns.scalatris.{GridSquareSize, ViewConfig}

import scala.util.Random

sealed abstract class Piece(
    val blockSize: GridSquareSize,
    val material: BlockMaterial,
    val localPos: Seq[Vertex],
):

  def update(stageSize: BoundingBox, direction: PieceDirection): Piece =
    direction match
      case PieceDirection.Neutral =>
        this
      case PieceDirection.Down    =>
        val nextPos = localPos.map(pos => pos + Vertex(0, 1))
        Piece.move(
          piece = this,
          localPos = (nextPos.map(_.y).max < stageSize.height).fold(
            nextPos,
            localPos,
          ),
        )
      case PieceDirection.Left    =>
        val nextPos = localPos.map(pos => pos - Vertex(1, 0))
        Piece.move(
          piece = this,
          localPos = (nextPos.map(_.x).min >= 0).fold(
            nextPos,
            localPos,
          ),
        )
      case PieceDirection.Right   =>
        val nextPos = localPos.map(pos => pos + Vertex(1, 0))
        Piece.move(
          this,
          localPos = (nextPos.map(_.x).max < stageSize.width).fold(
            nextPos,
            localPos,
          ),
        )
      case PieceDirection.Up      => Piece.rotate(this)

final case class IKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(2, 0),
      Vertex(3, 0),
    ),
) extends Piece(blockSize, material, localPos)

final case class JKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(0, 1),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(blockSize, material, localPos)

final case class LKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(2, 0),
      Vertex(0, 1),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(blockSize, material, localPos)

final case class OKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(0, 1),
      Vertex(1, 1),
    ),
) extends Piece(blockSize, material, localPos)

final case class SKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(1, 0),
      Vertex(2, 0),
      Vertex(0, 1),
      Vertex(1, 1),
    ),
) extends Piece(blockSize, material, localPos)

final case class TKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 1),
      Vertex(1, 0),
      Vertex(1, 1),
      Vertex(2, 1),
    ),
) extends Piece(blockSize, material, localPos)

final case class ZKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0, 0),
      Vertex(1, 0),
      Vertex(1, 1),
      Vertex(1, 2),
    ),
) extends Piece(blockSize, material, localPos)

object Piece:

  def init(blockMaterials: Seq[BlockMaterial]): Option[Piece] =
    for {
      index     <- Some(Random.nextInt(blockMaterials.length))
      pieceList <- fromBlockMaterial(blockMaterials)
      piece     <- pieceList.get(index)
    } yield piece

  def move(piece: Piece, localPos: Seq[Vertex]): Piece = piece match
    case k: IKind =>
      k.copy(localPos = localPos)
    case k: JKind =>
      k.copy(localPos = localPos)
    case k: LKind =>
      k.copy(localPos = localPos)
    case k: OKind =>
      k.copy(localPos = localPos)
    case k: SKind =>
      k.copy(localPos = localPos)
    case k: TKind =>
      k.copy(localPos = localPos)
    case k: ZKind =>
      k.copy(localPos = localPos)

  def rotate(piece: Piece): Piece = piece match
    case k: IKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: JKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: LKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: OKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: SKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: TKind =>
      k.copy(localPos = rotateRight(k.localPos))
    case k: ZKind =>
      k.copy(localPos = rotateRight(k.localPos))

  private def rotateRight(localPos: Seq[Vertex]): Seq[Vertex] =
    val c = math.cos(-math.Pi / 2.0)
    val s = math.sin(-math.Pi / 2.0)

    def roundToHalf(v: (Double, Double)): (Double, Double) = v match
      case (x, y) =>
        (math.round(x * 2.0) * 0.5, math.round(y * 2.0) * 0.5)

    localPos.map { v =>
      roundToHalf((v.x * c - v.y * s, v.x * s + v.y * c))
    }.map { case (x, y) => Vertex(x, y) }

  private def fromBlockMaterial(
      blockMaterials: Seq[BlockMaterial],
  ): Option[Seq[Piece]] =
    blockMaterials.traverse(m => materialToPiece(m))

  private def materialToPiece(material: BlockMaterial): Option[Piece] =
    val initDirection = PieceDirection.Neutral
    material match
      case material @ Blue(size)    =>
        JKind(
          blockSize = size,
          material,
        ).some
      case material @ Green(size)   =>
        SKind(
          blockSize = size,
          material,
        ).some
      case material @ Red(size)     =>
        ZKind(
          blockSize = size,
          material,
        ).some
      case material @ Orange(size)  =>
        LKind(
          blockSize = size,
          material,
        ).some
      case material @ Purple(size)  =>
        TKind(
          blockSize = size,
          material,
        ).some
      case material @ SkyBlue(size) =>
        IKind(
          blockSize = size,
          material,
        ).some
      case material @ Yellow(size)  =>
        OKind(
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
  case Up
  case Down

object PieceDirection:

  enum ControlScheme:
    case Neutral
    case Turning(left: Key, right: Key)
    case Rotating(up: Key)
    case Falling(down: Key)

  val turningKeys: ControlScheme.Turning =
    ControlScheme.Turning(Key.KEY_H, Key.KEY_L)

  val rotatingKeys: ControlScheme.Rotating = ControlScheme.Rotating(Key.KEY_K)

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
        case (ControlScheme.Rotating(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Up
        case (ControlScheme.Falling(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Down
        case _ =>
          PieceDirection.Neutral

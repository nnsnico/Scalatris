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
    protected val position: Vertex,
    protected val localPos: Seq[Vertex],
):

  def current: Seq[Vertex] = convertToStagePosition(localPos, position)

  def update(stageSize: BoundingBox, direction: PieceDirection): Piece =
    direction match
      case PieceDirection.Neutral =>
        this
      case PieceDirection.Left    =>
        val nextPos = current.map(pos => pos - Vertex(1, 0))
        Piece.move(
          piece = this,
          position = (nextPos.map(_.x).min >= 0).fold(
            position - Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Right   =>
        val nextPos = current.map(pos => pos + Vertex(1, 0))
        Piece.move(
          this,
          position = (nextPos.map(_.x).max < stageSize.width).fold(
            position + Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Up      =>
        val nextPos = convertToStagePosition(rotateLeft, position)
        Piece.moveByLocalPos(
          this,
          localPos = (
            nextPos.map(_.x).min >= 0 &&
              nextPos.map(_.x).max < stageSize.width &&
              nextPos.map(_.y).max < stageSize.height
          ).fold(
            rotateLeft,
            localPos,
          ),
        )
      case PieceDirection.Down    =>
        val nextPos = current.map(pos => pos + Vertex(0, 1))
        (nextPos.map(_.y).max < stageSize.height).fold(
          Piece.move(this, position + Vertex(0, 1)),
          Piece.updateState(this, PieceState.Landed),
        )

  private def convertToStagePosition(
      localPos: Seq[Vertex],
      position: Vertex,
  ): Seq[Vertex] =
    localPos.map(pos =>
      Vertex(
        math.ceil(pos.x + position.x + 0.5),
        math.floor(pos.y + position.y + 0.5),
      ),
    )

  private def rotateLeft: Seq[Vertex] =
    val c = math.cos(-math.Pi / 2.0)
    val s = math.sin(-math.Pi / 2.0)

    def roundToHalf(v: (Double, Double)): (Double, Double) = v match
      case (x, y) =>
        (math.round(x * 2.0) * 0.5, math.round(y * 2.0) * 0.5)

    localPos.map { v =>
      roundToHalf((v.x * c - v.y * s, v.x * s + v.y * c))
    }.map { case (x, y) => Vertex(x, y) }

final case class IKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(1, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-1.5, 0),
      Vertex(-0.5, 0),
      Vertex(0.5, 0),
      Vertex(1.5, 0),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class JKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-1.0, 0.5),
      Vertex(0.0, 0.5),
      Vertex(1.0, 0.5),
      Vertex(1.0, -0.5),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class LKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-1.0, 0.5),
      Vertex(0.0, 0.5),
      Vertex(1.0, 0.5),
      Vertex(-1.0, -0.5),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class OKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-0.5, 0.5),
      Vertex(0.5, 0.5),
      Vertex(-0.5, -0.5),
      Vertex(0.5, -0.5),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class SKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(0.0, 0.5),
      Vertex(1.0, 0.5),
      Vertex(-1.0, -0.5),
      Vertex(0.0, -0.5),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class TKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-1.0, 0.0),
      Vertex(0.0, 0.0),
      Vertex(1.0, 0.0),
      Vertex(0.0, 1.0),
    ),
) extends Piece(blockSize, material, position, localPos)

final case class ZKind(
    override val blockSize: GridSquareSize,
    override val material: BlockMaterial,
    override val position: Vertex = Vertex(0, 0),
    override val localPos: Seq[Vertex] = Seq(
      Vertex(-1.0, 0.5),
      Vertex(0.0, 0.5),
      Vertex(0.0, -0.5),
      Vertex(1.0, -0.5),
    ),
) extends Piece(blockSize, material, position, localPos)

object Piece:

  def init(blockMaterials: Seq[BlockMaterial]): Option[Piece] =
    for {
      index     <- Some(Random.nextInt(blockMaterials.length))
      // index     <- Some(5)
      pieceList <- fromBlockMaterial(blockMaterials)
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

  def moveByLocalPos(piece: Piece, localPos: Seq[Vertex]): Piece = piece match
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

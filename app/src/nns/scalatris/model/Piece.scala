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
    val material: BlockMaterial,
    val state: PieceState,
    protected val position: Vertex,
    protected val localPos: Seq[Vertex],
):

  def current: Seq[Vertex] = localPos.map(convertToStagePosition(_, position))

  def downPosition(
    stageSize: BoundingBox,
    placedPieces: Set[Vertex],
  ): Piece =
    val nextPos = current.map(pos => pos + Vertex(0, 1))
    (
      nextPos.map(_.y).max < stageSize.height &&
        validateBlocks(placedPieces, nextPos)
    ).fold(
      Piece.move(this, position + Vertex(0, 1)),
      Piece.updateState(this, PieceState.Landed),
    )

  def updatePositionByDirection(
      stageSize: BoundingBox,
      placedPieces: Set[Vertex],
      direction: PieceDirection,
  ): Piece =
    direction match
      case PieceDirection.Neutral =>
        this
      case PieceDirection.Left    =>
        val nextPos = current.map(pos => pos - Vertex(1, 0))
        Piece.move(
          piece = this,
          position = (
            nextPos.map(_.x).min >= 0 &&
              validateBlocks(placedPieces, nextPos)
          ).fold(
            position - Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Right   =>
        val nextPos = current.map(pos => pos + Vertex(1, 0))
        Piece.move(
          this,
          position = (
            nextPos.map(_.x).max < stageSize.width &&
              validateBlocks(placedPieces, nextPos)
          ).fold(
            position + Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Up      =>
        val nextPos = rotateLeft.map(convertToStagePosition(_, position))
        Piece.moveByLocalPos(
          this,
          localPos = (
            nextPos.map(_.x).min >= 0 &&
              nextPos.map(_.x).max < stageSize.width &&
              nextPos.map(_.y).max < stageSize.height &&
              validateBlocks(placedPieces, nextPos)
          ).fold(
            rotateLeft,
            localPos,
          ),
        )
      case PieceDirection.Down    =>
        downPosition(stageSize, placedPieces)

  def removeBlock(position: Seq[Vertex]): Piece =
    Piece.moveByLocalPos(
      this,
      localPos = localPos.filter(v =>
        !position.contains(convertToStagePosition(v, this.position)),
      ),
    )

  def shiftBlocksToDown(filledPositionY: Set[Double]): Piece =
    Piece.moveByLocalPos(
      this,
      localPos = localPos.map(v =>
        filledPositionY
          .toSeq
          .sorted
          .foldLeft(v)((z, posY) =>
            (convertToStagePosition(v, position).y < posY).fold(
              z + Vertex(0, 1),
              z,
            ),
          ),
      ),
    )

  private def validateBlocks(placedPieces: Set[Vertex], nextPos: Seq[Vertex]) =
    (placedPieces & nextPos.toSet).isEmpty

  private def convertToStagePosition(
      localPos: Vertex,
      position: Vertex,
  ): Vertex = Vertex(
    math.ceil(localPos.x + position.x + 0.5),
    math.floor(localPos.y + position.y + 0.5),
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

object Piece:

  final case class IKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(1, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-1.5, 0),
        Vertex(-0.5, 0),
        Vertex(0.5, 0),
        Vertex(1.5, 0),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class JKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(1.0, -0.5),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class LKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(-1.0, -0.5),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class OKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-0.5, 0.5),
        Vertex(0.5, 0.5),
        Vertex(-0.5, -0.5),
        Vertex(0.5, -0.5),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class SKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(-1.0, -0.5),
        Vertex(0.0, -0.5),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class TKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.0),
        Vertex(0.0, 0.0),
        Vertex(1.0, 0.0),
        Vertex(0.0, 1.0),
      ),
  ) extends Piece(material, state, position, localPos)

  final case class ZKind(
      override val material: BlockMaterial,
      override val state: PieceState = PieceState.Falling,
      override val position: Vertex = Vertex(0, 0),
      override val localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(0.0, -0.5),
        Vertex(1.0, -0.5),
      ),
  ) extends Piece(material, state, position, localPos)

  def init(blockMaterials: Seq[BlockMaterial]): Option[Piece] =
    fromBlockMaterial(blockMaterials).get(Random.nextInt(blockMaterials.length))

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

  def updateState(piece: Piece, state: PieceState): Piece = piece match
    case k: IKind =>
      k.copy(state = state)
    case k: JKind =>
      k.copy(state = state)
    case k: LKind =>
      k.copy(state = state)
    case k: OKind =>
      k.copy(state = state)
    case k: SKind =>
      k.copy(state = state)
    case k: TKind =>
      k.copy(state = state)
    case k: ZKind =>
      k.copy(state = state)

  private def fromBlockMaterial(materials: Seq[BlockMaterial]): Seq[Piece] =
    materials.map(m => materialToPiece(m))

  private def materialToPiece(material: BlockMaterial): Piece =
    material match
      case m: Blue    => JKind(m)
      case m: Green   => SKind(m)
      case m: Red     => ZKind(m)
      case m: Orange  => LKind(m)
      case m: Purple  => TKind(m)
      case m: SkyBlue => IKind(m)
      case m: Yellow  => OKind(m)

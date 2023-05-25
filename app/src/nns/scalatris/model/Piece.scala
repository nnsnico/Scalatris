package nns.scalatris.model

import cats._
import cats.data._
import cats.syntax.all._
import indigo.*
import indigo.shared.*
import indigo.shared.events.KeyboardEvent
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.assets.{Block, BlockMaterial}
import nns.scalatris.extensions._
import nns.scalatris.{GridSquareSize, ViewConfig}

import scala.util.Random

final case class Piece(
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
      copy(position = position + Vertex(0, 1)),
      copy(state = PieceState.Landed),
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
        copy(
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
        copy(
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
        this.copy(localPos =
          (
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
    copy(
      localPos = localPos.filter(v =>
        !position.contains(convertToStagePosition(v, this.position)),
      ),
    )

  def shiftBlocksToDown(filledPositionY: Set[Double]): Piece =
    copy(
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

  def init(blockMaterials: Seq[BlockMaterial]): Option[Piece] =
    createFromMaterials(blockMaterials).get(
      Random.nextInt(blockMaterials.length),
    )

  def createFromMaterials(
      materials: Seq[BlockMaterial],
  ): Seq[Piece] =
    for {
      kind       <- PieceKind.values
      material   <- materials
      maybePiece <- create(kind, material).toSeq
    } yield maybePiece

  def create(kind: PieceKind, material: BlockMaterial): Option[Piece] =
    (kind, material) match
      case (PieceKind.JKind(initPos, localPos), m: BlockMaterial.Blue)    =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.SKind(initPos, localPos), m: BlockMaterial.Green)   =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.ZKind(initPos, localPos), m: BlockMaterial.Red)     =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.LKind(initPos, localPos), m: BlockMaterial.Orange)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.TKind(initPos, localPos), m: BlockMaterial.Purple)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.IKind(initPos, localPos), m: BlockMaterial.SkyBlue) =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case (PieceKind.OKind(initPos, localPos), m: BlockMaterial.Yellow)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPos = localPos,
        ).some
      case _                                                              => none

  // private def materialToPiece(material: BlockMaterial): Piece =
  //   material match
  //     case m: JKind =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.JKind,
  //         position = Vertex(0, 0),
  //         localPos = Seq(
  //           Vertex(-1.0, 0.5),
  //           Vertex(0.0, 0.5),
  //           Vertex(1.0, 0.5),
  //           Vertex(1.0, -0.5),
  //         ),
  //       )
  //     case m: Green =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.SKind,
  //         position = Vertex(0, 0),
  //         localPos = Seq(
  //           Vertex(0.0, 0.5),
  //           Vertex(1.0, 0.5),
  //           Vertex(-1.0, -0.5),
  //           Vertex(0.0, -0.5),
  //         ),
  //       )

  //     case m: Red     =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.ZKind,
  //         position = Vertex(0, 0),
  //         localPos = Seq(
  //           Vertex(-1.0, 0.5),
  //           Vertex(0.0, 0.5),
  //           Vertex(0.0, -0.5),
  //           Vertex(1.0, -0.5),
  //         ),
  //       )
  //     case m: Orange  =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.LKind,
  //         position = Vertex(0, 0),
  //         localPos = Seq(
  //           Vertex(-1.0, 0.5),
  //           Vertex(0.0, 0.5),
  //           Vertex(1.0, 0.5),
  //           Vertex(-1.0, -0.5),
  //         ),
  //       )
  //     case m: Purple  =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.TKind,
  //         position = Vertex(0, 0),
  //         localPos = Seq(
  //           Vertex(-1.0, 0.0),
  //           Vertex(0.0, 0.0),
  //           Vertex(1.0, 0.0),
  //           Vertex(0.0, 1.0),
  //         ),
  //       )
  //     case m: SkyBlue =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.IKind,
  //         position = Vertex(1, 0),
  //         localPos = Seq(
  //           Vertex(-1.5, 0),
  //           Vertex(-0.5, 0),
  //           Vertex(0.5, 0),
  //           Vertex(1.5, 0),
  //         ),
  //       )
  //     case m: Yellow  =>
  //       Piece(
  //         material = m,
  //         state = PieceState.Falling,
  //         kind = PieceKind.OKind,
  //         position = Vertex(1, 0),
  //         localPos = Seq(
  //           Vertex(-0.5, 0.5),
  //           Vertex(0.5, 0.5),
  //           Vertex(-0.5, -0.5),
  //           Vertex(0.5, -0.5),
  //         ),
  //       )

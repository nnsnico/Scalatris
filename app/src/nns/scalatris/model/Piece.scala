package nns.scalatris.model

import cats._
import cats.data._
import cats.syntax.all._
import indigo.*
import indigo.shared.*
import indigo.shared.events.KeyboardEvent
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.ViewConfig
import nns.scalatris.assets.{Block, BlockMaterial}
import nns.scalatris.extensions.Boolean.*
import nns.scalatris.types.StageSize

final case class Piece(
    val material: BlockMaterial,
    val state: PieceState,
    protected val position: Vertex,
    protected val localPosition: Seq[Vertex],
):

  def currentPosition: Seq[Vertex] =
    localPosition.map(convertToStagePosition(_, position))

  def updatePositionByDirection(
      stageSize: StageSize,
      placedPieces: Set[Vertex],
      direction: PieceDirection,
  ): Piece =
    direction match
      case PieceDirection.Neutral =>
        this
      case PieceDirection.Left    =>
        val nextPosition = currentPosition.map(_ - Vertex(1, 0))
        copy(
          position = (
            nextPosition.minBy(_.x).x >= 0 && checkForPiecePlacement(
              placedPieces,
              nextPosition,
            )
          ).fold(
            position - Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Right   =>
        val nextPosition = currentPosition.map(pos => pos + Vertex(1, 0))
        copy(
          position = (
            nextPosition.map(_.x).max < stageSize.width &&
              checkForPiecePlacement(placedPieces, nextPosition)
          ).fold(
            position + Vertex(1, 0),
            position,
          ),
        )
      case PieceDirection.Up      =>
        val nextPosition = rotateLeft.map(convertToStagePosition(_, position))
        this.copy(
          localPosition = (
            nextPosition.minBy(_.x).x >= 0 &&
              nextPosition.maxBy(_.x).x < stageSize.width &&
              nextPosition.maxBy(_.y).y < stageSize.height &&
              checkForPiecePlacement(placedPieces, nextPosition)
          ).fold(
            rotateLeft,
            localPosition,
          ),
        )
      case PieceDirection.Down    =>
        downPosition(stageSize, placedPieces)

  def removeBlock(position: Seq[Vertex]): Piece =
    copy(
      localPosition = localPosition.filter(v =>
        !position.contains(convertToStagePosition(v, this.position)),
      ),
    )

  def shiftBlocksToDown(filledPositionY: Set[Double]): Piece =
    copy(
      localPosition = localPosition.map(v =>
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

  /*
   * check if piece can be placed on stage
   */
  private def checkForPiecePlacement(
      placedPieces: Set[Vertex],
      nextPos: Seq[Vertex],
  ): Boolean = (placedPieces & nextPos.toSet).isEmpty

  private def convertToStagePosition(
      localPos: Vertex,
      position: Vertex,
  ): Vertex = Vertex(
    math.ceil(localPos.x + position.x + 0.5),
    math.floor(localPos.y + position.y + 0.5),
  )

  private def downPosition(
      stageSize: StageSize,
      placedPieces: Set[Vertex],
  ): Piece =
    val nextPosition = currentPosition.map(_ + Vertex(0, 1))
    (
      nextPosition.maxBy(_.y).y < stageSize.height &&
        checkForPiecePlacement(placedPieces, nextPosition)
    ).fold(
      copy(position = position + Vertex(0, 1)),
      copy(state = PieceState.Landed),
    )

  private def rotateLeft: Seq[Vertex] =
    val c = math.cos(-math.Pi / 2.0)
    val s = math.sin(-math.Pi / 2.0)

    def roundToHalf(v: (Double, Double)): (Double, Double) = v match
      case (x, y) =>
        (math.round(x * 2.0) * 0.5, math.round(y * 2.0) * 0.5)

    localPosition.map { v =>
      roundToHalf((v.x * c - v.y * s, v.x * s + v.y * c))
    }.map { case (x, y) => Vertex(x, y) }

object Piece:

  def init(
      blockMaterials: Seq[BlockMaterial],
      index: Int,
  ): Either[Throwable, Piece] =
    createFromMaterials(blockMaterials)
      .get(index)
      .toRight(Exception("Piece#init: Failed to initial Piece"))

  def createFromMaterials(
      materials: Seq[BlockMaterial],
  ): EitherT[Seq, Throwable, Piece] = for {
    kind       <- EitherT.right(PieceKind.values)
    material   <- EitherT.right(materials)
    maybePiece <- EitherT.fromEither(create(kind, material))
  } yield maybePiece

  def create(
      kind: PieceKind,
      material: BlockMaterial,
  ): Either[Throwable, Piece] =
    (kind, material) match
      case (PieceKind.JKind(initPos, localPos), m: BlockMaterial.Blue)    =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.SKind(initPos, localPos), m: BlockMaterial.Green)   =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.ZKind(initPos, localPos), m: BlockMaterial.Red)     =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.LKind(initPos, localPos), m: BlockMaterial.Orange)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.TKind(initPos, localPos), m: BlockMaterial.Purple)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.IKind(initPos, localPos), m: BlockMaterial.SkyBlue) =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case (PieceKind.OKind(initPos, localPos), m: BlockMaterial.Yellow)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = initPos,
          localPosition = localPos,
        ).asRight
      case _                                                              => Left(Exception("Piece#create: Failed to create piece"))

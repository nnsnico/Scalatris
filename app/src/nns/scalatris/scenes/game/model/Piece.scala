package nns.scalatris.scenes.game.model

import cats.*
import cats.data.*
import cats.syntax.all.*
import indigo.*
import indigo.shared.*
import indigo.shared.geometry.Vertex
import mouse.all.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.{Block, BlockMaterial}
import nns.scalatris.extensions.CollectionExt.{maxOr, minOr}
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
            nextPosition.minOr(_.x, Vertex(-1)).x >= 0 &&
              checkForPiecePlacement(
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
            nextPosition.maxOr(_.x, Vertex(-1)).x < stageSize.width &&
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
            nextPosition.minOr(_.x, Vertex(-1)).x >= 0 &&
              nextPosition.maxOr(_.x, Vertex(-1)).x < stageSize.width &&
              nextPosition.maxOr(_.y, Vertex(-1)).y < stageSize.height &&
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
      nextPosition.maxOr(_.y, Vertex(-1)).y < stageSize.height &&
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

  def createPieceFlow(
      blockMaterials: Seq[BlockMaterial],
  ): Either[Throwable, Seq[Piece]] = for {
    piece <- Either.right(createFromMaterials(blockMaterials).collectRight)
    cond  <- Either.cond(
               piece.nonEmpty && piece.length == blockMaterials.length,
               right = piece,
               left = Exception("Pieces are empty or missing"),
             )
  } yield piece

  private[model] def createFromMaterials(
      materials: Seq[BlockMaterial],
  ): EitherT[Seq, Throwable, Piece] = for {
    kind       <- EitherT.right(PieceKind.values.toSeq)
    material   <- EitherT.right(materials)
    maybePiece <- EitherT.fromEither(createPiece(kind, material))
  } yield maybePiece

  private[game] def createPiece(
      kind: PieceKind,
      material: BlockMaterial,
  ): Either[Throwable, Piece] =
    (kind, material) match
      case (kind @ PieceKind.J, m: BlockMaterial.Blue)    =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.S, m: BlockMaterial.Green)   =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.Z, m: BlockMaterial.Red)     =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.L, m: BlockMaterial.Orange)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.T, m: BlockMaterial.Purple)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.I, m: BlockMaterial.SkyBlue) =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case (kind @ PieceKind.O, m: BlockMaterial.Yellow)  =>
        Piece(
          material = m,
          state = PieceState.Falling,
          position = kind.initPosition,
          localPosition = kind.localPos,
        ).asRight
      case _                                              =>
        Exception("Piece#create: Failed to create piece").asLeft

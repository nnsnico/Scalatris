package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import indigo.logger.*
import mouse.all.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Either.toOutcome
import nns.scalatris.scenes.*
import nns.scalatris.scenes.game.model.PieceDirection.ControlScheme
import nns.scalatris.scenes.game.model.{Piece, PieceDirection, PieceKind}
import nns.scalatris.types.StageSize

import scala.util.Random

final case class GameModel private (
    override val tickDelay: Seconds,
    override val lastUpdated: Seconds,
    currentPiece: Piece,
    pieceFlow: Seq[Piece],
    stageMap: Set[Piece],
    currentDirection: PieceDirection,
    controlScheme: Seq[ControlScheme],
    tickPieceDown: Seconds,
    lastUpdatedPieceDown: Seconds,
) extends BaseSceneModel(tickDelay, lastUpdated):

  def dropOnePiece(
      currentTime: Seconds,
      stageSize: StageSize,
  ): GameModel = copy(
    currentPiece = currentPiece.updatePositionByDirection(
      stageSize = stageSize,
      placedPieces = stageMap.flatMap(_.currentPosition.toSet),
      direction = PieceDirection.Down,
    ),
    lastUpdatedPieceDown = currentTime,
  )

  def updateDirection(
      direction: PieceDirection,
      lastUpdated: Seconds = Seconds.zero,
  ): GameModel = copy(
    currentDirection = direction,
    lastUpdated = lastUpdated,
  )

  def updatePiece(
      currentTime: Option[Seconds],
      stageSize: StageSize,
  ): GameModel = copy(
    currentPiece = currentPiece.updatePositionByDirection(
      stageSize,
      stageMap.flatMap(_.currentPosition.toSet),
      currentDirection,
    ),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

  def putPieceOnStage(
      stageSize: StageSize,
      blockMaterial: Seq[BlockMaterial],
      putPiece: Piece,
  ): Outcome[GameModel] = (for {
    maybePiece    <-
      pieceFlow
        .headOption
        .toRight(Exception("Unexpected Error: Pieces are empty in flow"))
    nextFlow      <- pieceFlow.tail.asRight
    candidateFlow <- (!nextFlow.isEmpty).fold(
                       t = nextFlow.asRight,
                       f = Piece
                         .createPieceFlow(blockMaterial)
                         .map(Random.shuffle(_)),
                     )
  } yield copy(
    currentPiece = maybePiece,
    pieceFlow = candidateFlow,
    currentDirection = PieceDirection.Neutral,
    stageMap = {
      val nextMap                = stageMap + putPiece
      val filteredFillPositionY  =
        GameModel.filterFilledPositionY(nextMap, stageSize.width)
      val removableBlockPosition = (p: Piece) =>
        p.currentPosition.filter(v => filteredFillPositionY.contains(v.y))

      nextMap
        .map(p =>
          removableBlockPosition(p)
            .isEmpty
            .fold(p, p.removeBlock(removableBlockPosition(p))),
        )
        .map(p => p.shiftBlocksToDown(filteredFillPositionY))
    },
  )).toOutcome

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): Either[Throwable, GameModel] = for {
    flow      <- Piece
                   .createPieceFlow(blockMaterial)
                   .map(Random.shuffle(_))
    initFlow   = flow.tail
    initPiece <-
      flow
        .headOption
        .toRight(Exception("Unexpected Error: Pieces are empty in flow"))
  } yield GameModel(
    tickDelay = Seconds(TICK_FRAME_SECONDS),
    lastUpdated = Seconds.zero,
    currentPiece = initPiece,
    pieceFlow = initFlow,
    stageMap = Set.empty,
    currentDirection = PieceDirection.Neutral,
    controlScheme = Seq(
      PieceDirection.turningKeys,
      PieceDirection.rotatingKeys,
      PieceDirection.fallingKeys,
    ),
    tickPieceDown = Seconds(FALL_DOWN_SECONDS),
    lastUpdatedPieceDown = Seconds.zero,
  )

  private[game] def filterFilledPositionY(
      map: Set[Piece],
      stageWidth: Double,
  ): Set[Double] = map
    .flatMap(_.currentPosition)
    .groupMapReduce(_.y)(_.x + 1)(_ + _)
    .filter((_, y) => y == factorialWidth(stageWidth))
    .keySet

  private def factorialWidth(stageWidth: Double): Int =
    (1 to stageWidth.toInt).reduce(_ + _)

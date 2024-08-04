package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import indigo.logger.*
import mouse.all.*
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Either.toOutcome
import nns.scalatris.scenes.*
import nns.scalatris.scenes.game.model.PieceDirection.ControlScheme
import nns.scalatris.scenes.game.model.PieceFlow.next
import nns.scalatris.scenes.game.model.StageMap.putOrStay
import nns.scalatris.scenes.game.model.{Piece, PieceDirection, PieceKind}
import nns.scalatris.types.StageSize
import nns.scalatris.ViewConfig

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
    gameState: GameState,
) extends BaseSceneModel:

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
  ): GameModel = copy(
    currentDirection = direction,
    lastUpdated = Seconds.zero,
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
    nextFlow     <- pieceFlow.next(blockMaterial)
    (piece, flow) = nextFlow
    nextMap <- stageMap
                 .putOrStay(putPiece)
                 .toRight(IllegalStateException("No space for block"))
  } yield copy(
    currentPiece = piece,
    pieceFlow = flow,
    currentDirection = PieceDirection.Neutral,
    stageMap = removeLines(nextMap, stageSize),
  ))
    .toOutcome
    .handleError { case e: IllegalStateException =>
      Outcome(copy(gameState = GameState.Crashed))
    }

private def removeLines(
    nextMap: Set[Piece],
    stageSize: StageSize,
): Set[Piece] = {
  val filteredFillPositionY = GameModel.filterFilledPositionY(
    nextMap,
    stageSize.width,
  )
  val removableBlockPosition = (p: Piece) =>
    p.currentPosition.filter(v => filteredFillPositionY.contains(v.y))

  nextMap
    .map(p =>
      removableBlockPosition(p)
        .isEmpty
        .fold(p, p.removeBlock(removableBlockPosition(p)))
        .shiftBlocksToDown(filteredFillPositionY),
    )
}

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): Either[Throwable, GameModel] = for {
    flow                 <- Piece.createPieceFlow(blockMaterial)
    initPieceFlow        <- flow.next(blockMaterial)
    (initPiece, initFlow) = initPieceFlow
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
    gameState = GameState.Running,
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
    (1 to stageWidth.toInt).fold(0)(_ + _)

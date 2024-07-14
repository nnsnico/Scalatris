package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import indigo.logger.*
import mouse.all.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.scenes.*
import nns.scalatris.scenes.game.model.PieceDirection.ControlScheme
import nns.scalatris.scenes.game.model.{Piece, PieceDirection, PieceKind}
import nns.scalatris.types.StageSize

import scala.util.Random

final case class GameModel private (
    override val tickDelay: Seconds,
    override val lastUpdated: Seconds,
    currentPiece: Either[Throwable, Piece],
    pieceFlow: Either[Throwable, Seq[Piece]],
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
    currentPiece = currentPiece.map(p =>
      p.updatePositionByDirection(
        stageSize = stageSize,
        placedPieces = stageMap.flatMap(_.currentPosition.toSet),
        direction = PieceDirection.Down,
      ),
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
    currentPiece = currentPiece.map(p =>
      p.updatePositionByDirection(
        stageSize,
        stageMap.flatMap(_.currentPosition.toSet),
        currentDirection,
      ),
    ),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

  def putPieceOnStage(
      stageSize: StageSize,
      blockMaterial: Seq[BlockMaterial],
      putPiece: Piece,
  ): GameModel = copy(
    currentPiece = pieceFlow.map(p => p.head),
    pieceFlow = for {
      nextFlow <- pieceFlow.map(_.tail)
      candidateFlow      <- (!nextFlow.isEmpty).fold(
                         nextFlow.asRight,
                         Piece
                           .createPieceFlow(blockMaterial)
                           .map(Random.shuffle(_)),
                       )
    } yield candidateFlow,
    currentDirection = PieceDirection.Neutral,
    stageMap = {
      val nextMap                = stageMap + putPiece
      val filteredFillPositionY  =
        GameModel.filterFilledPositionY(nextMap, stageSize.width)
      val removableBlockPosition =
        (p: Piece) =>
          p.currentPosition.filter(v => filteredFillPositionY.contains(v.y))

      nextMap
        .map(p =>
          removableBlockPosition(p)
            .isEmpty
            .fold(p, p.removeBlock(removableBlockPosition(p))),
        )
        .map(p => p.shiftBlocksToDown(filteredFillPositionY))
    },
  )

object GameModel:

  private def apply(
      tickDelay: Seconds,
      lastUpdated: Seconds,
      pieceFlow: Either[Throwable, Seq[Piece]],
      stageMap: Set[Piece],
      currentDirection: PieceDirection,
      controlScheme: Seq[ControlScheme],
      tickPieceDown: Seconds,
      lastUpdatedPieceDown: Seconds,
  ): GameModel = GameModel(
    tickDelay = tickDelay,
    lastUpdated = lastUpdated,
    // take a Piece from initialized flow
    currentPiece = pieceFlow.map(_.head),
    // set the flow removed a first element
    pieceFlow = pieceFlow.map(_.tail),
    stageMap = stageMap,
    currentDirection = currentDirection,
    controlScheme = controlScheme,
    tickPieceDown = tickPieceDown,
    lastUpdatedPieceDown = lastUpdatedPieceDown,
  )

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameModel = GameModel(
    tickDelay = Seconds(TICK_FRAME_SECONDS),
    lastUpdated = Seconds.zero,
    pieceFlow = Piece
      .createPieceFlow(blockMaterial)
      .map(Random.shuffle(_)),
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

package nns.scalatris.scenes.game

import indigo.*
import indigo.logger._
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Boolean.*
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.{Piece, PieceDirection}
import nns.scalatris.types.StageSize

import scala.util.Random

final private val TICK_FRAME_SECONDS = 0.1f
final private val FALL_DOWN_SECONDS  = 1.0f

final case class GameModel private (
    piece: Either[Throwable, Piece],
    stageMap: Set[Piece],
    currentDirection: PieceDirection,
    controlScheme: Seq[ControlScheme],
    tickDelay: Seconds,
    tickPieceDown: Seconds,
    lastUpdated: Seconds,
    lastUpdatedPieceDown: Seconds,
):

  def dropOnePiece(
      currentTime: Seconds,
      stageSize: StageSize,
  ): GameModel = copy(
    piece = piece.map(p =>
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
    piece = piece.map(p =>
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
    piece = Piece.init(blockMaterial, Random.nextInt(blockMaterial.length)),
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

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameModel = GameModel(
    // TODO: put all blocks in order to not duplicated
    piece = Piece.init(
      blockMaterials = blockMaterial,
      index = Random.nextInt(blockMaterial.length),
    ),
    stageMap = Set(),
    currentDirection = PieceDirection.Neutral,
    controlScheme = Seq(
      PieceDirection.turningKeys,
      PieceDirection.rotatingKeys,
      PieceDirection.fallingKeys,
    ),
    tickDelay = Seconds(TICK_FRAME_SECONDS),
    tickPieceDown = Seconds(FALL_DOWN_SECONDS),
    lastUpdated = Seconds.zero,
    lastUpdatedPieceDown = Seconds.zero,
  )

  def filterFilledPositionY(
      map: Set[Piece],
      stageWidth: Double,
  ): Set[Double] = map
    .flatMap(_.currentPosition)
    .groupMapReduce(_.y)(_.x + 1)(_ + _)
    .filter((_, y) => y == factorialWidth(stageWidth))
    .keySet

  protected def factorialWidth(stageWidth: Double): Int =
    (1 to stageWidth.toInt).reduce(_ + _)

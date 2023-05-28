package nns.scalatris.scenes.game

import indigo.*
import indigo.logger._
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.fold
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.{Piece, PieceDirection}
import nns.scalatris.{GridSquareSize, ViewConfig}
import scala.util.Random

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

  def updatePieceSoon(
      currentTime: Seconds,
      stageSize: BoundingBox,
  ): GameModel = copy(
    piece = piece.map(p =>
      p.downPosition(
        stageSize = stageSize,
        placedPieces = stageMap.flatMap(_.current.toSet),
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
      stageSize: BoundingBox,
  ): GameModel = copy(
    piece = piece.map(p =>
      p.updatePositionByDirection(
        stageSize,
        stageMap.flatMap(_.current.toSet),
        currentDirection,
      ),
    ),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

  def putPieceOnStage(
      stageSize: BoundingBox,
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
        (p: Piece) => p.current.filter(v => filteredFillPositionY.contains(v.y))

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
    piece = Piece.init(blockMaterial, Random.nextInt(blockMaterial.length)),
    stageMap = Set(),
    currentDirection = PieceDirection.Neutral,
    controlScheme = Seq(
      PieceDirection.turningKeys,
      PieceDirection.rotatingKeys,
      PieceDirection.fallingKeys,
    ),
    tickDelay = Seconds(0.1),
    tickPieceDown = Seconds(1.0),
    lastUpdated = Seconds.zero,
    lastUpdatedPieceDown = Seconds.zero,
  )

  def filterFilledPositionY(
      map: Set[Piece],
      stageWidth: Double,
  ): Set[Double] = map
    .flatMap(_.current)
    .groupMapReduce(_.y)(_.x + 1)(_ + _)
    .filter(_._2 == factorialWidth(stageWidth))
    .keySet

  protected def factorialWidth(stageWidth: Double): Int =
    (1 to stageWidth.toInt).reduce(_ + _)

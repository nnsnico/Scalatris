package nns.scalatris.scenes.game

import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import indigo.logger._
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.{Piece, PieceDirection}
import nns.scalatris.{GridSquareSize, ViewConfig}
import nns.scalatris.extensions.fold

final case class GameModel private (
    piece: Option[Piece],
    stageMap: Set[Piece],
    currentDirection: PieceDirection,
    controlScheme: Seq[ControlScheme],
    tickDelay: Seconds,
    tickPieceDown: Seconds,
    lastUpdated: Seconds,
    lastUpdatedPieceDown: Seconds,
)

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameModel = GameModel(
    piece = Piece.init(blockMaterials = blockMaterial),
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

  def updateDirection(
      model: GameModel,
      direction: PieceDirection,
      lastUpdated: Seconds = Seconds.zero,
  ): GameModel =
    model.copy(
      currentDirection = direction,
      lastUpdated = lastUpdated,
    )

  def updatePiece(
      model: GameModel,
      currentTime: Option[Seconds],
      stageSize: BoundingBox,
  ): GameModel = model.copy(
    piece = model
      .piece
      .map(p =>
        p.updatePositionByDirection(
          stageSize,
          model.stageMap.flatMap(_.current.toSet),
          model.currentDirection,
        ),
      ),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

  def updatePieceSoon(
      model: GameModel,
      currentTime: Seconds,
      stageSize: BoundingBox,
  ): GameModel = model.copy(
    piece = model
      .piece
      .map(p =>
        p.downPosition(
          stageSize = stageSize,
          placedPieces = model.stageMap.flatMap(_.current.toSet),
        ),
      ),
    lastUpdatedPieceDown = currentTime,
  )

  def putPieceOnStage(
      model: GameModel,
      stageSize: BoundingBox,
      blockMaterial: Seq[BlockMaterial],
      putPiece: Piece,
  ): GameModel = model.copy(
    piece = Piece.init(blockMaterials = blockMaterial),
    currentDirection = PieceDirection.Neutral,
    stageMap = {
      val nextMap                = model.stageMap + putPiece
      val filteredFillPositionY  =
        filterFilledPositionY(nextMap, stageSize.width)
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

  def filterFilledPositionY(
      map: Set[Piece],
      stageWidth: Double,
  ): Set[Double] = map
    .flatMap(_.current)
    .groupMapReduce(_.y)(_.x + 1)(_ + _)
    .filter(_._2 == factorialWidth(stageWidth))
    .keySet

  def factorialWidth(stageWidth: Double): Int =
    (1 to stageWidth.toInt).reduce(_ + _)

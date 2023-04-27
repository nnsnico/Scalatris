package nns.scalatris.scenes.game

import indigo.*
import nns.scalatris.model.Piece
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.ViewConfig
import nns.scalatris.GridSquareSize
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.PieceDirection

final case class GameModel private (
    piece: Option[Piece],
    currentDirection: PieceDirection,
    controlScheme: ControlScheme,
    tickDelay: Seconds,
    lastUpdated: Seconds,
)

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameModel = GameModel(
    piece = Piece.init(
      x = viewConfig.stageHorizontalCenter,
      y = 0,
      blockMaterials = blockMaterial,
    ),
    currentDirection = PieceDirection.Neutral,
    controlScheme = PieceDirection.turningKeys,
    tickDelay = Seconds(0.1),
    lastUpdated = Seconds.zero,
  )

  def updateDirection(model: GameModel, direction: PieceDirection): GameModel =
    model.copy(
      currentDirection = direction
    )

  def updatePiece(
      model: GameModel,
      currentTime: Seconds,
      gridSquareSize: GridSquareSize,
  ): GameModel = model.copy(
    piece = model.piece.map(p => p.update(gridSquareSize, model.currentDirection)),
    lastUpdated = currentTime,
  )

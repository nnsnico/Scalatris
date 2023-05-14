package nns.scalatris.scenes.game

import indigo.*
import nns.scalatris.model.Piece
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.ViewConfig
import nns.scalatris.GridSquareSize
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.PieceDirection
import indigoextras.geometry.Vertex
import indigoextras.geometry.BoundingBox

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
      position = viewConfig.stageSize.position,
      blockMaterials = blockMaterial,
    ),
    currentDirection = PieceDirection.Neutral,
    controlScheme = PieceDirection.turningKeys,
    tickDelay = Seconds(0.1),
    lastUpdated = Seconds.zero,
  )

  def updateDirection(
      model: GameModel,
      lastUpdated: Option[Seconds],
      direction: PieceDirection,
  ): GameModel =
    model.copy(
      currentDirection = direction,
      lastUpdated = lastUpdated.getOrElse(Seconds.zero),
    )

  def updatePiece(
      model: GameModel,
      currentTime: Option[Seconds],
      stageSize: BoundingBox,
  ): GameModel = model.copy(
    piece = model.piece.map(p => p.update(stageSize, model.currentDirection)),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

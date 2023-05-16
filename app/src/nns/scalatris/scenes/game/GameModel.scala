package nns.scalatris.scenes.game

import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.model.PieceDirection.ControlScheme
import nns.scalatris.model.{Piece, PieceDirection}
import nns.scalatris.{GridSquareSize, ViewConfig}

final case class GameModel private (
    piece: Option[Piece],
    currentDirection: PieceDirection,
    controlScheme: Seq[ControlScheme],
    tickDelay: Seconds,
    lastUpdated: Seconds,
)

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameModel = GameModel(
    piece = Piece.init(blockMaterials = blockMaterial),
    currentDirection = PieceDirection.Neutral,
    controlScheme = Seq(
      PieceDirection.turningKeys,
      PieceDirection.rotatingKeys,
      PieceDirection.fallingKeys,
    ),
    tickDelay = Seconds(0.1),
    lastUpdated = Seconds.zero,
  )

  def updateDirection(
      model: GameModel,
      direction: PieceDirection,
  ): GameModel =
    model.copy(
      currentDirection = direction,
      lastUpdated = Seconds.zero,
    )

  def updatePiece(
      model: GameModel,
      currentTime: Option[Seconds],
      stageSize: BoundingBox,
  ): GameModel = model.copy(
    piece = model.piece.map(p => p.update(stageSize, model.currentDirection)),
    lastUpdated = currentTime.getOrElse(Seconds.zero),
  )

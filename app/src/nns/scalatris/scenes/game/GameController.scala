package nns.scalatris.scenes.game

import cats.syntax.all._
import indigo.*
import indigoextras.geometry.Vertex
import nns.scalatris.GridSquareSize
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions._
import nns.scalatris.model.Piece
import nns.scalatris.model.PieceDirection._

final case class GameController(gameModel: GameModel)

object GameController:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GameController = GameController(
    gameModel = GameModel.init(
      viewConfig = viewConfig,
      blockMaterial = blockMaterial,
    ),
  )

  def handleEvent(
      gameModel: GameModel,
      gameTime: GameTime,
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[GameModel] =
    case FrameTick
        if gameTime.running < gameModel.lastUpdated + gameModel.tickDelay =>
      Outcome(gameModel)
    case FrameTick        =>
      Outcome(
        GameModel.updatePiece(
          model = gameModel,
          currentTime = gameTime.running,
          stageSize = viewConfig.stageSize,
        ),
      )
    case e: KeyboardEvent =>
      Outcome(
        GameModel.updateDirection(
          model = gameModel,
          direction = gameModel.controlScheme.toPieceDirection(e),
        ),
      )

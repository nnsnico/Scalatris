package nns.scalatris.scenes.game

import cats.syntax.all._
import indigo.*
import indigoextras.geometry.Vertex
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions._
import nns.scalatris.model.PieceDirection._
import nns.scalatris.model.{Piece, PieceDirection}
import nns.scalatris.{GridSquareSize, ViewConfig}

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
          currentTime = gameTime.running.some,
          stageSize = viewConfig.stageSize,
        ),
      )
    case e: KeyboardEvent =>
      Outcome(
        GameModel.updateDirection(
          model = gameModel,
          direction = gameModel
            .controlScheme
            .map(_.toPieceDirection(e))
            .filter(_ != PieceDirection.Neutral)
            .headOption
            .getOrElse(PieceDirection.Neutral)
        ),
      )

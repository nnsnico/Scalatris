package nns.scalatris.scenes.game

import cats.syntax.all._
import indigo.*
import indigoextras.geometry.Vertex
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions._
import nns.scalatris.model.PieceDirection._
import nns.scalatris.model.{Piece, PieceDirection, PieceState}
import indigoextras.geometry.BoundingBox

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
      blockMaterial: Seq[BlockMaterial],
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[GameModel] =
    case FrameTick
        if gameTime.running < gameModel.lastUpdated + gameModel.tickDelay =>
      Outcome(gameModel)
    case FrameTick        =>
      gameModel
        .piece
        .map(p =>
          p.state match {
            case PieceState.Falling =>
              GameModel.updatePiece(
                model = gameModel,
                currentTime = gameTime.running.some,
                stageSize = viewConfig.stageSize,
              )
            case PieceState.Landed  =>
              GameModel.putPieceOnStage(
                gameModel,
                viewConfig.stageSize,
                blockMaterial,
                p,
              )
          },
        )
        .toOutcome
    case e: KeyboardEvent =>
      Outcome(
        GameModel.updateDirection(
          model = gameModel,
          direction = gameModel
            .controlScheme
            .map(_.toPieceDirection(e))
            .find(_ != PieceDirection.Neutral)
            .getOrElse(PieceDirection.Neutral),
        ),
      )

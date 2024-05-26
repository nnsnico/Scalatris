package nns.scalatris.scenes.game

import cats.syntax.all._
import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Either.*
import nns.scalatris.model.PieceDirection._
import nns.scalatris.model.{Piece, PieceDirection, PieceState}

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
    case FrameTick
        if gameTime.running >= gameModel.lastUpdatedPieceDown + gameModel.tickPieceDown =>
      Outcome(
        gameModel.dropOnePiece(
          currentTime = gameTime.running,
          stageSize = viewConfig.stageSize,
        ),
      )
    case FrameTick        =>
      gameModel
        .piece
        .map(p =>
          p.state match {
            case PieceState.Falling =>
              gameModel.updatePiece(
                currentTime = gameTime.running.some,
                stageSize = viewConfig.stageSize,
              )
            case PieceState.Landed  =>
              gameModel.putPieceOnStage(
                stageSize = viewConfig.stageSize,
                blockMaterial = blockMaterial,
                putPiece = p,
              )
          },
        )
        .toOutcome
    case e: KeyboardEvent =>
      Outcome(
        gameModel.updateDirection(
          direction = gameModel
            .controlScheme
            .map(_.toPieceDirection(e))
            .find(_ != PieceDirection.Neutral)
            .getOrElse(PieceDirection.Neutral),
        ),
      )

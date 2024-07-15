package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Either.*
import nns.scalatris.scenes.game.model.PieceDirection.*
import nns.scalatris.scenes.game.model.{Piece, PieceDirection, PieceState}

final case class GameController(gameModel: GameModel):

  def handleEvent(
      gameTime: GameTime,
      blockMaterial: Seq[BlockMaterial],
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[GameController] =
    case FrameTick
        if gameTime.running < gameModel.lastUpdated + gameModel.tickDelay =>
      Outcome(this)
    case FrameTick
        if gameTime.running >= gameModel.lastUpdatedPieceDown + gameModel.tickPieceDown =>
      Outcome(
        copy(
          gameModel = gameModel.dropOnePiece(
            currentTime = gameTime.running,
            stageSize = viewConfig.stageSize,
          ),
        ),
      )
    case FrameTick        =>
      gameModel.currentPiece.state match {
        case PieceState.Falling =>
          Outcome(
            copy(
              gameModel = gameModel.updatePiece(
                currentTime = gameTime.running.some,
                stageSize = viewConfig.stageSize,
              ),
            ),
          )
        case PieceState.Landed  =>
          gameModel
            .putPieceOnStage(
              stageSize = viewConfig.stageSize,
              blockMaterial = blockMaterial,
              putPiece = gameModel.currentPiece,
            )
            .map(updatedModel => copy(gameModel = updatedModel))
      }
    case e: KeyboardEvent =>
      Outcome(
        copy(
          gameModel = gameModel.updateDirection(
            direction = gameModel
              .controlScheme
              .map(_.toPieceDirection(e))
              .find(_ != PieceDirection.Neutral)
              .getOrElse(PieceDirection.Neutral),
          ),
        ),
      )
    case _                => Outcome(this)

object GameController:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): Either[Throwable, GameController] = for {
    gameModelOrThrow <- GameModel.init(viewConfig, blockMaterial)
  } yield GameController(
    gameModel = gameModelOrThrow,
  )


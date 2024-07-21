package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.extensions.Either.*
import nns.scalatris.scenes.game.model.PieceDirection.*
import nns.scalatris.scenes.game.model.{Piece, PieceDirection, PieceState}
import nns.scalatris.scenes.{BaseController, BaseSceneModel}

final case class GameController(
    override val model: GameModel,
) extends BaseController[GameModel]:

  def handleEvent(
      gameTime: GameTime,
      blockMaterial: Seq[BlockMaterial],
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[GameController] =
    case FrameTick if gameTime.running < model.lastUpdated + model.tickDelay =>
      Outcome(this)
    case FrameTick
        if gameTime.running >= model.lastUpdatedPieceDown + model.tickPieceDown =>
      Outcome(
        copy(
          model = model.dropOnePiece(
            currentTime = gameTime.running,
            stageSize = viewConfig.stageSize,
          ),
        ),
      )
    case FrameTick =>
      model.currentPiece.state match {
        case PieceState.Falling =>
          Outcome(
            copy(
              model = model.updatePiece(
                currentTime = gameTime.running.some,
                stageSize = viewConfig.stageSize,
              ),
            ),
          )
        case PieceState.Landed =>
          model
            .putPieceOnStage(
              stageSize = viewConfig.stageSize,
              blockMaterial = blockMaterial,
              putPiece = model.currentPiece,
            )
            .map(updatedModel => copy(model = updatedModel))
      }
    case e: KeyboardEvent =>
      Outcome(
        copy(
          model = model.updateDirection(
            direction = model
              .controlScheme
              .map(_.toPieceDirection(e))
              .find(_ != PieceDirection.Neutral)
              .getOrElse(PieceDirection.Neutral),
          ),
        ),
      )
    case _ =>
      Outcome(this)

object GameController:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): Either[Throwable, GameController] = for {
    gameModelOrThrow <- GameModel.init(viewConfig, blockMaterial)
  } yield GameController(
    model = gameModelOrThrow,
  )

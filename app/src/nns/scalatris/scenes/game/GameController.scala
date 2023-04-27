package nns.scalatris.scenes.game

import indigo.*
import nns.scalatris.GridSquareSize
import nns.scalatris.extensions._
import nns.scalatris.model.Piece
import cats.syntax.all._
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.model.PieceDirection._

final case class GameController(
    gameModel: GameModel,
):

  def updateFrame(
      viewConfig: ViewConfig,
      gameTime: GameTime,
  ): Outcome[GameController] = Outcome(
    if (gameTime.running < gameModel.lastUpdated + gameModel.tickDelay)
      this
    else
      copy(
        gameModel = GameModel.updatePiece(
          gameModel,
          gameTime.running,
          viewConfig.gridSquareSize,
        ),
      ),
  )

  def handleKeyboard(
      e: KeyboardEvent,
      gridSquareSize: GridSquareSize,
  ): Outcome[GameController] = Outcome(
    copy(
      gameModel = GameModel.updateDirection(
        gameModel,
        gameModel.controlScheme.toPieceDirection(e),
      ),
    ),
  )

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

package nns.scalatris.model

import indigo.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import indigo.shared.Outcome
import nns.scalatris.GridSquareSize
import nns.scalatris.scenes.game.GameController
import nns.scalatris.scenes.game.GameModel
import nns.scalatris.scenes.game.GameView
import nns.scalatris.model.PieceDirection.ControlScheme

final case class GlobalModel(
    gameController: GameController,
    gameState: GameState,
)

object GlobalModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GlobalModel = GlobalModel(
    gameController = GameController.init(viewConfig, blockMaterial),
    gameState = GameState.Running,
  )

enum GameState:
  case Running, Crashed

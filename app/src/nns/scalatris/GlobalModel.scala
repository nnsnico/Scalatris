package nns.scalatris

import indigo.*
import indigo.shared.Outcome
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial
import nns.scalatris.scenes.game.{GameController, GameModel, GameView}
import nns.scalatris.scenes.title.TitleController

final case class GlobalModel(
    titleController: TitleController,
    gameController: GameController,
    gameState: GameState,
)

object GlobalModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
  ): GlobalModel = GlobalModel(
    titleController = TitleController.init(viewConfig),
    gameController = GameController.init(viewConfig, blockMaterial),
    gameState = GameState.Running,
  )

enum GameState:
  case Running, Crashed

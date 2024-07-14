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
  ): Either[Throwable, GlobalModel] = for {
    gameConOrThrow <- GameController.init(viewConfig, blockMaterial)
  } yield GlobalModel(
    titleController = TitleController.init(viewConfig),
    gameController = gameConOrThrow,
    gameState = GameState.Running,
  )

enum GameState:
  case Running, Crashed

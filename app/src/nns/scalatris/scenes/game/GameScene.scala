package nns.scalatris.scenes.game

import indigo.*
import indigo.scenes.*
import indigo.shared.Outcome
import indigo.shared.scenegraph.Graphic
import nns.scalatris.assets._
import nns.scalatris.model.GlobalModel
import nns.scalatris.{StartUpData, _}

object GameScene extends Scene[StartUpData, GlobalModel, ViewModel]:
  type SceneViewModel = Group
  type SceneModel     = GameModel

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[GlobalModel, GameModel] =
    Lens(
      _.gameController.gameModel,
      (globalModel, model) =>
        globalModel.copy(
          gameController = globalModel.gameController.copy(gameModel = model),
        ),
    )

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.stage)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      model: GameModel,
  ): GlobalEvent => Outcome[GameModel] =
    GameController.handleEvent(
      gameModel = model,
      gameTime = context.gameTime,
      blockMaterial = context.startUpData.staticAssets.blockMaterial,
      viewConfig = context.startUpData.viewConfig,
    )

  override def updateViewModel(
      context: SceneContext[StartUpData],
      model: GameModel,
      viewModel: Group,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      model: GameModel,
      viewModel: Group,
  ): Outcome[SceneUpdateFragment] =
    GameView.update(
      viewConfig = context.startUpData.viewConfig,
      model = model,
      stage = viewModel,
    )

  override def subSystems: Set[SubSystem] = Set.empty

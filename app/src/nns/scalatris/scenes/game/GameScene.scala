package nns.scalatris.scenes.game

import indigo.*
import indigo.scenes.*
import nns.scalatris.assets.*
import nns.scalatris.{GlobalModel, StartUpData, ViewModel}

object GameScene extends Scene[StartUpData, GlobalModel, ViewModel]:
  type SceneViewModel = Group
  type SceneModel     = GameController

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[GlobalModel, SceneModel] =
    Lens(
      _.gameController,
      (globalModel, controller) =>
        globalModel.copy(
          gameController = globalModel
            .gameController
            .copy(
              gameModel = controller.gameModel,
            ),
        ),
    )

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.stage)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      controller: SceneModel,
  ): GlobalEvent => Outcome[SceneModel] =
    controller.handleEvent(
      gameTime = context.gameTime,
      blockMaterial = context.startUpData.staticAssets.blockMaterial,
      viewConfig = context.startUpData.viewConfig,
    )

  override def updateViewModel(
      context: SceneContext[StartUpData],
      controller: SceneModel,
      viewModel: Group,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      controller: SceneModel,
      viewModel: Group,
  ): Outcome[SceneUpdateFragment] =
    GameView.update(
      viewConfig = context.startUpData.viewConfig,
      model = controller.gameModel,
      stage = viewModel,
    )

  override def subSystems: Set[SubSystem] = Set.empty

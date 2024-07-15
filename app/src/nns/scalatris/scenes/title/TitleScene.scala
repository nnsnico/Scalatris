package nns.scalatris.scenes.title

import indigo.*
import indigo.scenes.*
import nns.scalatris.scenes.game.GameModel
import nns.scalatris.{GlobalModel, StartUpData, ViewModel}

object TitleScene extends Scene[StartUpData, GlobalModel, ViewModel]:
  type SceneViewModel = Group
  type SceneModel     = TitleController

  override def name: SceneName = SceneName("title")

  override def modelLens: Lens[GlobalModel, SceneModel] = Lens(
    _.titleController,
    (globalModel, controller) =>
      globalModel.copy(
        titleController = controller,
      ),
  )

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.title)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      controller: SceneModel,
  ): GlobalEvent => Outcome[SceneModel] = controller.handleEvent(
    gameTime = context.gameTime,
    viewConfig = context.startUpData.viewConfig,
  )

  override def updateViewModel(
      context: SceneContext[StartUpData],
      contoller: SceneModel,
      viewModel: SceneViewModel,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      controller: SceneModel,
      viewModel: SceneViewModel,
  ): Outcome[SceneUpdateFragment] = TitleView.update(
    model = controller.model,
    viewConfig = context.startUpData.viewConfig,
  )

  override def subSystems: Set[SubSystem] = Set.empty

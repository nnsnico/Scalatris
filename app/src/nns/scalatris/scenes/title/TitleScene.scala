package nns.scalatris.scenes.title

import indigo.scenes.{Scene, SceneContext, SceneName}
import indigo.shared.Outcome
import indigo.shared.events.{EventFilters, GlobalEvent}
import indigo.shared.scenegraph.{Group, SceneUpdateFragment}
import indigo.shared.subsystems.SubSystem
import indigo.shared.utils.Lens
import nns.scalatris.model.GlobalModel
import nns.scalatris.scenes.game.GameModel
import nns.scalatris.{StartUpData, ViewModel}

object TitleScene extends Scene[StartUpData, GlobalModel, ViewModel]:
  type SceneViewModel = Group
  type SceneModel     = TitleModel

  override def name: SceneName = SceneName("title")

  override def modelLens: Lens[GlobalModel, SceneModel] = Lens(
    _.titleController.titleModel,
    (globalModel, model) =>
      globalModel.copy(
        titleController = globalModel.titleController.copy(titleModel = model),
      ),
  )

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.title)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      model: SceneModel,
  ): GlobalEvent => Outcome[SceneModel] = _ => Outcome(model)

  override def updateViewModel(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel,
  ): Outcome[SceneUpdateFragment] = TitleView.update(
    model = model,
    viewConfig = context.startUpData.viewConfig,
  )

  override def subSystems: Set[SubSystem] = Set.empty

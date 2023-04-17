package nns.scalatris.scenes.game

import indigo.*
import indigo.scenes.*
import nns.scalatris.StartUpData
import indigo.shared.Outcome
import indigo.shared.scenegraph.Graphic
import nns.scalatris.assets.Font
import nns.scalatris.GridSquareSize._
import nns.scalatris.assets.Grid
import nns.scalatris.ViewModel
import nns.scalatris.model.GameModel

object GameScene extends Scene[StartUpData, GameModel, ViewModel]:
  type SceneModel     = GameModel
  type SceneViewModel = Group

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[GameModel, GameModel] = Lens.keepLatest

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.stage)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      model: GameModel,
  ): GlobalEvent => Outcome[SceneModel] =
    model.update(
      gridSquareSize = context.startUpData.viewConfig.gridSquareSize,
      gameTime = context.gameTime,
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
  ): Outcome[SceneUpdateFragment] = GameView.update(
    viewConfig = context.startUpData.viewConfig,
    model = model,
    stage = viewModel,
  )

  override def subSystems: Set[SubSystem] = Set()

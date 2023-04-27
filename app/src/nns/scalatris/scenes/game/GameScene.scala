package nns.scalatris.scenes.game

import indigo.*
import indigo.scenes.*
import indigo.shared.Outcome
import indigo.shared.scenegraph.Graphic
import indigo.IndigoLogger.*
import nns.scalatris._
import nns.scalatris.StartUpData
import nns.scalatris.GridSquareSize._
import nns.scalatris.assets._
import nns.scalatris.model.GlobalModel

object GameScene extends BaseScene[StartUpData, GlobalModel, ViewModel]:
  type SceneViewModel = Group
  type Controller     = GameController

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[GlobalModel, GameController] =
    Lens(
      _.gameController,
      (globalModel, controller) =>
        globalModel.copy(gameController = controller),
    )

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.stage)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      controller: GameController,
  ): GlobalEvent => Outcome[GameController] =
    case FrameTick        =>
      controller.updateFrame(
        viewConfig = context.startUpData.viewConfig,
        gameTime = context.gameTime,
      )
    case e: KeyboardEvent =>
      controller.handleKeyboard(
        e,
        context.startUpData.viewConfig.gridSquareSize,
      )

  override def updateViewModel(
      context: SceneContext[StartUpData],
      model: Controller,
      viewModel: Group,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      controller: Controller,
      viewModel: Group,
  ): Outcome[SceneUpdateFragment] =
    GameView.update(
      viewConfig = context.startUpData.viewConfig,
      model = controller.gameModel,
      stage = viewModel,
    )

  override def subSystems: Set[SubSystem] = Set()

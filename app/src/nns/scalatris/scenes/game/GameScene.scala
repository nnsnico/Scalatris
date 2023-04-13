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

object GameScene extends Scene[StartUpData, Unit, ViewModel]:
  type SceneModel     = Unit
  type SceneViewModel = Group

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[Unit, Unit] = Lens.unit

  override def viewModelLens: Lens[ViewModel, Group] = Lens.readOnly(_.stage)

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      model: Unit,
  ): GlobalEvent => Outcome[SceneModel] = _ => Outcome(())

  override def updateViewModel(
      context: SceneContext[StartUpData],
      model: Unit,
      viewModel: Group,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[StartUpData],
      model: Unit,
      viewModel: Group,
  ): Outcome[SceneUpdateFragment] = Outcome {
    val horizontalCenter = context.startUpData.viewConfig.horizontalCenter
    val verticalCenter   = context.startUpData.viewConfig.verticalCenter
    SceneUpdateFragment
      .empty
      .addLayer(
        Layer(
          BindingKey("ui"),
          viewModel.children |+|
            Batch(
              Font.toText("scalatris", horizontalCenter, verticalCenter).alignCenter,
            ),
        ),
      )
  }

  override def subSystems: Set[SubSystem] = Set()

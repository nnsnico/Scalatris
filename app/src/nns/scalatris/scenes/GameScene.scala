package nns.scalatris.scenes

import indigo.*
import indigo.scenes.*
import nns.scalatris.StartUpData
import indigo.shared.Outcome
import indigo.shared.scenegraph.Graphic
import nns.scalatris.assets.Font
import nns.scalatris.GridSquareSize._
import nns.scalatris.assets.Grid

object GameScene extends Scene[StartUpData, Unit, Unit]:
  type SceneModel     = Unit
  type SceneViewModel = Unit

  override def name: SceneName = SceneName("scalatris")

  override def modelLens: Lens[Unit, Unit] = Lens.unit

  override def viewModelLens: Lens[Unit, Unit] = Lens.unit

  override def eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  override def updateModel(
      context: SceneContext[StartUpData],
      model: Unit,
  ): GlobalEvent => Outcome[SceneModel] = _ => Outcome(())

  override def updateViewModel(
      context: SceneContext[StartUpData],
      model: Unit,
      viewModel: Unit,
  ): GlobalEvent => Outcome[SceneViewModel] = _ => Outcome(())

  override def present(
      context: SceneContext[StartUpData],
      model: Unit,
      viewModel: Unit,
  ): Outcome[SceneUpdateFragment] = Outcome {
    val horizontalCenter = context.startUpData.viewConfig.horizontalCenter
    val verticalCenter   = context.startUpData.viewConfig.verticalCenter
    SceneUpdateFragment
      .empty
      .addLayer(
        Layer(
          BindingKey("ui"),
          Batch.fromSeq(
            context.startUpData.staticAssets.stage ++ context
              .startUpData
              .staticAssets
              .piece
              .createPiece(),
          ) |+|
            Batch(
              Font.toText("scalatris", horizontalCenter, verticalCenter).alignCenter,
            ),
        ),
      )
  }

  override def subSystems: Set[SubSystem] = Set()

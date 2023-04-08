package nns.tetris.scenes

import indigo.*
import indigo.scenes.*
import nns.tetris.StartUpData
import indigo.shared.Outcome
import indigo.shared.scenegraph.Graphic
import nns.tetris.assets.Font
import nns.tetris.GridSquareSize._
import nns.tetris.assets.Grid

object TetrisScene extends Scene[StartUpData, Unit, Unit]:
  type SceneModel     = Unit
  type SceneViewModel = Unit

  override def name: SceneName = SceneName("tetris")

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
          Batch.fromSeq(context.startUpData.staticAssets.stage) |+|
            Batch.fromSeq(
              context
                .startUpData
                .staticAssets
                .tetrimino
                .createPiece(
                  offsetX = context.startUpData.viewConfig.gridSquareSize,
                  offsetY = context.startUpData.viewConfig.gridSquareSize,
                ),
            ) |+|
            Batch(
              Font.toText("hogehoge", horizontalCenter, verticalCenter).alignCenter,
            ),
        ),
      )
  }

  override def subSystems: Set[SubSystem] = Set()

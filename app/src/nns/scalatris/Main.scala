package nns.scalatris

import cats.syntax.all.*
import indigo.*
import indigo.scenes.*
import nns.scalatris.extensions.Either.toOutcome
import nns.scalatris.scenes.game.GameScene
import nns.scalatris.scenes.title.TitleScene

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Main extends IndigoGame[ViewConfig, StartUpData, GlobalModel, ViewModel]:

  // 1. Initialize View config
  override def boot(
      flags: Map[String, String],
  ): Outcome[BootResult[ViewConfig]] = Outcome {
    val viewConfig = ViewConfig.default
    val config     = GameConfig(
      viewport = viewConfig.viewport,
      clearColor = RGBA.Black,
      magnification = viewConfig.magnificationLevel,
    )

    val path: String =
      flags.getOrElse("baseUrl", "")

    BootResult(config, viewConfig)
      .withAssets(assets.allAssets(path))
      .withFonts(assets.Font.info)
  }

  // 2. Setup global config (ViewConfig, Assets)
  override def setup(
      viewConfig: ViewConfig,
      assetCollection: AssetCollection,
      dice: Dice,
  ): Outcome[Startup[StartUpData]] = StartUpData.initialize(viewConfig)

  // All Scenes
  override def scenes(
      viewConfig: ViewConfig,
  ): NonEmptyList[Scene[StartUpData, GlobalModel, ViewModel]] =
    NonEmptyList(
      GameScene,
      TitleScene,
    )

  // The first Scene
  override def initialScene(viewConfig: ViewConfig): Option[SceneName] =
    TitleScene.name.some

  // initialize global game model
  override def initialModel(startupData: StartUpData): Outcome[GlobalModel] =
    GlobalModel
      .init(
        viewConfig = startupData.viewConfig,
        blockMaterial = startupData.staticAssets.blockMaterial,
      )
      .toOutcome

  // on update global game model
  override def updateModel(
      context: FrameContext[StartUpData],
      model: GlobalModel,
  ): GlobalEvent => Outcome[GlobalModel] =
    _ => Outcome(model)

  // initialize view state
  override def initialViewModel(
      startupData: StartUpData,
      model: GlobalModel,
  ): Outcome[ViewModel] =
    Outcome(
      ViewModel.init(startupData),
    )

  // on update view state
  override def updateViewModel(
      context: FrameContext[StartUpData],
      model: GlobalModel,
      viewModel: ViewModel,
  ): GlobalEvent => Outcome[ViewModel] = _ => Outcome(viewModel)

  override def present(
      context: FrameContext[StartUpData],
      model: GlobalModel,
      viewModel: ViewModel,
  ): Outcome[SceneUpdateFragment] = Outcome(
    SceneUpdateFragment
      .empty
      .addLayer(Layer(BindingKey("ui"))),
  )

  override def eventFilters: EventFilters = EventFilters.Restricted

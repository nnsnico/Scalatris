package nns.scalatris.scenes.title

import indigo.*
import indigo.scenes.SceneEvent.JumpTo
import indigo.shared.Outcome
import nns.scalatris.extensions.Option.toOutcome
import nns.scalatris.scenes.game.GameScene
import nns.scalatris.scenes.title.model.CursorDirection
import nns.scalatris.{GameEvent, ViewConfig}

final case class TitleController(viewConfig: ViewConfig, titleModel: TitleModel)

object TitleController:

  def init(viewConfig: ViewConfig): TitleController = TitleController(
    viewConfig = viewConfig,
    titleModel = TitleModel.init(),
  )

  def handleEvent(
      titleModel: TitleModel,
      gameTime: GameTime,
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[TitleModel] =
    case GameEvent.StartGame =>
      Outcome(titleModel).addGlobalEvents(JumpTo(GameScene.name))
    case FrameTick           =>
      Outcome(titleModel)
    case e: KeyboardEvent    =>
      titleModel.updateDirection(
        direction = titleModel
          .controlScheme
          .map(_.toCursorDirection(e))
          .find(_ != CursorDirection.Neutral)
          .getOrElse(CursorDirection.Neutral),
      )
    case _                   => Outcome(titleModel)

package nns.scalatris.scenes.title

import indigo.*
import indigo.scenes.SceneEvent.JumpTo
import indigo.shared.Outcome
import nns.scalatris.extensions.Option.toOutcome
import nns.scalatris.scenes.BaseController
import nns.scalatris.scenes.game.GameScene
import nns.scalatris.scenes.title.model.CursorDirection
import nns.scalatris.{GameEvent, ViewConfig}

final case class TitleController(
    viewConfig: ViewConfig,
    override val model: TitleModel,
) extends BaseController[TitleModel]:

  def handleEvent(
      gameTime: GameTime,
      viewConfig: ViewConfig,
  ): GlobalEvent => Outcome[TitleController] =
    case GameEvent.StartGame =>
      Outcome(this).addGlobalEvents(JumpTo(GameScene.name))
    case FrameTick           =>
      Outcome(this)
    case e: KeyboardEvent    =>
      model
        .updateDirection(
          direction = model
            .controlScheme
            .map(_.toCursorDirection(e))
            .find(_ != CursorDirection.Neutral)
            .getOrElse(CursorDirection.Neutral),
        )
        .map(updatedModel => copy(model = updatedModel))

    case _ => Outcome(this)

object TitleController:

  def init(viewConfig: ViewConfig): TitleController = TitleController(
    viewConfig = viewConfig,
    model = TitleModel.init(),
  )


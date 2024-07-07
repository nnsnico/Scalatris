package nns.scalatris.scenes.title

import indigo.*
import indigo.scenes.*
import nns.scalatris.assets.Font as GameFont
import nns.scalatris.model.{Entry, Title, toText}
import nns.scalatris.{ViewConfig, ViewModel}

object TitleView:

  private final val CURSOR_OFFSET = 10

  def update(
      model: TitleModel,
      viewConfig: ViewConfig,
  ): Outcome[SceneUpdateFragment] = Outcome(
    SceneUpdateFragment
      .empty
      .addLayer(
        Layer(
          BindingKey("title-ui"),
          drawTitle(
            title = model.title,
            viewConfig = viewConfig,
          ) ++ drawEntry(
            entries = model.selectableItems,
            viewConfig = viewConfig,
          ),
        ),
      ),
  )

  private def drawTitle(
      title: Title,
      viewConfig: ViewConfig,
  ): Batch[SceneNode] = Batch(
    GameFont
      .toText(
        text = title.toString,
        x = viewConfig.horizontalCenter,
        y = 50,
      )
      .copy(
        rotation = Radians.fromDegrees(-10),
        scale = Vector2(2.0),
        alignment = TextAlignment.Center,
      ),
  )

  private def drawEntry(
      entries: NonEmptyList[Entry],
      viewConfig: ViewConfig,
  ): Batch[SceneNode] =
    entries.map { case entry =>
      GameFont
        .toText(
          text = s"${entry.status.toText} ${entry.text}",
          x = viewConfig.horizontalCenter - CURSOR_OFFSET,
          y = (150 + (entry.index.value * viewConfig.gridSquareSize.toInt)),
        )
        .copy(
          alignment = TextAlignment.Center,
        )
    }.toBatch



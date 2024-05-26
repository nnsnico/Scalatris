package nns.scalatris.scenes.title

import indigo._
import indigo.shared.Outcome
import indigo.shared.scenegraph.{Layer, SceneUpdateFragment}
import nns.scalatris.assets.{Font => GameFont}
import nns.scalatris.model.{Entry, Title}
import nns.scalatris.{ViewConfig, ViewModel}
import indigoextras.geometry.BoundingBox
import indigo.shared.datatypes.Vector2

object TitleView:

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
    entries
      .zipWithIndex
      .map { case (entry, index) =>
        GameFont
          .toText(
            text = entry.toString(),
            x = viewConfig.horizontalCenter,
            y = (150 + (index * viewConfig.gridSquareSize.toInt)),
          )
          .copy(
            alignment = TextAlignment.Center,
          )
      }
      .toBatch

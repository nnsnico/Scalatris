package nns.scalatris.scenes.title

import indigo.shared.collections.NonEmptyList
import nns.scalatris.model.{Entry, Title}

final case class TitleModel private (
    title: Title,
    selectableItems: NonEmptyList[Entry],
)

object TitleModel:

  def init(): TitleModel = TitleModel(
    title = Title("Scalatris"),
    selectableItems = NonEmptyList(
      Entry("start"),
      Entry("exit"),
    ),
  )

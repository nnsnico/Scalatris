package nns.scalatris.scenes.title

import cats.data.{NonEmptyList, OptionT}
import cats.syntax.all.*
import indigo.*
import indigo.NonEmptyList as IndigoNel
import indigo.shared.Outcome
import nns.scalatris.GameEvent
import nns.scalatris.extension.NonEmptyList.toIndigoNel
import nns.scalatris.extensions.Boolean.fold
import nns.scalatris.extensions.Option.toOutcome
import nns.scalatris.scenes.*
import nns.scalatris.scenes.title.ChoicesExtension.{
  moveCursorToDown,
  moveCursorToUp,
  updateStatusByIndex,
}
import nns.scalatris.scenes.title.model.*

final case class TitleModel private (
    override val tickDelay: Seconds,
    override val lastUpdated: Seconds,
    controlScheme: Seq[CursorDirection.ControlScheme],
    title: Title,
    selectableItems: IndigoNel[Entry],
) extends BaseSceneModel(tickDelay, lastUpdated):

  val selectingItem = selectableItems.find(_.status == SelectStatus.Selecting)

  def updateDirection(
      direction: CursorDirection,
  ): Outcome[TitleModel] = direction match
    case CursorDirection.Up    =>
      copy(
        selectableItems = selectableItems.moveCursorToUp(selectingItem),
      ).some.toOutcome
    case CursorDirection.Down  =>
      copy(
        selectableItems = selectableItems.moveCursorToDown(selectingItem),
      ).some.toOutcome
    case CursorDirection.Enter =>
      this.some.toOutcome.addGlobalEvents(GameEvent.StartGame)
    case _                     =>
      this.some.toOutcome

object TitleModel:

  private val selectableItems = NonEmptyList
    .of(
      ("start", SelectStatus.Selecting),
      ("exit", SelectStatus.NotSelected),
    )
    .zipWithIndex
    .map { case ((title, status), i) =>
      Entry(Index(i), title, status)
    }
    .toIndigoNel()

  def init(): TitleModel = TitleModel(
    tickDelay = Seconds(TICK_FRAME_SECONDS),
    lastUpdated = Seconds.zero,
    controlScheme = Seq(
      CursorDirection.moveUpKeys,
      CursorDirection.moveDownKeys,
      CursorDirection.enterKeys,
    ),
    title = Title("Scalatris"),
    selectableItems = selectableItems,
  )

private object ChoicesExtension:

  extension (items: IndigoNel[Entry])

    def moveCursorToUp(selectingItem: Option[Entry]): IndigoNel[Entry] =
      items.map { case entry @ Entry(i, _, s) =>
        (for {
          selectingIndex <- selectingItem.map(_.index)
          index           =
            if (selectingIndex.dec.value >= items.first.index.value) {
              selectingIndex.dec
            } else items.first.index
          nextIndex      <- (index == i).fold(i.some, none)
          updated        <- items.updateStatusByIndex(
                              nextIndex,
                              SelectStatus.Selecting,
                            )
        } yield updated)
          .getOrElse(entry.copy(status = SelectStatus.NotSelected))
      }

    def moveCursorToDown(selectingItem: Option[Entry]): IndigoNel[Entry] =
      items.map { case entry @ Entry(i, _, s) =>
        (for {
          selectingIndex <- selectingItem.map(_.index)
          index           =
            if (selectingIndex.inc.value < items.last.index.value) {
              selectingIndex.inc
            } else items.last.index
          nextIndex      <- (index == i).fold(i.some, none)
          updated        <- items.updateStatusByIndex(
                              nextIndex,
                              SelectStatus.Selecting,
                            )
        } yield updated)
          .getOrElse(entry.copy(status = SelectStatus.NotSelected))
      }

    def updateStatusByIndex(i: Index, status: SelectStatus): Option[Entry] =
      for {
        targetIndex  <- items.find(_.index == i)
        toggledStatus = targetIndex.copy(status = status)
      } yield toggledStatus

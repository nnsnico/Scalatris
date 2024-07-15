package nns.scalatris.scenes.title

import cats.data.{NonEmptyList, OptionT}
import cats.syntax.all.*
import indigo.*
import indigo.NonEmptyList as IndigoNel
import mouse.all.*
import nns.scalatris.GameEvent
import nns.scalatris.extension.NonEmptyList.toIndigoNel
import nns.scalatris.extensions.Option.toOutcome
import nns.scalatris.scenes.*
import nns.scalatris.scenes.title.extensions.ChoicesExtension.*
import nns.scalatris.scenes.title.model.*

final case class TitleModel private[title] (
    override val tickDelay: Seconds,
    override val lastUpdated: Seconds,
    controlScheme: Seq[CursorDirection.ControlScheme],
    title: Title,
    selectableItems: IndigoNel[Entry],
) extends BaseSceneModel:

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
    )
    .zipWithIndex
    .map { case ((title, status), i) =>
      Entry(Index(i), title, status)
    }
    .toIndigoNel

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

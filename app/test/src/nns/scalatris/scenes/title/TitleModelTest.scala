package nns.scalatris.scenes.title

import cats.data.NonEmptyList
import indigo.*
import indigo.NonEmptyList as IndigoNel
import munit.*
import nns.scalatris.extension.NonEmptyList.toIndigoNel
import nns.scalatris.scenes.title.TitleModel
import nns.scalatris.scenes.title.model.*
import nns.scalatris.scenes.title.model.CursorDirection.ControlScheme

class TitleModelTest extends FunSuite {

  private val target = TitleModel(
    tickDelay = Seconds.zero,
    lastUpdated = Seconds.zero,
    controlScheme = Seq(
      CursorDirection.moveUpKeys,
      CursorDirection.moveDownKeys,
      CursorDirection.enterKeys,
    ),
    title = Title("Scalatris"),
    selectableItems = NonEmptyList
      .of(
        ("start", SelectStatus.Selecting),
        ("exit", SelectStatus.NotSelected),
      )
      .zipWithIndex
      .map { case ((title, status), i) =>
        Entry(Index(i), title, status)
      }
      .toIndigoNel,
  )

  test(
    "updateDirection should returned TitleModel with updated direction",
  ) {
    val downDirection = CursorDirection.Down

    val actual = target
      .updateDirection(direction = downDirection)
      .getOrElse(fail("Unexpected error"))

    val selectingStatus   =
      actual.selectableItems.find(_.status == SelectStatus.Selecting)
    val notSelectedStatus =
      actual.selectableItems.find(_.status == SelectStatus.NotSelected)

    assertEquals(
      actual.selectableItems.length,
      2,
    )

    notSelectedStatus.fold(
      fail("notSelectedStatus is None"),
    ) {
      assertEquals(
        _,
        Entry(
          Index(0),
          "start",
          SelectStatus.NotSelected,
        ),
      )
    }
    selectingStatus.fold(
      fail("selectingStatus is None"),
    ) {
      assertEquals(
        _,
        Entry(
          Index(1),
          "exit",
          SelectStatus.Selecting,
        ),
      )
    }
  }

  test(
    "updateDirection should returned unupdated TitleModel when attempting to move cursor in the direction of no choice",
  ) {
    val upDirection = CursorDirection.Up

    val actual = target
      .updateDirection(direction = upDirection)
      .getOrElse(fail("Unexpected error"))

    val selectingStatus   =
      actual.selectableItems.find(_.status == SelectStatus.Selecting).getOrElse(fail("Unexpected error"))
    val notSelectedStatus =
      actual.selectableItems.find(_.status == SelectStatus.NotSelected).getOrElse(fail("Unexpected error"))

    assertEquals(
      actual.selectableItems.length,
      2,
    )
    assertEquals(
      selectingStatus,
      Entry(
        Index(0),
        "start",
        SelectStatus.Selecting,
      ),
    )
    assertEquals(
      notSelectedStatus,
      Entry(
        Index(1),
        "exit",
        SelectStatus.NotSelected,
      ),
    )
  }
}

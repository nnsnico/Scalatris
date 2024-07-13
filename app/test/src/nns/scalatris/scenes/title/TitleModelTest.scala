package nns.scalatris.scenes.title

import indigo.*
import munit.*
import nns.scalatris.scenes.title.TitleModel
import nns.scalatris.scenes.title.model.*

class TitleModelTest extends FunSuite {
  private val target = TitleModel.init()

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
      actual.selectableItems.find(_.status == SelectStatus.Selecting).get
    val notSelectedStatus =
      actual.selectableItems.find(_.status == SelectStatus.NotSelected).get

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

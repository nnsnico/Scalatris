package nns.scalatris.scenes.title.extensions

import indigo.NonEmptyList as IndigoNel
import mouse.all.*
import nns.scalatris.scenes.title.model.{Entry, Index, SelectStatus}

object ChoicesExtension:

  extension (items: IndigoNel[Entry])

    def moveCursorToUp(selectingItem: Option[Entry]): IndigoNel[Entry] =
      items.map { case entry @ Entry(i, _, s) =>
        (for {
          selectingIndex  <- selectingItem.map(_.index)
          nextIndexInRange = items
                               .in(selectingIndex.dec)
                               .fold(selectingIndex.dec, items.first.index)
          nextIndex       <- (nextIndexInRange == i).option(i)
          updated         <- items.updateStatusByIndex(
                               nextIndex,
                               SelectStatus.Selecting,
                             )
        } yield updated)
          .getOrElse(entry.copy(status = SelectStatus.NotSelected))
      }

    def moveCursorToDown(selectingItem: Option[Entry]): IndigoNel[Entry] =
      items.map { case entry @ Entry(i, _, s) =>
        (for {
          selectingIndex  <- selectingItem.map(_.index)
          nextIndexInRange = items
                               .in(selectingIndex.inc)
                               .fold(selectingIndex.inc, items.last.index)
          nextIndex       <- (nextIndexInRange == i).option(i)
          updated         <- items.updateStatusByIndex(
                               nextIndex,
                               SelectStatus.Selecting,
                             )
        } yield updated)
          .getOrElse(entry.copy(status = SelectStatus.NotSelected))
      }

    private inline def in(index: Index): Boolean = (for {
      contains <- items.map(_.index).find(_ == index)
    } yield contains).cata(_ => true, false)

    private def updateStatusByIndex(
        i: Index,
        status: SelectStatus,
    ): Option[Entry] =
      for {
        targetIndex  <- items.find(_.index == i)
        toggledStatus = targetIndex.copy(status = status)
      } yield toggledStatus

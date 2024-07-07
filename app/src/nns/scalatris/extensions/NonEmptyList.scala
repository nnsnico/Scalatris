package nns.scalatris.extension

import cats.data.NonEmptyList
import indigo.{NonEmptyList => IndigoNel}

object NonEmptyList:

  extension [T](nel: NonEmptyList[T])

    def toIndigoNel(): IndigoNel[T] = IndigoNel(
      nel.head,
      nel.tail,
    )

package nns.scalatris.extensions

object CollectionExt:

  extension [T, B: Ordering](l: Iterable[T])
    def maxOr(value: T => B, or: T): T = l.maxByOption(value).getOrElse(or)
    def minOr(value: T => B, or: T): T = l.minByOption(value).getOrElse(or)

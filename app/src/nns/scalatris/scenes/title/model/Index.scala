package nns.scalatris.scenes.title.model

import nns.scalatris.extensions.Boolean.fold

opaque type Index = Int

object Index:
  def apply(i: Int): Index = i

  extension (i: Index)
    def value: Int               = i
    def dec: Index               = i - 1
    def inc: Index               = i + 1
    def ==(source: Int): Boolean = i == source

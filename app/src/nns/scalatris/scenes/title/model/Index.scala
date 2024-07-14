package nns.scalatris.scenes.title.model

opaque type Index = Int

object Index:
  def apply(i: Int): Index = i

  extension (i: Index)
    def value: Int               = i
    inline def dec: Index        = i - 1
    inline def inc: Index        = i + 1
    inline def ==(source: Int): Boolean = i == source

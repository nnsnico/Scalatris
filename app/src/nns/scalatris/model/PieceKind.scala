package nns.scalatris.model

import indigoextras.geometry.Vertex

sealed class PieceKind(initPosition: Vertex, localPos: Seq[Vertex])

object PieceKind:

  val values: Seq[PieceKind] = Seq(
    IKind(),
    JKind(),
    LKind(),
    OKind(),
    SKind(),
    TKind(),
    ZKind(),
  )

  case class IKind(
      initPosition: Vertex = Vertex(1, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-1.5, 0),
        Vertex(-0.5, 0),
        Vertex(0.5, 0),
        Vertex(1.5, 0),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class JKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(1.0, -0.5),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class LKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(-1.0, -0.5),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class OKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-0.5, 0.5),
        Vertex(0.5, 0.5),
        Vertex(-0.5, -0.5),
        Vertex(0.5, -0.5),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class SKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(0.0, 0.5),
        Vertex(1.0, 0.5),
        Vertex(-1.0, -0.5),
        Vertex(0.0, -0.5),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class TKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.0),
        Vertex(0.0, 0.0),
        Vertex(1.0, 0.0),
        Vertex(0.0, 1.0),
      ),
  ) extends PieceKind(initPosition, localPos)

  case class ZKind(
      initPosition: Vertex = Vertex(0, 0),
      localPos: Seq[Vertex] = Seq(
        Vertex(-1.0, 0.5),
        Vertex(0.0, 0.5),
        Vertex(0.0, -0.5),
        Vertex(1.0, -0.5),
      ),
  ) extends PieceKind(initPosition, localPos)

package nns.scalatris.scenes.game.model

import indigo.*

enum PieceKind(val initPosition: Vertex, val localPos: Seq[Vertex]):

  case I
      extends PieceKind(
        initPosition = Vertex(1, 0),
        localPos = Seq(
          Vertex(-1.5, 0),
          Vertex(-0.5, 0),
          Vertex(0.5, 0),
          Vertex(1.5, 0),
        ),
      )

  case J
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(-1.0, 0.5),
          Vertex(0.0, 0.5),
          Vertex(1.0, 0.5),
          Vertex(1.0, -0.5),
        ),
      )

  case L
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(-1.0, 0.5),
          Vertex(0.0, 0.5),
          Vertex(1.0, 0.5),
          Vertex(-1.0, -0.5),
        ),
      )

  case O
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(-0.5, 0.5),
          Vertex(0.5, 0.5),
          Vertex(-0.5, -0.5),
          Vertex(0.5, -0.5),
        ),
      )

  case S
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(0.0, 0.5),
          Vertex(1.0, 0.5),
          Vertex(-1.0, -0.5),
          Vertex(0.0, -0.5),
        ),
      )

  case T
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(-1.0, 0.0),
          Vertex(0.0, 0.0),
          Vertex(1.0, 0.0),
          Vertex(0.0, 1.0),
        ),
      )

  case Z
      extends PieceKind(
        initPosition = Vertex(0, 0),
        localPos = Seq(
          Vertex(-1.0, 0.5),
          Vertex(0.0, 0.5),
          Vertex(0.0, -0.5),
          Vertex(1.0, -0.5),
        ),
      )

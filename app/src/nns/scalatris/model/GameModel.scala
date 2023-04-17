package nns.scalatris.model

import indigo.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets._
import indigo.shared.Outcome
import nns.scalatris.model.ControlScheme._
import nns.scalatris.GridSquareSize

final case class GameModel(
    gameState: GameState,
    piece: Option[Piece],
    pieceState: PieceState,
    controlScheme: ControlScheme,
    tickDelay: Seconds,
    lastUpdated: Seconds,
):

  def update(
      gridSquareSize: GridSquareSize,
      gameTime: GameTime,
  ): GlobalEvent => Outcome[GameModel] =
    // 指定した秒間隔内の場合、現在の状態を維持する
    case FrameTick if gameTime.running < lastUpdated + tickDelay =>
      Outcome(this)

    // PieceDirectionの状態に応じたモデルの更新
    case FrameTick =>
      gameState match {
        case s @ GameState.Running =>
          GameModel.updateRunning(
            gridSquareSize = gridSquareSize,
            gameTime = gameTime,
            state = this,
          )(FrameTick)
        case s @ GameState.Crashed =>
          ???
      }

    // 割り込みでキーイベントをキャッチする
    case e =>
      gameState match {
        case s @ GameState.Running =>
          GameModel.updateRunning(
            gridSquareSize = gridSquareSize,
            gameTime = gameTime,
            state = this,
          )(e)
        case s @ GameState.Crashed =>
          ???
      }

object GameModel:

  def init(
      viewConfig: ViewConfig,
      blockMaterial: Seq[BlockMaterial],
      controlScheme: ControlScheme,
  ): GameModel = GameModel(
    gameState = GameState.Running,
    piece = Piece.init(
      x = viewConfig.stageHorizontalCenter,
      y = 0,
      blockMaterials = blockMaterial,
    ),
    pieceState = PieceState.INITIALIZE,
    controlScheme = controlScheme,
    tickDelay = Seconds(1),
    lastUpdated = Seconds.zero,
  )

  def updateRunning(
      gridSquareSize: GridSquareSize,
      gameTime: GameTime,
      state: GameModel,
  ): GlobalEvent => Outcome[GameModel] =
    case FrameTick        =>
      Outcome(
        state.copy(
          piece = state.piece.map(p => p.update(gridSquareSize)),
        ),
      )
    case e: KeyboardEvent =>
      Outcome(
        state.copy(
          piece = state.piece.map(p => state.controlScheme.controlPiece(e, p)),
        ),
      )

enum GameState:
  case Running, Crashed

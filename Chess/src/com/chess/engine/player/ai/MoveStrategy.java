package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
/**
 * Interface with execution method for MiniMax class
 * */
public interface MoveStrategy {
    Move execute(Board board);
}

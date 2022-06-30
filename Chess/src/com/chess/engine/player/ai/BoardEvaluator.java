package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
/**
 * Interface with evaluation method for StandardBoardEvaluator class
 * */
public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}

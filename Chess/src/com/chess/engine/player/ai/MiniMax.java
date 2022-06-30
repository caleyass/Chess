package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.BoardEvaluator;
import com.chess.engine.player.ai.MoveStrategy;
import com.chess.engine.player.ai.StandardBoardEvaluator;

import java.util.concurrent.atomic.AtomicLong;

import static com.chess.engine.board.Move.*;

public final class MiniMax implements MoveStrategy {

    private final BoardEvaluator evaluator;
    private final int searchDepth;

    public MiniMax(final int searchDepth) {
        this.evaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }


    /**
     * @param board board
     * @return best move computed by minimax algorithm
     */
    public Move execute(final Board board) {
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINKING with depth = " +this.searchDepth);
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth - 1) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth - 1);
                System.out.println("\t" + toString() + " analyzing move (numMoves) " + move +
                        " scores " + currentValue);
                if (board.currentPlayer().getAlliance().isWhite() &&
                        currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() &&
                        currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            } else {
                System.out.println("\t" + toString() + " can't execute move  +numMoves+ ) " + move);
            }

        }

        return bestMove;
    }

    /**
     * @param board board
     * @param depth depth of thinking
     * @return lowest seen value
     */
    private int min(final Board board,
                    final int depth) {
        if(depth == 0) {
            return this.evaluator.evaluate(board, depth);
        }
        if(isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }
    /**
     * @param board board
     * @param depth depth of thinking
     * @return highest seen value
     */
    private int max(final Board board,
                    final int depth) {
        if(depth == 0) {
            return this.evaluator.evaluate(board, depth);
        }
        if(isEndGameScenario(board)) {
            return this.evaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    /**
     * @param board board
     * @return if it is EndGameScenario
     */
    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
    }


}
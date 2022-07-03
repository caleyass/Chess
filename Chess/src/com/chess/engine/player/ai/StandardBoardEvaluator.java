package com.chess.engine.player.ai;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

/**
 * Contains special bonuses for AI
 */
public final class StandardBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board,final int depth) {
        return scorePlayer(board, board.getWhitePlayer(), depth)- scorePlayer(board, board.getBlackPlayer(), depth);
    }

    /**
     * @param board board
     * @param player player
     * @param depth depth
     * @return score of player
     */
    private int scorePlayer(final Board board,final Player player,final int depth) {
        return pieceValue(player)+mobility(player)+check(player) + checkMate(player, depth)+castled(player)+forwardBonus(player);
    }

    /**
     * @param player player
     * @return CASTLE_BONUS
     */
    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    /**
     * @param player player
     * @param depth depth
     * @return CHECKMATE_BONUS
     */
    private static int checkMate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * (depthBonus(depth)) : 0;
    }

    /**
     * @param depth depth
     * @return DEPTH_BONUS * depth
     */
    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    /**
     * @param player player
     * @return ability to make move
     */
    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    /**
     * @param player  player
     * @return CHECK_BONUS
     */
    private static int check(final Player player){
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    /**
     * @param player player
     * @return pieceValueScore
     */
    private int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for(final Piece piece: player.getActivePieces()){
            pieceValueScore+=piece.getPieceValue();
        }
        return pieceValueScore;
    }

    private int forwardBonus(final Player player) {
        int pieceValueScore = 0;
        if(player.getAlliance()== Alliance.WHITE) {
            for (final Piece piece : player.getActivePieces()) {
                pieceValueScore += 63 - piece.getPiecePosition();
            }
        }
        else{
            for (final Piece piece : player.getActivePieces()) {
                pieceValueScore += piece.getPiecePosition();
            }
        }
        return pieceValueScore;
    }
}

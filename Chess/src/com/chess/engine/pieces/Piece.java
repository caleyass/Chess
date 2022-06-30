package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    /**
     * @param piecePosition piecePosition
     * @param pieceAlliance pieceAlliance
     * @param pieceType pieceType
     * @param isFirstMove if it is a FirstMove
     */
    Piece(final int piecePosition, final Alliance pieceAlliance, final PieceType pieceType, final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        //TODO more work here
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() &&
                pieceAlliance == otherPiece.getPieceAlliance() &&
                pieceType == otherPiece.getPieceType();
    }

    /**
     * @return hash code
     */
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public int hashCode() {return this.cachedHashCode;}

    public Alliance getPieceAlliance(){return this.pieceAlliance;}

    public boolean isFirstMove(){return this.isFirstMove;}

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public Integer getPiecePosition() {return this.piecePosition;}

    public PieceType getPieceType() {return this.pieceType;}

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    public Alliance getPieceAllegiance() {
        return this.pieceAlliance;
    }


    /**
     * methods that define a type of pieces
     */
    public enum PieceType{
        PAWN(100, "P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

        },
        KNIGHT(300, "N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

        },
        BISHOP(300, "B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

        },
        ROOK(500, "R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

        },
        QUEEN(900, "Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

        },
        KING(10000, "K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

        };

        private String pieceName;
        private int pieceValue;

        PieceType(final int pieceValue, final String pieceName){
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString(){
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();

        public int getPieceValue() {
            return this.pieceValue;
        }
    }
}

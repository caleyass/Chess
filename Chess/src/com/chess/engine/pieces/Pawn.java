package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{


    private static final int[] CANDIDATE_MOVE_COORDINATE = { 8, 16, 7, 9 };

    /**
     * @param pieceAlliance pieceAlliance
     * @param piecePosition piecePosition
     */
    public Pawn( final Alliance pieceAlliance, final int piecePosition) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, true);
    }
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }
    public Piece getPromotionPiece(){
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
            int candidateDestinationCoordinate = this.piecePosition +(currentCandidateOffset * this.pieceAlliance.getDirection());

            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }

            if(currentCandidateOffset==8&&!board.getTile(candidateDestinationCoordinate).isTileOccupied()){

                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                }
                else{
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }

            }
            else if(currentCandidateOffset ==16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack())
                    || ((BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite())))){
                final int behindCandidateDestinationCoordinate = this.piecePosition +(8 * this.pieceAlliance.getDirection());
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                        && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }

            }
            else if(currentCandidateOffset == 7
                &&!((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())
                    || (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCoordinate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance!=pieceOnCoordinate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCoordinate)));
                        }
                        else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCoordinate));
                        }
                    }
                }else if(board.getEnPassantPawn()!=null){
                    if(board.getEnPassantPawn().getPiecePosition()==(this.piecePosition+(this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
            else if(currentCandidateOffset == 9
                    &&!((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())
                    || (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCoordinate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance!=pieceOnCoordinate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCoordinate)));
                        }
                        else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCoordinate));
                        }
                    }
                }
                else if(board.getEnPassantPawn()!=null){
                    if(board.getEnPassantPawn().getPiecePosition()==(this.piecePosition-(this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }

            }
        }

        return ImmutableList.copyOf(legalMoves);
    }


    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}

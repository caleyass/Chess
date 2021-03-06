package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

/**
 * Move of a piece
 */
public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public Board getBoard(){
        return this.board;
    }
    public static final Move NULL_MOVE = new NullMove();

    /**
     * @param board board
     * @param movedPiece movedPiece
     * @param destinationCoordinate destinationCoordinate
     */
      private Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
      }

    /**
     * @param board board
     * @param destinationCoordinate destinationCoordinate
     */
        private Move(final Board board, final int destinationCoordinate){
            this.board = board;
            this.movedPiece = null;
            this.destinationCoordinate = destinationCoordinate;
            this.isFirstMove = false;
        }

      public int getCurrentCoordinate(){
          return this.getMovedPiece().getPiecePosition();
      }

    public Integer getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    /**
     * @return edited board
     */
    public Board execute() {
        final Builder builder = new Builder();
        for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
            //TODO hashcode and equals for pieces
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        //move the moved piece
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    /**
     * @param other other object
     * @return if object equals
     */
    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate()
                && getDestinationCoordinate() == otherMove.getDestinationCoordinate()
                && getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public boolean isAttack(){
          return false;
    }

    public boolean isCastlingMove(){
          return false;
    }

    public Piece getAttackedPiece(){
          return null;
    }

    /**
     * Major attack move
     */
    public static class MajorAttackMove extends AttackMove {
        /**
         * @param board  board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param attackedPiece attackedPiece
         */
        public MajorAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Major move
     */
    public static final class MajorMove extends Move{
        /**
         * @param board  board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         */
         public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Attack move
     */
    public static class AttackMove extends Move{
         final Piece attackedPiece;

        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param attackedPiece attackedPiece
         */
        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(this == other) return true;
            if(!(other instanceof AttackMove)) return false;
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }


    /**
     * Pawn move
     */
    public static final class PawnMove extends Move{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         */
        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnMove && super.equals(other);
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Pawn attack move
     */
    public static class PawnAttackMove extends AttackMove{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param attackedPiece attackedPiece
         */
        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Pawn en passant attack move
     */
    public static final class PawnEnPassantAttackMove extends PawnAttackMove{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param attackedPiece attackedPiece
         */
        public PawnEnPassantAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();


        }
    }

    /**
     * Pawn promotion to queen
     */
    public static class PawnPromotion extends Move{

          final Move decoratedMove;
          final Pawn promotedPawn;

        /**
         * @param decoratedMove move of pawn
         */
          public PawnPromotion(final Move decoratedMove){
              super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
              this.decoratedMove = decoratedMove;
              this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
          }

          @Override
          public int hashCode(){
              return decoratedMove.hashCode()+(31*promotedPawn.hashCode());
          }
          @Override
          public boolean equals(final Object other){
              return this == other || other instanceof PawnPromotion && (super.equals(other));
          }
          @Override
        public Board execute(){

              final Board pawnMovedBoard = this.decoratedMove.execute();
              final Board.Builder builder = new Builder();
              for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                  if(!this.promotedPawn.equals(piece)){
                      builder.setPiece(piece);
                  }
              }
              for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                  builder.setPiece(piece);
              }
              builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
              builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
              return builder.build();
          }

          @Override
        public boolean isAttack(){
              return this.decoratedMove.isAttack();
          }

          @Override
        public Piece getAttackedPiece(){
              return this.decoratedMove.getAttackedPiece();
          }

          @Override
        public String toString(){
              return "";
          }
    }

    /**
     * Pawn long move
     */
    public static final class PawnJump extends Move{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         */
        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Castle move
     */
    static class CastleMove extends Move{
          protected final Rook castleRook;
          protected final int castleRookStart;
          protected final int castleRookDestination;

        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param castleRook castleRook
         * @param castleRookStart castleRookStart
         * @param castleRookDestination castleRookDestination
         */
        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !(this.castleRook.equals(piece))){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            //todo look into the first move
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }

    /**
     * King side castle move with rook
     */
    public static final class KingSideCastleMove extends CastleMove{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param castleRook castleRook
         * @param castleRookStart castleRookStart
         * @param castleRookDestination castleRookDestination
         */
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }

        public String toString(){
            return "0-0";
        }
    }

    /**
     * Queen side castle move with rook
     */
    public static final class QueenSideCastleMove extends CastleMove{
        /**
         * @param board board
         * @param movedPiece movedPiece
         * @param destinationCoordinate destinationCoordinate
         * @param castleRook castleRook
         * @param castleRookStart castleRookStart
         * @param castleRookDestination castleRookDestination
         */
        public QueenSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueenSideCastleMove)) {
                return false;
            }
            final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
            return super.equals(otherQueenSideCastleMove) && this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
        }

        public String toString(){
            return "0-0-0";
        }
    }

    /**
     * Null move
     */
    public static final class NullMove extends Move{
        public NullMove() {
            super(null,  -1);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute the null move");
        }

        //in order not to get nullPointerException
        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    /**
     * Finds a move by coordinates
     */
    public static class MoveFactory {

        private static final Move NULL_MOVE = new NullMove();

        private MoveFactory() {
            throw new RuntimeException("Not instantiatable!");
        }

        public static Move getNullMove() {
            return NULL_MOVE;
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move move(final Board board,
                                final int currentCoordinate,
                                final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

}

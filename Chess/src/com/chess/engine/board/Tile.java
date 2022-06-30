package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;


import java.util.HashMap;
import java.util.Map;

/**
 * Tole on the board
 */
public abstract class Tile {
    protected final int tileCoordinate;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPosibleEmptyTiles();

    /**
     * @return emptyTileMap
     */
    private static Map<Integer, EmptyTile> createAllPosibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0; i < BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return ImmutableMap.copyOf(emptyTileMap);
    }

    /**
     * @param tileCoordinate  tileCoordinate
     * @param piece piece
     * @return occupied or empty tile
     */
    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece!= null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    /**
     * @param tileCoordinate tileCoordinate
     */
    private Tile(final int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate(){
        return this.tileCoordinate;
    }

    /**
     * Empty tile
     */
    public static final class EmptyTile extends Tile{
        /**
         * @param coordinate coordinate
         */
        public EmptyTile(int coordinate){
            super(coordinate);
        }

        @Override
        public String toString() {
            return " - ";
        }

        @Override
        public boolean isTileOccupied(){
            return false;
        }

        @Override
        public Piece getPiece(){
            return null;
        }
    }

    /**
     * Occupied tile
     */
    public static final class OccupiedTile extends Tile{
        private final Piece pieceOnTile;

        /**
         * @param coordinate coordinate
         * @param pieceOnTile pieceOnTile
         */
        public OccupiedTile(int coordinate, final Piece pieceOnTile){
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isWhite() ?
                    getPiece().toString().substring(0, 1):
                    getPiece().toString().substring(0,1).toLowerCase();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }
    }
}

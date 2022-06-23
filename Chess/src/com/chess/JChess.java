package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

public class JChess {
    private static Board board;
    public static void main(String[] args) {
        /*
        board = Board.createStandardBoard();
        System.out.println(board);

         */
        Table.get().show();
    }
}

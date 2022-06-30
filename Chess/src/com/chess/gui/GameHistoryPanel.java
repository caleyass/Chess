package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.gui.Table.MoveLog;

class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 40);

    /**
     * create a base of the game history panel
     */
    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    /**
     * @param board board
     * @param moveHistory moveHistory
     */
    void redo(final Board board,
              final MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (final Move move : moveHistory.getMoves()) {
            final String moveText;
            if(move!=null) {
                moveText = move.toString();
                if(moveHistory.getMoves().size() >= 4 && !checkOnePieceRepeat(moveHistory.getMoves()) ) {
                    if (move.getMovedPiece().getPieceAlliance().isWhite()) {
                        this.model.setValueAt(moveText, currentRow, 0);
                    } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                        this.model.setValueAt(moveText, currentRow, 1);
                        currentRow++;
                    }
                }
                else if(moveHistory.getMoves().size() < 4){
                    if (move.getMovedPiece().getPieceAlliance().isWhite()) {
                        this.model.setValueAt(moveText, currentRow, 0);
                    } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                        this.model.setValueAt(moveText, currentRow, 1);
                        currentRow++;
                    }
                }
            }
        }

        if(moveHistory.getMoves().size() > 0 ) {
            if(moveHistory.getMoves().size() >= 4 && !checkOnePieceRepeat(moveHistory.getMoves()) ){
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();
            if (lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }
            else if (lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
            if(moveHistory.getMoves().size() >= 10) {
                if (checkRepeat(moveHistory.getMoves())) {
                    String[] buttons = {"Нова гра", "Вийти"};
                    String text = "";
                    if (Board.calculateActivePieces(board.getGameBoard(), Alliance.WHITE).size() > Board.calculateActivePieces(board.getGameBoard(), Alliance.BLACK).size()) {
                        text = "Білі перемогли!";
                    } else if (Board.calculateActivePieces(board.getGameBoard(), Alliance.WHITE).size() < Board.calculateActivePieces(board.getGameBoard(), Alliance.BLACK).size()) {
                        text = "Чорні перемогли!";
                    } else {
                        text = "Нічия!";
                    }
                    int returnValue = JOptionPane.showOptionDialog(null, text, "Перемога",
                            JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION, null, buttons, buttons[0]);
                    if (returnValue == 0) {
                        Table.get().restart();
                    } else {
                        System.exit(0);
                    }
                }
            }
            }
            else if(moveHistory.getMoves().size() < 4){
                final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
                final String moveText = lastMove.toString();
                if (lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
                }
                else if (lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                    this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
                }
            }

        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    /**
     * checks if pieces can't find new move. if so, @return true (game over).
     * wins that player who has more pieces
     * */
    private boolean checkRepeat(List<Move> moves) {
        Move lastMove = moves.get(moves.size()-1);
        Move lastMove_1 = moves.get(moves.size()-2); // pre last move
        Move lastMove_2 = moves.get(moves.size()-3); // pre pre last move
        Move lastMove_3 = moves.get(moves.size()-4);// pre pre pre last move

        //if moves repeat - return true
        return lastMove.toString().equals(moves.get(moves.size()-5).toString())
                && lastMove_1.toString().equals(moves.get(moves.size()-6).toString())
                && lastMove_2.toString().equals(moves.get(moves.size()-7).toString())
                && lastMove_3.toString().equals(moves.get(moves.size()-8).toString());
    }
    /**check if one piece is repeated in a row
     * @param moves - whole list of moves
     * @return true if so*/
    private boolean checkOnePieceRepeat(List<Move> moves){
        Move lastMove = moves.get(moves.size()-1);
        Move lastMove_1 = moves.get(moves.size()-2); // pre last move
        Move lastMove_2 = moves.get(moves.size()-3); // pre pre last move
        Move lastMove_3 = moves.get(moves.size()-4);// pre pre pre last move
        if(lastMove == null || lastMove_1 == null
            || (lastMove.toString().equals(lastMove_2.toString()))
            || (lastMove_1.toString().equals(lastMove_3.toString())))
            return true;
        return false;
    }

    /**
     * @param board board
     * @return whether a king is in check mate or in check or not
     */
    private static String calculateCheckAndCheckMateHash(final Board board) {
        if(board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if(board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class Row {

        private String whiteMove;
        private String blackMove;

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(final String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(final String move) {
            this.blackMove = move;
        }

    }

    private static class DataModel extends DefaultTableModel {

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        /**
         * sets row count to 0
         */
        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            final Row currentRow = this.values.get(row);
            if(col == 0) {
                return currentRow.getWhiteMove();
            } else if (col == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object aValue,
                               final int row,
                               final int col) {
            final Row currentRow;
            if(this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if(col == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else  if(col == 1) {
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row, col);
            }
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int col) {
            return NAMES[col];
        }
    }
}
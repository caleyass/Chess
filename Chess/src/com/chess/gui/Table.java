package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.*;

/**
 * Main GUI of chess table
 */
public class Table extends Observable {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private static int returnValue = -1;

    private Move computerMove;

    private boolean highlighLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(1030,1030);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(800, 800);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(100, 100);
    private static String defaultPieceImagesPath = "art/simple/";

    private final Color lightTileColor = Color.decode("#FCEB72");
    private final Color darkTileColor = Color.decode("#86A1FC");

    private static Table INSTANCE = new Table();


    /**
     * potential players: human or computer
     */
    enum PlayerType{
        HUMAN,
        COMPUTER
    }

    /**
     * base for table
     */
    private Table() {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlighLegalMoves = false;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    /**
     * @return restart table
     */
    public Table restart(){
        this.gameFrame.dispose();
        INSTANCE = new Table();
        returnValue=-1;
        return INSTANCE;
    }
    public static Table get(){
        return INSTANCE;
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    /**
     * draws start board
     */
    public void show() {
        invokeLater(new Runnable() {
            public void run() {
                Table.get().getMoveLog().clear();
                Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
        });
    }

    /**
     * @return menu of settings
     */
    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("????????????????????????");
        final JMenuItem setupGameMenuItem = new JMenuItem("?????????????????????? ??????????????");

        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionsMenu.add(setupGameMenuItem);

        return optionsMenu;
    }

    /**
     * @param gameSetup gameSetup
     */
    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);

    }

    /**
     * decide when game is over
     */
    private static class TableGameAIWatcher implements Observer{

        @Override
        public void update(Observable o, Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer())
                    && !Table.get().getGameBoard().currentPlayer().isInCheckMate()
                    && !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                //create an AI thread
                //execute AI work
                final AIThinkTank thinkTank = new AIThinkTank(Table.get().getGameSetup().getSearchDepth());
                thinkTank.execute();
            }

            if((Table.get().getGameBoard().currentPlayer().isInCheckMate() || Table.get().getGameBoard().currentPlayer().isInStaleMate()) && returnValue==-1){
                String text = "";
                switch(Table.get().getGameBoard().currentPlayer().getAlliance()){
                    case WHITE:
                        text = "??????????";
                        break;
                    case BLACK:
                        text = "????????";
                        break;
                }
                text += " ??????????????????!";
                showButton(text);
            }

        }


        /**
         * show buttons and prints who won
         * @param text who won
         */
        private void showButton(String text) {
            String[] buttons = { "???????? ??????", "??????????" };
            returnValue = JOptionPane.showOptionDialog(null, text, "????????????????!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION, null, buttons, buttons[0]);
            System.out.println(returnValue);
            if(returnValue == 0){
                Table.get().restart();
            }
            else{
                System.exit(0);
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move){
        this.computerMove = move;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    /**
     * @param playerType playerType
     */
    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    /**
     * helps to adapt AI
     */
    private static class AIThinkTank extends SwingWorker<Move, String>{

        private int searchDepth = 4;

        /**
         * @param searchDepth depth of ai thinking
         */
        private AIThinkTank(int searchDepth){
            this.searchDepth = searchDepth;
        }


        /**
         * @return best move computed by AI
         * @throws Exception
         */
        @Override
        protected Move doInBackground() throws Exception{

            final MoveStrategy miniMax = new MiniMax(searchDepth);

            final Move bestMove = miniMax.execute(Table.get().getGameBoard());

            return bestMove;
        }

        @Override
        protected void done(){
            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    /**
     * @return tableMenuBar
     */
    private JMenuBar populateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();

        //?????????? ???????????? ???? ?????????????????? ????????
        tableMenuBar.add(createOptionsMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    /**
     * Contains list of all moves
     */
    public static class MoveLog{
        private final List<Move> moves;

        MoveLog (){
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves(){
            return this.moves;
        }
        public void addMove(final Move move){
            this.moves.add(move);
        }
        public int size(){
            return this.moves.size();
        }
        public void clear(){
            this.moves.clear();
        }
        public Move removeMove(int index){
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    /**
     * Creates tiles on board
     */
    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i =0 ; i < BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    /**
     * Logic of tile
     */
    private class TilePanel extends JPanel{
        private final int tileID;
        TilePanel(final BoardPanel boardPanel, final int tileID){
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            this.assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;

                    }
                    else if(isLeftMouseButton(e)){
                        if(sourceTile == null) {
                            //first click
                            sourceTile = chessBoard.getTile(tileID);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        }
                        else {
                            //second click
                            destinationTile = chessBoard.getTile(tileID);
                            final Move move = Move.MoveFactory.move(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                                if((chessBoard.currentPlayer().isInCheckMate() || chessBoard.currentPlayer().isInStaleMate()) && returnValue==-1){
                                    //JOptionPane.showMessageDialog(null, chessBoard.currentPlayer().getAlliance()+" is in checkmate!");
                                    String text = "";
                                    switch(chessBoard.currentPlayer().getAlliance()){
                                        case WHITE:
                                            text = "?????????? ??????????????????!";
                                            break;
                                        case BLACK:
                                            text = "???????? ??????????????????!";
                                            break;
                                    }

                                    String[] buttons = { "???????? ??????", "??????????" };
                                    returnValue = JOptionPane.showOptionDialog(null, text, "????????????????!",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION, createFileMenu().getDisabledSelectedIcon(), buttons, buttons[0]);
                                    System.out.println(returnValue);
                                    if(returnValue == 0){
                                        restart();
                                    }
                                    else{
                                        System.exit(0);
                                    }
                                }
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);

                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }

                                boardPanel.drawBoard(chessBoard);

                            }
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            validate();
        }

        /**
         * @param board board
         * draws a tile on board
         */
        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        /**
         * @param board board
         */
        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileID).isTileOccupied()){
                try {
                    final BufferedImage image
                            = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileID).getPiece().getPieceAlliance().toString().substring(0,1)
                            + board.getTile(this.tileID).getPiece().toString() + ".gif"));
                    this.add(new JLabel(new ImageIcon(image.getScaledInstance(image.getWidth()*2, image.getHeight()*2, Image.SCALE_SMOOTH))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * @param board board
         */
        private void highlightLegals(final Board board){
                for(final Move move: pieceLegalMoves(board)){

                    if(move.getDestinationCoordinate() == this.tileID){

                        try{
                            if(!allowedMove(move)) {
                                add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH))));
                            }
                            else{
                                add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/red_dot.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH))));
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

        }

        /**
         * @param move move
         * @return if current player is in check
         */
        private boolean allowedMove(final Move move){
            if (!Table.get().getGameBoard().currentPlayer().getLegalMoves().contains(move)) {
                return false;
            }
            final Board transitionedBoard = move.execute();

            return transitionedBoard.currentPlayer().getOpponent().isInCheck();

        }

        /**
         * @param board board
         * @return collection of legal moves
         */
        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();

        }

        /**
         * set colors of tiles
         */
        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileID]
                    || BoardUtils.SIXTH_RANK[this.tileID]
                    || BoardUtils.FOURTH_RANK[this.tileID]
                    || BoardUtils.SECOND_RANK[this.tileID]){
                this.setBackground(this.tileID % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtils.SEVENTH_RANK[this.tileID]
                    || BoardUtils.FIFTH_RANK[this.tileID]
                    || BoardUtils.THIRD_RANK[this.tileID]
                    || BoardUtils.FIRST_RANK[this.tileID]){
                this.setBackground(this.tileID % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }

    /**
     * @return menu that can exit
     */
    //?????????????? ????????????, ?????? ??????????????????????????
    private JMenu createFileMenu(){
        final JMenu fileMenu = new JMenu("??????????");
        fileMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JMenuItem exitMenuItem = new JMenuItem("??????????");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    /**
     * @return menu that can swap pieces
     */
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("??????????");
        final JMenuItem flipBoardMenuItem = new JMenuItem("?????????????????????? ??????????");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        return preferencesMenu;
    }

    /**
     * Direction of board : NORMAL, FLIPPED
     */
    public enum BoardDirection{
        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite(){
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }
}

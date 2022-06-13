package com.chess.gui;

import com.chess.engine.board.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    public Table() {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setVisible(true);
        this.gameFrame.setLayout(new BorderLayout());

        //board panel
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        //Menu Bar (верхнє меню)
        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

    }

    private JMenuBar populateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();

        //додає ячейку до верхнього меню
        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

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
    }

    private class TilePanel extends JPanel{
        private final int tileID;
        TilePanel(final BoardPanel boardPanel, final int tileID){
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }

        private void assignTileColor() {
        }
    }

    //Створює ячейку, яка розкривається
    private JMenu createFileMenu(){
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN file");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open pgn file");
            }
        });
        fileMenu.add(openPGN);
        return fileMenu;
    }
}

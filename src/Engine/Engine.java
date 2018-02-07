package Engine;

import GUI.ChessCanvas;
import Players.AI.RandomPlayer;
import Players.HumanPlayer;
import Players.Move;
import Players.Player;
import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class Engine implements Runnable {

    public static final int CELL_AMOUNT = 8;

    private JFrame frame;
    private ChessCanvas canvas;

    private int standardCellWidth = 100;

    private Handler handler;

    private Player whitePlayer;
    private Player blackPlayer;
    private Move lastMove; //used for en-passent

    private Thread t;
    private volatile boolean hasToStop = false;

    public Engine() {

        handler = new Handler(this);

        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
           @Override
           public void windowClosing(WindowEvent e) {
               exitProcedure();
           }
        });

        canvas = new ChessCanvas(handler);
        canvas.setCellWidth(standardCellWidth);
        canvas.setDoubleBuffered(true);
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                int x = e.getX();
                int y = e.getY();
                canvas.setMousePointer(new ChessPosition((double)x,  (double)y, canvas));
                canvas.setMousePosition(new Point(e.getX(), e.getY()));
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (handler.isWhiteToMove()) {
                    whitePlayer.mousePressed(e);
                } else {
                    blackPlayer.mousePressed(e);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (lastMove != null)
                        lastMove.undo();
                }
            }
        });

        JPanel chessPanel = new JPanel();
        chessPanel.add(canvas);
        chessPanel.setBackground(Color.BLACK);

        chessPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Dimension d = e.getComponent().getSize();
                canvas.setPreferredSize(d);
                int min = (int)Math.min(d.getHeight(), d.getWidth());
                int newCellWidth = min / 8;
                canvas.setCellWidth(newCellWidth);
                int x = (int)(d.getWidth() / 2)-(newCellWidth * 4);
                int y = (int)(d.getHeight() / 2)-(newCellWidth * 4);
                canvas.setLocation(new Point(x, y));
                canvas.repaint();
                //update the piece scale
                handler.getPieces().forEach(p -> p.setCellWidth(newCellWidth));
            }
        });

        frame.setContentPane(chessPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        initializeGame();
    }

    /**
     * To be called when the window closes. Cleans up threads and exits the program.
     */
    private void exitProcedure() {
        stop();
        System.exit(0);
    }

    /**
     * Start the game loop
     */
    private void start() {
        t = new Thread(this);
        t.start();
    }

    /**
     * Stop the game loop
     */
    private void stop() {
        hasToStop = true;
        try {
            t.join();
            playerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeGame() {
        whitePlayer = new HumanPlayer(ChessColor.White, this);
        blackPlayer = new HumanPlayer(ChessColor.Black, this);
        handler.addPiece(new Rook(new ChessPosition(1,1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Rook(new ChessPosition(8, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Knight(new ChessPosition(2, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Knight(new ChessPosition(7, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Bishop(new ChessPosition(3, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Bishop(new ChessPosition(6, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new King(new ChessPosition(5, 1, canvas), ChessColor.White, standardCellWidth, this));
        handler.addPiece(new Queen(new ChessPosition(4, 1, canvas), ChessColor.White, standardCellWidth, this));
        for (int i = 1; i <= 8; i++) {
            handler.addPiece(new Pawn(new ChessPosition(i, 2, canvas), ChessColor.White, standardCellWidth, this));
        }
        handler.addPiece(new Rook(new ChessPosition(1,8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Rook(new ChessPosition(8, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Knight(new ChessPosition(2, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Knight(new ChessPosition(7, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Bishop(new ChessPosition(3, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Bishop(new ChessPosition(6, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new King(new ChessPosition(5, 8, canvas), ChessColor.Black, standardCellWidth, this));
        handler.addPiece(new Queen(new ChessPosition(4, 8, canvas), ChessColor.Black, standardCellWidth, this));
        for (int i = 1; i <= 8; i++) {
            handler.addPiece(new Pawn(new ChessPosition(i, 7, canvas), ChessColor.Black, standardCellWidth, this));
        }
        handler.updateMoves();
        start();
    }

    /**
     * The game loop
     */
    private Thread playerThread; //The thread that executes the players
    private boolean playerThreadRunning = false;
    @Override
    public void run() {
        while(true) {
            if (hasToStop) break; //It should stop when it needs to.
            //do a repaint
            canvas.requestBoardRepaint();
            canvas.repaint();
            if (!playerThreadRunning) { //check if the player thread is already running
                if (handler.isWhiteToMove()) {
                    playerThread = new Thread(whitePlayer); //if not, make a new thread and start it
                } else {
                    playerThread = new Thread(blackPlayer);
                }
                playerThread.start();
                playerThreadRunning = true;
            }
            Move m;
            if (handler.isWhiteToMove()) {
                m = whitePlayer.fetchMove(); //try to fetch the move white makes
            } else {
                m = blackPlayer.fetchMove();
            }
            if (m != null) { //if there is a move
                m.execute(); //execute it
                handler.updateMoves(); //update the possible moves for the new state
                canvas.requestBoardRepaint(); //a piece has moved, so the pieces should be redrawn
                try { //try joining the player thread, as it has executed his job
                    playerThread.join();
                    playerThreadRunning = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (handler.getWhiteKing().isMated()) {
                JOptionPane.showMessageDialog(getFrame(), "White lost");
                stop();
            }
            if (handler.getBlackKing().isMated()) {
                JOptionPane.showMessageDialog(getFrame(), "Black lost");
                stop();
            }
        }
    }

    public ChessCanvas getCanvas() {
        return this.canvas;
    }

    public Frame getFrame() {return this.frame;}

    public Handler getHandler() {
        return this.handler;
    }

    public void setLastMove(Move m) {
        this.lastMove = m;
    }

    public Move getLastMove() {
        return this.lastMove;
    }

    /**
     * Returns whether or  not a human is playing currently
     */
    public boolean humanTurn() {
        if (handler.isWhiteToMove() && whitePlayer instanceof HumanPlayer) {
            return true;
        }
        if (!handler.isWhiteToMove() && blackPlayer instanceof HumanPlayer) {
            return true;
        }
        return false;
    }
}

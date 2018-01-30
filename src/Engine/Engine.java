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

    JFrame frame;
    ChessCanvas canvas;
    JPanel chessPanel;

    private int standardCellWidth = 100;

    Handler handler;
    boolean whiteTurn = true;

    Player whitePlayer;
    Player blackPlayer;
    Move lastMove; //used for en-passent
    //to be updated after every move. To prevent continuous fetching of moves.
    Set<Move> whitePlayerMoves; //every move of the white player
    Set<Move> blackPlayerMoves;
    Set<Move> whitePlayerMovesWithCheck; //every valid move of the white player (that will not result in check)
    Set<Move> blackPlayerMovesWithCheck;

    Thread t;
    volatile boolean hasToStop = false;

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
                if (whiteTurn) {
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

        chessPanel = new JPanel();
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
    public void exitProcedure() {
        stop();
        System.exit(0);
    }

    /**
     * Start the game loop
     */
    public void start() {
        t = new Thread(this);
        t.start();
    }

    /**
     * Stop the game loop
     */
    public void stop() {
        hasToStop = true;
        try {
            t.join();
            playerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initializeGame() {
        whitePlayer = new HumanPlayer(ChessColor.White, this);
        blackPlayer = new RandomPlayer(ChessColor.Black, this);
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
        updateMoves();
        start();
    }

    /**
     * The game loop
     */
    Thread playerThread; //The thread that executes the players
    boolean playerThreadRunning = false;
    @Override
    public void run() {
        while(true) {
            if (hasToStop) break; //It should stop when it needs to.
            //do a repaint
            canvas.repaint();
            if (whiteTurn) { //when it is white's turn
                if (!playerThreadRunning) { //check if the white thread is already running
                    playerThread = new Thread(whitePlayer); //if not, make a new thread and start it
                    playerThread.start();
                    playerThreadRunning = true;
                }
                Move m = whitePlayer.fetchMove(); //try to fetch the move white makes
                if (m != null) { //if there is a move
                    m.execute(); //execute it
                    updateMoves(); //update the possible moves for the new state
                    try { //try joining the player thread, as it has executed his job
                        playerThread.join();
                        playerThreadRunning = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {    //blackTurn
                if (!playerThreadRunning) {
                    playerThread = new Thread(blackPlayer);
                    playerThread.start();
                    playerThreadRunning = true;
                }
                Move m = blackPlayer.fetchMove();
                if (m != null) {
                    m.execute();
                    updateMoves();
                    try {
                        playerThread.join();
                        playerThreadRunning = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (handler.getWhiteKing().isMated()) {
                JOptionPane.showMessageDialog(getFrame(), "White lost");
            }
            if (handler.getBlackKing().isMated()) {
                JOptionPane.showMessageDialog(getFrame(), "Black lost");
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

    public void changeTurn() {
        whiteTurn = !whiteTurn;
    }

    /**
     * Returns whether or  not a human is playing currently
     * @return
     */
    public boolean humanTurn() {
        if (whiteTurn && whitePlayer instanceof HumanPlayer) {
            return true;
        }
        if (!whiteTurn && blackPlayer instanceof HumanPlayer) {
            return true;
        }
        return false;
    }

    /**
     * Updates the moves for every player. This happens whenever someone executes a move. To prevent continous calculation of the moves.
     */
    public void updateMoves() {
        whitePlayerMoves = new HashSet<>();
        blackPlayerMoves = new HashSet<>();
        whitePlayerMovesWithCheck = new HashSet<>();
        blackPlayerMovesWithCheck =  new HashSet<>();
        Set<Piece> whitePieces = handler.getPieces(ChessColor.White);
        for (Piece p : whitePieces) {
            whitePlayerMoves.addAll(p.getMoves());
            whitePlayerMovesWithCheck.addAll(p.getMovesWithCheck());
        }
        Set<Piece> blackPieces = handler.getPieces(ChessColor.Black);
        for (Piece p : blackPieces) {
            blackPlayerMoves.addAll(p.getMoves());
            blackPlayerMovesWithCheck.addAll((p.getMovesWithCheck()));
        }
    }

    public Set<Move> getMoves(ChessColor c) {
        if (c == ChessColor.Black) {
            return this.blackPlayerMoves;
        } else {
            return this.whitePlayerMoves;
        }
    }
    public Set<Move> getMovesWithCheck(ChessColor c) {
        if (c == ChessColor.Black) {
            return this.blackPlayerMovesWithCheck;
        } else {
            return this.whitePlayerMovesWithCheck;
        }
    }

    public Set<Move> getOppositeColorMoves(ChessColor c) {
        if (c == ChessColor.Black) {
            return whitePlayerMoves;
        } else {
            return blackPlayerMoves;
        }
    }
    public Set<Move> getOppositeColorMovesWithCheck(ChessColor c) {
        if (c == ChessColor.Black) {
            return whitePlayerMovesWithCheck;
        } else {
            return blackPlayerMovesWithCheck;
        }
    }
}

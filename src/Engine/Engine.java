package Engine;

import GUI.ChessCanvas;
import Players.AI.AlphaBetaPlayer;
import Players.AI.RandomPlayer;
import Players.HumanPlayer;
import Players.Move;
import Players.Player;
import pieces.*;

import javax.sql.rowset.serial.SerialRef;
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
        //backspace is cancel move
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    handler.undoLastMove();
                    canvas.requestBoardRepaint();
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
                canvas.requestBoardRepaint();
                canvas.repaint();
                //update the piece scale
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
        blackPlayer = new AlphaBetaPlayer(ChessColor.Black, this);
        canvas.requestBoardRepaint();//to start with a painted board.
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
                handler.execute(m, true); //execute it

                try { //try joining the player thread, as it has executed his job
                    playerThread.join();
                    playerThreadRunning = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (handler.whiteMated()) {
                    JOptionPane.showMessageDialog(getFrame(), "White lost");
                    return;
                }
                if (handler.blackMated()) {
                    JOptionPane.showMessageDialog(getFrame(), "Black lost");
                    return;
                }
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

    public boolean isAlive() {
        return t.isAlive();
    }

    public void join() {
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

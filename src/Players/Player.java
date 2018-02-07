package Players;

import pieces.ChessColor;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Set;

import Engine.*;

/**
 * A player, subclass of SwingWorker to let the gui not hang when calculating a move.
 */
public abstract class Player implements Runnable {

    protected ChessColor color;
    protected Move move;
    protected Engine e;
    protected Handler handler;

    public Player(ChessColor color, Engine e) {
        this.color = color;
        this.e = e;
        this.handler = e.getHandler();
    }

    public ChessColor getColor() {
        return this.color;
    }

    /**
     * Used to fetch a move. When the move is fetched, the move is deleted in the process.
     * @return
     */
    public Move fetchMove() {
        if (move == null) {
            return move;
        } else {
            Move answer = move;
            move = null;
            return answer;
        }
    }

    /**
     * To be overriden when a specific player type needs to check the mouse
     * @param e
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Returns a random valid move given the current situation in the handler
     * @return A random move
     */
    protected Move getRandomValidMove(Handler handler) {
        Random r = new Random();
        Move move = null;
        Set<Move> possibleMoves = handler.getMovesWithCheck(getColor());
        int size = possibleMoves.size();
        int number = r.nextInt(size);
        int i = 0;
        for (Move m : possibleMoves) {
            if (i == number) {
                move = m;
            }
            i++;
        }
        return move;
    }
}

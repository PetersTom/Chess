package Players;

import pieces.ChessColor;
import java.awt.event.MouseEvent;
import Engine.Engine;

/**
 * A player, subclass of SwingWorker to let the gui not hang when calculating a move.
 */
public abstract class Player implements Runnable {

    protected ChessColor color;
    protected Move move;
    protected Engine e;

    public Player(ChessColor color, Engine e) {
        this.color = color;
        this.e = e;
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
}

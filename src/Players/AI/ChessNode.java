package Players.AI;

import Engine.Handler;
import Players.Move;

/**
 * A class representing a node in the search tree for the alpha beta algorithm.
 * It contains the handler, which in turn holds all pieces. Adepting the pieces of the handler makes
 * this a different node. The get/setBestMove methods are intended to story/retrieve the best move
 * as it has been computed
 */
public class ChessNode {
    private final Handler handler;
    private Move move;
    public ChessNode(Handler h) {
        this.handler = h;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void setBestMove(Move m) {
        this.move = m;
    }
    public Move getBestMove() {
        return this.move;
    }
}

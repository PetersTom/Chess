package Players.AI;

import Engine.*;
import Players.Move;
import Players.Player;
import pieces.ChessColor;

import java.util.Random;
import java.util.Set;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class AlphaBetaPlayer extends Player {

    private int bestValue;
    private final int maxSearchDepth = 3; //the initial search depth
    private long startTime;

    public AlphaBetaPlayer(ChessColor color, Engine e) {
        super(color, e);
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        Move bestMove = null;
        bestValue = 0;
        Handler originalHandler = handler.deepCopy();
        ChessNode node = new ChessNode(handler); //root of the search tree
        try {
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, 0, maxSearchDepth);
            //store the best move found uptill now
            bestMove = node.getBestMove();
            //print some results for debugging purposses
            System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
            );
        } catch (AITimeLimitExceededException e) { /* just here to catch the exception and to stop if needed */ }

        if (bestMove == null) { //no move found yet
            move = getRandomValidMove(handler); //set the move to be fetched to a random move
        } else {
            move = bestMove;    //set the move to be fetched to the best move uptill now
        }
    }

    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains Handler and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AITimeLimitExceededException
     **/
    private int alphaBeta(ChessNode node, int alpha, int beta, int depth, int maxSearchDepth)
            throws AITimeLimitExceededException {
        if (node.getHandler().isWhiteToMove()) {
           // return alphaBetaMax(node, alpha, beta, depth, maxSearchDepth);
        } else  {
            //return alphaBetaMin(node, alpha, beta, depth, maxSearchDepth);
        }
        return 0;
    }

}

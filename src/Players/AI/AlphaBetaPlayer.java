package Players.AI;

import Engine.*;
import Players.Move;
import Players.Player;
import pieces.ChessColor;
import pieces.Piece;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;
import java.util.Set;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class AlphaBetaPlayer extends Player {

    private int bestValue;
    private final int maxSearchDepth = 3; //the initial search depth
    private final long maxRunningTime = 10000; //10 seconds
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
        ChessNode node = new ChessNode(handler.deepCopy()); //root of the search tree
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
            return alphaBetaMax(node, alpha, beta, depth, maxSearchDepth);
        } else  {
            return alphaBetaMin(node, alpha, beta, depth, maxSearchDepth);
        }
    }

    private int alphaBetaMin(ChessNode node, int alpha, int beta, int depth, int maxSearchDepth)
            throws AITimeLimitExceededException {
        //Stop if maximum running time is exceeded.
        if (System.currentTimeMillis()- startTime > maxRunningTime) {
            throw new AITimeLimitExceededException();
        }

        Handler handler = node.getHandler();

        //base cases
        if (handler.whiteMated()) {
            return MIN_VALUE;
        }
        if (handler.blackMated()) {
            return MAX_VALUE;
        }
        if (depth >= maxSearchDepth) {
            return evaluate(handler);
        }

        //The minimizing player is always the black player, so only the black player can play at this moment.
        Set<Move> moves = handler.getMovesWithCheck(ChessColor.Black);

        //while there are still moves to evaluate
        for (Move move : moves) {
            //get the state that corresponds to the move that we are going to evaluate
            move.execute(); //changes the handler
            ChessNode newNode = new ChessNode(handler.deepCopy());  //make a new node with a copy of the handler
            //check the child nodes and set the best move accordingly
            int recursiveCall;
            if (moves.size() == 1) {    //if there is only one move possible, do not count this to the recursion depth
                recursiveCall = alphaBetaMax(newNode, alpha, beta, depth, maxSearchDepth);
            } else {
                recursiveCall = alphaBetaMax(newNode, alpha, beta, depth + 1, maxSearchDepth);
            }
            //undo the move for the next one. Changes the handler.
            move.undo();

            //Checks if the value of the childnode is such that changes are necessary to alpha and beta
            if (recursiveCall < beta) {
                beta = recursiveCall;
                if (depth == 0) {
                    node.setBestMove(move);
                }
                if (beta <= alpha) {
                    return alpha;
                }
            }
        }
        return beta;
    }

    private int alphaBetaMax(ChessNode node, int alpha, int beta, int depth, int maxSearchDepth)
            throws AITimeLimitExceededException {
        //Stop if maximum running time is exceeded.
        if (System.currentTimeMillis()- startTime > maxRunningTime) {
            throw new AITimeLimitExceededException();
        }

        Handler handler = node.getHandler();

        //base cases
        if (handler.whiteMated()) {
            return MIN_VALUE;
        }
        if (handler.blackMated()) {
            return MAX_VALUE;
        }
        if (depth >= maxSearchDepth) {
            return evaluate(handler);
        }

        //get the possible moves, the maximizing player is always white
        Set<Move> moves = handler.getMovesWithCheck(ChessColor.White);

        //while there are still moves to evaluate
        for (Move move : moves) {
            //get the state that corresponds to the move that we are going to evaluate
            move.execute(); //changes the handler
            ChessNode newNode = new ChessNode(handler.deepCopy());
            //check the child nodes and set the best move accordingly
            int recursiveCall;
            if (moves.size() == 1) { //if there is only one possible move, do not count it to the recursion depth
                recursiveCall = alphaBetaMin(newNode, alpha, beta, depth, maxSearchDepth);
            } else {
                recursiveCall = alphaBetaMin(newNode, alpha, beta, depth + 1, maxSearchDepth);
            }
            //undo the move again to make sure the state is ready for the next one
            move.undo();

            //check if the value of the child node is such that changes are necessary to alpha or beta.
            if (recursiveCall > alpha) {
                alpha = recursiveCall;
                if (depth == 0) {
                    node.setBestMove(move);
                }
                if (beta <= alpha) {
                    return beta;
                }
            }
        }
        return alpha;
    }

    /**
     * A method that evaluates a given state
     * @param handler
     * @return
     */
    private int evaluate(Handler handler) {
        System.err.println("value: " + countPiecesValue(handler));
        return countPiecesValue(handler);
    }

    private int countPiecesValue(Handler handler) {
        Set<Piece> pieces = handler.getPieces(); //no concurrentmodification issues because this method returns a copy
        int totalValue = 0;
        for (Piece p : pieces) {
            totalValue += p.getPieceValue();
        }
        return totalValue;
    }

}

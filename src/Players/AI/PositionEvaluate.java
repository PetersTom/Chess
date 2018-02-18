package Players.AI;

import Engine.Handler;
import pieces.Piece;

/**
 * Class can not be instantiated. Only static methods are in here to evaluate a certain chessposition.
 */
public abstract class PositionEvaluate {

    /**
     * A method that evaluates a given state. Uses all possible evaluation methods
     */
    public static int evaluate(Handler handler) {
        return evaluate(handler, true);
    }

    /**
     * Evaluates the position with only the methods that are true as input.
     * Throws an illegalArgumentException when all arguments are false;
     */
    public static int evaluate(Handler handler, boolean countPieces) {
        int value = 0;
        if (countPieces) {
            value += countPiecesValue(handler);
        }
        return value;
    }

    private static int countPiecesValue(Handler handler) {
        Piece[][] pieces = handler.getPieces(); //no concurrentmodification issues because this method returns a copy
        int totalValue = 0;
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null) {
                    totalValue += p.getPieceValue();
                }
            }
        }
        return totalValue;
    }
}

package Players;

import pieces.*;
import Engine.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A class to execute a move. It also contains all information to undo a move.
 */
public class Move {
    ChessPosition end;    //new position
    Piece capturedPiece; //can be null
    ChessPosition capturedPiecePosition; //most of the time this will be the same as end, unless it is en-passent
    Engine e;

    private boolean[] castlingsPossible = {true, true, true, true};

    //values used for return
    ChessPosition start; //starting position of the piece. Used in en-passent as well
    Move previousLastMove;

    private boolean executed; //states whether or not this move has been executed or not

    public Move(ChessPosition start, ChessPosition end, Piece capture, ChessPosition capturedPiecePosition, Engine e, Move previousLastMove) {
        this.end = end;
        this.start = start;
        this.capturedPiece = capture;
        this.capturedPiecePosition = capturedPiecePosition;
        this.e = e;
        this.previousLastMove = previousLastMove;
    }

    public ChessPosition getEndPosition() {
        return end;
    }

    public ChessPosition getStartPosition() {
        return this.start;
    }

    public boolean isExecuted() {
        return this.executed;
    }

    public void setExecuted(boolean b) {
        this.executed = b;
    }

    public Piece getCapturedPiece() {
        return this.capturedPiece;
    }

    public ChessPosition getCapturedPiecePosition() {
        return capturedPiecePosition;
    }

    public Move getPreviousLastMove() {
        return this.previousLastMove;
    }

    @Override
    public String toString() {
        return this.start + " " + this.end;
    }

    /**
     * Sets the values of the castling options before this move.
     * The order is as follows: whiteshort, whitelong, blackshort, blacklong.
     */
    public void setCastlings(boolean[] b) {
        if (b.length != 4) throw new IllegalArgumentException("The length of the array is not correct.");
        for (int i = 0; i < 4; i++) {
            castlingsPossible[i] = b[i]; //castlingsPossible = b would result in only a pointer to the booleans. We want a copy.
        }

    }

    public boolean[] getCastlingsPossible() {
        return castlingsPossible;
    }


}

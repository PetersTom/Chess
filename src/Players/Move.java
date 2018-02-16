package Players;

import pieces.*;
import Engine.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A class to execute a move. It also contains all information to undo a move.
 */
public class Move {
    Piece p;    //piece to move
    ChessPosition end;    //new position
    Piece capturedPiece; //can be null
    Engine e;

    //values used for return
    ChessPosition start; //starting position of the piece. Used in en-passent as well
    boolean alreadyMoved; //if the piece had already moved or not.
    Move previousLastMove;

    private boolean executed; //states whether or not this move has been executed or not

    public Move(Piece p, ChessPosition end, Piece capture, Engine e, Move previousLastMove) {
        this.p = p;
        this.end = end;
        this.start = p.getPosition();
        this.capturedPiece = capture;
        this.e = e;
        this.previousLastMove = previousLastMove;
        this.alreadyMoved = p.hasMoved();
    }

    public Piece getPiece() {
        return this.p;
    }

    public ChessPosition getEndPosition() {
        return end;
    }

    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * Executes the move. It cannot already been executed
     */
    public void execute(Handler handler) {
        if (executed) throw new IllegalArgumentException();
        executed = true;
        p.setMoved(true);
        p.moveTo(end);
        handler.setLastMove(this);
        if (capturedPiece != null) {
            handler.removePiece(capturedPiece);
        }
        handler.changeTurn();
        handler.updateMoves();
    }

    /**
     * Reverts the move if already done. Can only be called if execute() has been called before.
     */
    public void undo(Handler handler) {
        if (!executed) throw new IllegalArgumentException();
        executed = false;
        p.setMoved(alreadyMoved);
        p.moveTo(start);
        handler.setLastMove(previousLastMove);
        if (capturedPiece != null) {
            handler.addPiece(capturedPiece);
        }
        handler.changeTurn();
        handler.updateMoves();
    }

    /**
     * Try a move out to check if the new position leads to a check on this side.
     * Should always be followed by an unTryMove().
     */
    public void tryMove(Handler handler) {
        p.moveTo(end);
        if (capturedPiece != null) {
            handler.removePiece(capturedPiece); //if the king takes a piece that is protected by one of the opposite color, he should still be checked.
                                                //when not removing the piece, the moves of the protecting piece would not cover this square
        }
        handler.updateMovesWithoutCheck();
    }

    public void unTryMove(Handler handler) {
        p.moveTo(start);
        if (capturedPiece != null) {
            handler.addPiece(capturedPiece);
        }
        handler.updateMovesWithoutCheck();
    }

    public boolean isExecuted() {
        return this.executed;
    }

    /**
     * Returns a copy of this, with a different handler and a different piece. This is used when copying the handler.
     * TODO: This move is already executed when copied. So the piece that is captured is not h.getPiece(end), because that
     * TODO: would be p. No clue how to copy properly. null should suffice, but it should be changed in subclasses as well.
     */
    public Move copy(Handler h, Piece p) {
        //not needed to copy the move before this one, as that would trigger a butterfly affect rippling down every lastMove.
        Move copyMove = new Move(p, this.end, h.getPiece(end), this.e, null);
        copyMove.start = this.start;
        copyMove.alreadyMoved = this.alreadyMoved;
        return copyMove;
    }

    @Override
    public String toString() {
        return this.p + " " + this.start + " " + this.end;
    }
}

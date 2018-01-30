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
    Handler handler;
    Engine e;

    //values used for return
    ChessPosition start; //starting position of the piece. Used in en-passent as well
    boolean alreadyMoved; //if the piece had already moved or not.
    Move previousLastMove;

    public Move(Piece p, ChessPosition end, Piece capture, Engine e) {
        this.p = p;
        this.end = end;
        this.start = p.getPosition();
        this.capturedPiece = capture;
        this.e = e;
        this.handler = e.getHandler();
        this.previousLastMove = e.getLastMove();
        this.alreadyMoved = p.hasMoved();
    }

    public Piece getPiece() {
        return this.p;
    }

    public ChessPosition getPosition() {
        return end;
    }

    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * Executes the move
     */
    public void execute() {
        p.setMoved(true);
        p.moveTo(end);
        e.setLastMove(this);
        if (capturedPiece != null) {
            handler.removePiece(capturedPiece);
        }
        e.changeTurn();
    }

    /**
     * Reverts the move if already done
     */
    public void undo() {
        p.setMoved(alreadyMoved);
        p.moveTo(start);
        e.setLastMove(previousLastMove);
        if (capturedPiece != null) {
            handler.addPiece(capturedPiece);
        }
        e.changeTurn();
    }

    /**
     * Try a move out to check if the new position leads to a check on this side.
     * Should always be followed by an unTryMove().
     */
    public void tryMove() {
        p.moveTo(end);
    }

    public void unTryMove() {
        p.moveTo(start);
    }

    @Override
    public String toString() {
        return this.p + " " + this.start + " " + this.end;
    }
}

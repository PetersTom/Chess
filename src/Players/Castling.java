package Players;

import pieces.ChessPosition;
import pieces.King;
import Engine.*;
import pieces.Piece;
import pieces.Rook;

public class Castling extends Move {

    Rook rook;
    ChessPosition start2;
    ChessPosition end2;
    boolean rookAlreadyMoved;

    public Castling(King king, ChessPosition l, Rook rook, ChessPosition end2, Engine e, Move previousLastMove) {
        super(king, l, null, e, previousLastMove);
        this.rook = rook;
        this.end2 = end2;
        this.rookAlreadyMoved = rook.hasMoved();
        this.start2 = rook.getPosition();
    }

    public King getKing() {
        return (King)this.getPiece();
    }

    public ChessPosition getKingMove() {
        return this.getEndPosition();
    }

    public Rook getRook() {
        return this.rook;
    }

    public ChessPosition getRookMove() {
        return this.end2;
    }

    @Override
    public void execute(Handler handler) {
        super.execute(handler);
        rook.setMoved(true);
        rook.moveTo(end2);
    }

    @Override
    public void undo(Handler handler) {
        super.undo(handler);
        rook.setMoved(rookAlreadyMoved);
        rook.moveTo(start2);
    }

    @Override
    public void tryMove(Handler handler) {
        super.tryMove(handler);
        rook.moveTo(end2);
    }

    @Override
    public void unTryMove(Handler handler) {
        super.unTryMove(handler);
        rook.moveTo(start2);
    }

    @Override
    public Move copy(Handler h, Piece p) {
        //not needed to copy the move before this one, as that would trigger a butterfly affect rippling down every lastMove.
        Castling copyMove = new Castling((King)p, this.end, (Rook)h.getPiece(end2), this.end2, this.e, null);
        copyMove.start = this.start;
        copyMove.alreadyMoved = this.alreadyMoved;
        copyMove.rook = (Rook)h.getPiece(start2);
        copyMove.start2 = this.start2;
        copyMove.end2 = this.end2;
        copyMove.rookAlreadyMoved = this.rookAlreadyMoved;
        return copyMove;
    }

}

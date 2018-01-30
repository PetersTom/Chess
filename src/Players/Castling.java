package Players;

import pieces.ChessPosition;
import pieces.King;
import Engine.Engine;
import pieces.Rook;

public class Castling extends Move {

    Rook rook;
    ChessPosition start2;
    ChessPosition end2;
    boolean rookAlreadyMoved;

    public Castling(King king, ChessPosition l, Rook rook, ChessPosition end2, Engine e) {
        super(king, l, null, e);
        this.rook = rook;
        this.end2 = end2;
        this.rookAlreadyMoved = rook.hasMoved();
        this.start2 = rook.getPosition();
    }

    public King getKing() {
        return (King)this.getPiece();
    }

    public ChessPosition getKingMove() {
        return this.getPosition();
    }

    public Rook getRook() {
        return this.rook;
    }

    public ChessPosition getRookMove() {
        return this.end2;
    }

    @Override
    public void execute() {
        super.execute();
        rook.setMoved(true);
        rook.moveTo(end2);
    }

    @Override
    public void undo() {
        super.undo();
        rook.setMoved(rookAlreadyMoved);
        rook.moveTo(start2);
    }

    @Override
    public void tryMove() {
        super.tryMove();
        rook.moveTo(end2);
    }

    @Override
    public void unTryMove() {
        super.unTryMove();
        rook.moveTo(start2);
    }

}

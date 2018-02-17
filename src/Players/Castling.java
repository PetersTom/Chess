package Players;

import pieces.ChessPosition;
import pieces.King;
import Engine.*;
import pieces.Piece;
import pieces.Rook;

public class Castling extends Move {

    ChessPosition rookStart;
    ChessPosition rookEnd;

    public Castling(ChessPosition kingStart, ChessPosition kingEnd, ChessPosition rookStart, ChessPosition rookEnd, Engine e, Move previousLastMove) {
        super(kingStart, kingEnd, null, e, previousLastMove);
        this.rookEnd = rookEnd;
        this.rookStart = rookStart;
    }

    public ChessPosition getRookEndPosition() {
        return this.rookEnd;
    }

    public ChessPosition getRookStartPosition() {
        return rookStart;
    }
}

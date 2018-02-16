package Players;

import Engine.*;
import pieces.*;

/**
 * A move that will promote a pawn
 */
public class PawnPromotion extends Move {

    Piece toPromoteTo;

    public PawnPromotion(Piece p, ChessPosition end, Piece capture, Engine e, Move previousLastMove, Piece toPromoteTo) {
        super(p, end, capture, e, previousLastMove);
        this.toPromoteTo = toPromoteTo;
    }

    @Override
    public void execute(Handler handler) {
        super.execute(handler);
        handler.removePiece(p);
        handler.addPiece(toPromoteTo);
    }

    @Override
    public void undo(Handler handler) {
        super.undo(handler);
        handler.removePiece(toPromoteTo);
        handler.addPiece(p);
    }

    public String getPromotionType() {
        if (toPromoteTo instanceof Queen) {
            return "Queen";
        } else if (toPromoteTo instanceof Rook) {
            return "Rook";
        } else if (toPromoteTo instanceof Knight) {
            return "Knight";
        } else if (toPromoteTo instanceof Bishop) {
            return "Bishop";
        } else {
            return "No valid promotion type";
        }
    }

    public Piece getPromotionPiece() {
        return this.toPromoteTo;
    }

    @Override
    public Move copy(Handler h, Piece p) {
        //not needed to copy the move before this one, as that would trigger a butterfly affect rippling down every lastMove.
        PawnPromotion copyMove = new PawnPromotion(p, this.end, h.getPiece(end), this.e, null, h.getPiece(end));
        copyMove.start = this.start;
        copyMove.alreadyMoved = this.alreadyMoved;
        copyMove.toPromoteTo = h.getPiece(end);
        return copyMove;
    }
}

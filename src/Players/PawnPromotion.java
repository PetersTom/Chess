package Players;

import Engine.Engine;
import pieces.*;

/**
 * A move that will promote a pawn
 */
public class PawnPromotion extends Move {

    Piece toPromoteTo;

    public PawnPromotion(Piece p, ChessPosition end, Piece capture, Engine e, Piece toPromoteTo) {
        super(p, end, capture, e);
        this.toPromoteTo = toPromoteTo;
    }

    @Override
    public void execute() {
        super.execute();
        handler.removePiece(p);
        handler.addPiece(toPromoteTo);
    }

    @Override
    public void undo() {
        super.undo();
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
}

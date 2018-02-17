package Players;

import Engine.*;
import pieces.*;

/**
 * A move that will promote a pawn
 */
public class PawnPromotion extends Move {

    Piece toPromoteTo;
    Piece pawn;

    public PawnPromotion(ChessPosition start, ChessPosition end, Piece capture, Engine e, Move previousLastMove, Piece pawn, Piece toPromoteTo) {
        super(start, end, capture, e, previousLastMove);
        this.toPromoteTo = toPromoteTo;
        this.pawn = pawn;
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

    public Piece getPawn() {
        return this.pawn;
    }

    public Piece getPromotionPiece() {
        return this.toPromoteTo;
    }
}

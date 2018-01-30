package pieces;

import Engine.Engine;
import Players.Move;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Rook extends Piece {

    public Rook(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (c == ChessColor.Black) {
            file += "bRook.png";
        } else {
            file += "wRook.png";
        }
        setupImg();
    }

    @Override
    public Set<Move> getMoves() {
        Set<ChessPosition> possibleMoves = new HashSet<>();
        int x = getPosition().x;
        int y = getPosition().y;
        for (int i = x + 1; i <= Engine.CELL_AMOUNT; i++) { //horizontal to the right
            Piece p = handler.getPiece(i, y);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(i, y, canvas));
                }
                break;  //break out the loop, one can't place a piece behind another
            }
            possibleMoves.add(new ChessPosition(i, y, canvas));
        }

        for (int i = x - 1; i > 0; i--) { //horizontal to the left
            Piece p = handler.getPiece(i, y);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(i, y, canvas));
                }
                break;  //break out the loop, one can't place a piece behind another
            }
            possibleMoves.add(new ChessPosition(i, y, canvas));
        }

        for (int i = y + 1; i <= Engine.CELL_AMOUNT; i++) { //vertical up
            Piece p = handler.getPiece(x, i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x, i, canvas));
                }
                break; //break out the loop, one can't place a piece behind another
            }
            possibleMoves.add(new ChessPosition(x, i, canvas));
        }

        for (int i = y - 1; i > 0; i--) { //vertical down
            Piece p = handler.getPiece(x, i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x, i, canvas));
                }
                break; //break out the loop, one can't place a piece behind another
            }
            possibleMoves.add(new ChessPosition(x, i, canvas));
        }

        return possibleMoves.stream().map(m -> new Move(this, m, handler.getPiece(m), e)).collect(Collectors.toSet());
    }
}

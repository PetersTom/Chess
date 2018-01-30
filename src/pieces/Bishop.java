package pieces;

import Players.Move;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.Engine;

public class Bishop extends Piece {

    public Bishop(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (c == ChessColor.Black) {
            file += "bBishop.png";
        } else {
            file += "wBishop.png";
        }
        setupImg();
    }


    @Override
    public Set<Move> getMoves() {
        Set<ChessPosition> possibleMoves = new HashSet<>();
        int x = getPosition().x;
        int y = getPosition().y;
        int i = 1;
        while (x + i <= Engine.CELL_AMOUNT && y + i <= Engine.CELL_AMOUNT) { //right up
            Piece p = handler.getPiece(x+i, y+i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+i, y+i, canvas));
                }
                break;
            }
            possibleMoves.add(new ChessPosition(x+i, y+i, canvas));
            i++;
        }
        i = 1;
        while (x - i > 0 && y + i <= Engine.CELL_AMOUNT) { //left up
            Piece p = handler.getPiece(x-i, y+i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-i, y+i, canvas));
                }
                break;
            }
            possibleMoves.add(new ChessPosition(x-i, y+i, canvas));
            i++;
        }
        i = 1;
        while (x - i > 0 && y - i > 0) { //left down
            Piece p = handler.getPiece(x-i, y-i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-i, y-i, canvas));
                }
                break;
            }
            possibleMoves.add(new ChessPosition(x-i, y-i, canvas));
            i++;
        }
        i = 1;
        while (x + i <= Engine.CELL_AMOUNT && y - i > 0) { //right down
            Piece p = handler.getPiece(x+i, y-i);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+i, y-i, canvas));
                }
                break;
            }
            possibleMoves.add(new ChessPosition(x+i, y-i, canvas));
            i++;
        }
        return possibleMoves.stream().map(m -> new Move(this, m, handler.getPiece(m), e)).collect(Collectors.toSet());
    }
}

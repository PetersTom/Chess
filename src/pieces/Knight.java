package pieces;

import Players.Move;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.Engine;

public class Knight extends Piece {

    public Knight(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (c == ChessColor.Black) {
            file += "bKnight.png";
        } else {
            file += "wKnight.png";
        }
        setupImg();
    }

    @Override
    public Piece copy() {
        return new Knight(this.getPosition(), this.getColor(), cellWidth, e);
    }

    @Override
    public Set<Move> getMoves() {
        Set<ChessPosition> possibleMoves = new HashSet<>();
        int x = getPosition().x;
        int y = getPosition().y;
        if (x + 2 <= Engine.CELL_AMOUNT && y + 1 <= Engine.CELL_AMOUNT) {
            Piece p = handler.getPiece(x+2, y+1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+2, y+1, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x + 2, y + 1, canvas));
            }
        }
        if (x + 1 <= Engine.CELL_AMOUNT && y + 2 <= Engine.CELL_AMOUNT) {
            Piece p = handler.getPiece(x+1, y+2);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+1, y+2, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x + 1, y + 2, canvas));
            }
        }
        if (x - 2 > 0 && y + 1 <= Engine.CELL_AMOUNT) {
            Piece p = handler.getPiece(x-2, y+1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-2, y+1, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x - 2, y + 1, canvas));
            }
        }
        if (x - 1 > 0 && y + 2 <= Engine.CELL_AMOUNT) {
            Piece p = handler.getPiece(x-1, y+2);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-1, y+2, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x - 1, y + 2, canvas));
            }
        }
        if (x + 1 <= Engine.CELL_AMOUNT && y - 2 > 0) {
            Piece p = handler.getPiece(x+1, y-2);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+1, y-2, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x + 1, y - 2, canvas));
            }
        }
        if (x + 2 <= Engine.CELL_AMOUNT && y - 1 > 0) {
            Piece p = handler.getPiece(x+2, y-1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x+2, y-1, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x + 2, y - 1, canvas));
            }
        }
        if (x - 1 > 0 && y - 2 > 0) {
            Piece p = handler.getPiece(x-1, y-2);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-1, y-2, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x - 1, y - 2, canvas));
            }
        }
        if (x - 2 > 0 && y - 1 > 0) {
            Piece p = handler.getPiece(x-2, y-1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleMoves.add(new ChessPosition(x-2, y-1, canvas));
                }
            } else {
                possibleMoves.add(new ChessPosition(x - 2, y - 1, canvas));
            }
        }
        return possibleMoves.stream().map(m -> new Move(this, m, handler.getPiece(m), e)).collect(Collectors.toSet());
    }
}

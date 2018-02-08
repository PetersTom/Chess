package pieces;

import Players.Move;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.Engine;

public class Queen extends Piece {

    public Queen(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (c == ChessColor.Black) {
            file += "bQueen.png";
        } else {
            file += "wQueen.png";
        }
        setupImg();
        pieceValue = 9;
        if (c == ChessColor.Black) {
            pieceValue = -9;
        }
    }

    @Override
    public Piece copy() {
        return new Queen(this.getPosition(), this.getColor(), cellWidth, e);
    }


    /**
     * Continuously making new objects is a lot of overhead, but it is easy to code and insightful, if
     * performance is needed copy the code in bishop and rook to here. The objects are discarded at the end of the method
     * by the carbage collector. They will not be visible when not added to the handler.
     *
     * Edit: performance was needed, so the code was copied.
     * @return
     */
    @Override
    public Set<Move> getMoves() {
//        Set<Move> possibleMoves =  new HashSet<>();
//        Piece bishop = new Bishop(this.getPosition(), this.getColor(), this.cellWidth, e);
//        possibleMoves.addAll(bishop.getMoves());
//        Piece rook = new Rook(this.getPosition(), this.getColor(), this.cellWidth, e);
//        possibleMoves.addAll(rook.getMoves());
//        return possibleMoves.stream().map(m -> new Move(this, m.getPosition(), handler.getPiece(m.getPosition()), e)).collect(Collectors.toSet());

        Set<ChessPosition> possibleMoves = new HashSet<>();
        int x = getPosition().x;
        int y = getPosition().y;
        //Rook moves
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


        //bishop moves
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

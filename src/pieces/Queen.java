package pieces;

import Players.Move;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.*;

import javax.imageio.ImageIO;

public class Queen extends Piece {

    private static Image wimg;
    private static Image bimg;

    public Queen(ChessPosition p, ChessColor c, int cellWidth, Engine e, Handler h) {
        super(p, c, cellWidth, e, h);
        if (wimg == null && bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wQueen.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bQueen.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read Queen image");
                ex.printStackTrace();
            }
        }
        pieceValue = 9;
        if (c == ChessColor.Black) {
            pieceValue = -9;
        }
    }

    @Override
    public Image getImg() {
        if (this.getColor() == ChessColor.White) {
            return Queen.wimg;
        } else {
            return Queen.bimg;
        }
    }

    @Override
    public Piece copy(Handler h) {
        return new Queen(this.getPosition(), this.getColor(), cellWidth, e, h);
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
//        Piece bishop = new Bishop(this.getEndPosition(), this.getColor(), this.cellWidth, e);
//        possibleMoves.addAll(bishop.getMoves());
//        Piece rook = new Rook(this.getEndPosition(), this.getColor(), this.cellWidth, e);
//        possibleMoves.addAll(rook.getMoves());
//        return possibleMoves.stream().map(m -> new Move(this, m.getEndPosition(), handler.getPiece(m.getEndPosition()), e)).collect(Collectors.toSet());

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


        return possibleMoves.stream().map(m -> new Move(this, m, handler.getPiece(m), e, this.handler.getLastMove())).collect(Collectors.toSet());
    }
}

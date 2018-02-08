package pieces;

import Engine.Engine;
import Players.Move;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Rook extends Piece {

    private static Image wimg;
    private static Image bimg;

    public Rook(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (wimg == null && bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wRook.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bRook.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read Queen image");
                ex.printStackTrace();
            }
        }
        pieceValue = 5;
        if (c == ChessColor.Black) {
            pieceValue = -5;
        }
    }

    @Override
    public Image getImg() {
        if (this.getColor() == ChessColor.White) {
            return Rook.wimg;
        } else {
            return Rook.bimg;
        }
    }

    @Override
    public Piece copy() {
        return new Rook(this.getPosition(), this.getColor(), cellWidth, e);
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

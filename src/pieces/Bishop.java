package pieces;

import Players.AI.ChessNode;
import Players.Move;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.Engine;

import javax.imageio.ImageIO;
import java.awt.Image;

public class Bishop extends Piece {

    //the black and white images
    private static Image wimg;
    private static Image bimg;

    public Bishop(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (wimg == null && bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wBishop.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bBishop.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read Bishop image");
                ex.printStackTrace();
            }
        }
        pieceValue = 3;
        if (c == ChessColor.Black) {
            pieceValue = -3;
        }
    }

    @Override
    public Image getImg() {
        if (this.getColor() == ChessColor.White) {
            return Bishop.wimg;
        } else {
            return Bishop.bimg;
        }
    }

    @Override
    public Piece copy() {
        return new Bishop(this.getPosition(), this.getColor(), cellWidth, e);
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

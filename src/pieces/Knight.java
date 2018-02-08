package pieces;

import Players.Move;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.Engine;

import javax.imageio.ImageIO;

public class Knight extends Piece {

    private static Image wimg;
    private static Image bimg;

    public Knight(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (wimg == null &&  bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wKnight.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bKnight.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read Knight image");
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
            return Knight.wimg;
        } else {
            return Knight.bimg;
        }
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

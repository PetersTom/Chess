package pieces;

import Players.Move;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import Engine.*;
import Players.PawnPromotion;

import javax.imageio.ImageIO;

public class Pawn extends Piece {

    private static Image wimg;
    private static Image bimg;

    public Pawn(ChessColor c, Engine e, Handler h) {
        super(c, e, h);
        if (wimg == null && bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wPawn.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bPawn.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read Pawn image");
                ex.printStackTrace();
            }
        }
        pieceValue = 1;
        if (c == ChessColor.Black) {
            pieceValue = -1;
        }
    }

    @Override
    public Image getImg() {
        if (this.getColor() == ChessColor.White) {
            return Pawn.wimg;
        } else {
            return Pawn.bimg;
        }
    }

    @Override
    public Set<Move> getMoves(ChessPosition position) {
        Set<ChessPosition> possibleChessPositions = new HashSet<>();
        Set<Move> possibleMoves = new HashSet<>();
        int x = position.x;
        int y = position.y;
        if (this.getColor() == ChessColor.White) {
            if (handler.getPiece(x, y + 1) == null) {
                if (y < Engine.CELL_AMOUNT) {   //not the last row
                    possibleChessPositions.add(new ChessPosition(x, y + 1, canvas));
                }
                if (y == 2 && handler.getPiece(x, y + 2) == null) { //if on home row and no blocking piece, add the double step
                    possibleChessPositions.add(new ChessPosition(x, y + 2, canvas));
                }
            }
            //captures
            Piece p = handler.getPiece(x - 1, y + 1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleChessPositions.add(new ChessPosition(x - 1, y + 1, canvas));
                }
            }
            p = handler.getPiece(x + 1, y + 1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleChessPositions.add(new ChessPosition(x + 1, y + 1, canvas));
                }
            }
            //en-passent
            if (y == 5) {
                Move lastMove = handler.getLastMove();
                if (handler.getPiece(x+1, y) instanceof Pawn) { //if it is a pawn
                    if (lastMove.getEndPosition().equals(new ChessPosition(x + 1, y, canvas))) {
                        if (lastMove.getStartPosition().equals(new ChessPosition(x + 1, y + 2, canvas))) { //and it did the last move
                            possibleMoves.add(new Move(position, new ChessPosition(x + 1, y + 1, canvas), handler.getPiece(x + 1, y), e, this.handler.getLastMove()));
                        }
                    }
                }
                if (handler.getPiece(x-1, y) instanceof Pawn) {
                    if (lastMove.getEndPosition().equals(new ChessPosition(x - 1, y, canvas))) {
                        if (lastMove.getStartPosition().equals(new ChessPosition(x - 1, y + 2, canvas))) {
                            possibleMoves.add(new Move(position, new ChessPosition(x - 1, y + 1, canvas), handler.getPiece(x - 1, y), e, this.handler.getLastMove()));
                        }
                    }
                }
            }
        } else {    //color is black
            if (handler.getPiece(x, y-1) == null) {
                if (y > 0) {    //not the last row
                    possibleChessPositions.add(new ChessPosition(x, y-1, canvas));
                }
                if (y == 7 && handler.getPiece(x, y - 2) == null) { //if on home row and no blocking piece, add the double step
                    possibleChessPositions.add(new ChessPosition(x, y - 2, canvas));
                }
            }
            //captures
            Piece p = handler.getPiece(x-1, y - 1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleChessPositions.add(new ChessPosition(x -1, y - 1, canvas));
                }
            }
            p = handler.getPiece(x + 1, y - 1);
            if (p != null) {
                if (p.getColor() != this.getColor()) {
                    possibleChessPositions.add(new ChessPosition(x + 1, y - 1, canvas));
                }
            }
            //en-passent
            if (y == 4) {
                Move lastMove = handler.getLastMove();
                if (handler.getPiece(x+1, y) instanceof Pawn) {
                    if (lastMove.getEndPosition().equals(new ChessPosition(x+1, y, canvas))) {
                        if (lastMove.getStartPosition().equals(new ChessPosition(x + 1, y - 2, canvas))) {
                            possibleMoves.add(new Move(position, new ChessPosition(x + 1, y - 1, canvas), handler.getPiece(x + 1, y), e, this.handler.getLastMove()));
                        }
                    }
                }
                if (handler.getPiece(x-1, y) instanceof Pawn) {
                    if (lastMove.getEndPosition().equals(new ChessPosition(x - 1, y, canvas))) {
                        if (lastMove.getStartPosition().equals(new ChessPosition(x - 1, y - 2, canvas))) {
                            if (new ChessPosition(x - 1, y - 2, canvas).equals(lastMove.getStartPosition())) {
                                possibleMoves.add(new Move(position, new ChessPosition(x - 1, y - 1, canvas), handler.getPiece(x - 1, y), e, this.handler.getLastMove()));
                            }
                        }
                    }
                }
            }
        }
        //pawn promotions:
        Set<ChessPosition> promotions;
        if (this.getColor() == ChessColor.White) {
            promotions = possibleChessPositions.stream().filter(p -> p.y == 8).collect(Collectors.toSet());
        } else { //black
            promotions = possibleChessPositions.stream().filter(p -> p.y == 1).collect(Collectors.toSet());
        }
        possibleChessPositions.removeAll(promotions);
        Set<Move> promotionMoves = new HashSet<>();
        for (ChessPosition p : promotions) {
            promotionMoves.addAll(getPromotions(p));
        }
        possibleMoves.addAll(possibleChessPositions.stream().map(m -> new Move(position, m, handler.getPiece(m), e, this.handler.getLastMove())).collect(Collectors.toSet()));
        possibleMoves.addAll(promotionMoves);
        return possibleMoves;
    }

    private Set<Move> getPromotions(ChessPosition p) {
        Queen queen = new Queen(this.getColor(), e, handler);
        Knight knight = new Knight(this.getColor(), e, handler);
        Rook rook = new Rook(this.getColor(), e, handler);
        Bishop bishop = new Bishop(this.getColor(), e, handler);
        Set<Move> promotions = new HashSet<>();
        ChessPosition start = null;
        if (p.y == 8) {
            start = new ChessPosition (p.x, p.y - 1, canvas);
        } else if (p.y == 1) {
            start = new ChessPosition(p.x, p.y + 1, canvas);
        }
        promotions.add(new PawnPromotion(start, p, handler.getPiece(p), e, this.handler.getLastMove(), this, queen));
        promotions.add(new PawnPromotion(start, p, handler.getPiece(p), e, this.handler.getLastMove(), this, knight));
        promotions.add(new PawnPromotion(start, p, handler.getPiece(p), e, this.handler.getLastMove(), this, rook));
        promotions.add(new PawnPromotion(start, p, handler.getPiece(p), e, this.handler.getLastMove(), this, bishop));
        return promotions;
    }
}

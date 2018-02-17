package pieces;

import Players.Castling;
import Players.Move;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Engine.*;

import javax.imageio.ImageIO;

public class King extends Piece {

    private static Image wimg;
    private static Image bimg;

    public King(ChessColor c, Engine e, Handler h) {
        super(c, e, h);
        if (wimg == null && bimg == null) {
            try {
                URL u = getClass().getClassLoader().getResource("wKing.png");
                assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                //because when that happens, the application cannot start anyways.
                wimg = ImageIO.read(u);
                u = getClass().getClassLoader().getResource("bKing.png");
                assert u != null;
                bimg = ImageIO.read(u);
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Can't read King image");
                ex.printStackTrace();
            }
        }
        pieceValue = 0; //the king has no real value, as it cannot be taken anyway. It could be set to infinity, but
                        //overflow issues would arise when computing the overall piece value.
    }

    @Override
    public Image getImg() {
        if (this.getColor() == ChessColor.White) {
            return King.wimg;
        } else {
            return King.bimg;
        }
    }

    @Override
    public Set<Move> getMoves(ChessPosition position) {
        Set<ChessPosition> possiblePositions = new HashSet<>();
        int x = position.x;
        int y = position.y;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Piece p = handler.getPiece(x + i, y + j);   //the king itself is skipped because of the color check
                if (p != null) {
                    if (p.getColor() != this.getColor()) {
                        possiblePositions.add(new ChessPosition(x+i, y+j, canvas));
                    }
                    continue; //do not add the move if there is a piece with the same color
                }
                possiblePositions.add(new ChessPosition(x+i, y+j, canvas));
            }
        }
        //remove out of bounds
        Stream<ChessPosition> chessPositionStream = possiblePositions.stream().filter(p -> p.x > 0 && p.x <= Engine.CELL_AMOUNT && p.y > 0 && p.y <= Engine.CELL_AMOUNT);
        Set<ChessPosition> possiblePositionsWithinBounds = chessPositionStream.collect(Collectors.toSet());
        Set<Move> possibleMoves = possiblePositionsWithinBounds.stream().map(p -> new Move(position, p, handler.getPiece(p), p, e, this.handler.getLastMove())).collect(Collectors.toSet());

        return possibleMoves;
    }

    /**
     * The castling moves should be added here to prevent a stackoverflow with continuous checking for the other king
     */
    @Override
    public Set<Move> getMovesWithCheck(ChessPosition position) {
        Set<Move> castlingMoves = new HashSet<>();
        //add castling
        if (this.getColor() == ChessColor.White) {
            Set<ChessPosition> otherMovePositions = getOtherPiecesMovePositions();
            if (position.x == 5 && position.y == 1) {   //basePosition
                Piece p = handler.getPiece(8, 1);   //short castling
                Rook r;
                if (p instanceof Rook) {
                    if (handler.getPiece(7, 1) == null && handler.getPiece(6, 1) == null) { //no other pieces
                        r = (Rook) p;
                        if (handler.whiteShortCastlingPossible()) { //both not moved
                            if (!isChecked(position)) { //not checked
                                if (!otherMovePositions.contains(new ChessPosition(6,1, canvas))) { //check if going over on on a checked square
                                    if (!otherMovePositions.contains(new ChessPosition(7, 1, canvas))) {
                                        castlingMoves.add(new Castling(position, new ChessPosition(7, 1, canvas),
                                                new ChessPosition(8, 1, canvas), new ChessPosition(6, 1, canvas), e, this.handler.getLastMove()));
                                    }
                                }
                            }
                        }
                    }
                }
                p = handler.getPiece(1, 1); //long castling
                if (p instanceof Rook) {
                    if (handler.getPiece(2, 1) == null && handler.getPiece(3, 1) == null && handler.getPiece(4, 1) == null) {   //no pieces in the way
                        r = (Rook) p;
                        if (handler.whiteLongCastlingPossible()) { //both not moved
                            if (!isChecked(position)) { //not checked
                                if (!otherMovePositions.contains(new ChessPosition(3, 1, canvas))) {    //not going over a checked square
                                    if (!otherMovePositions.contains(new ChessPosition(4, 1, canvas))) {
                                        castlingMoves.add(new Castling(position, new ChessPosition(3, 1, canvas),
                                                    new ChessPosition(1, 1, canvas), new ChessPosition(4, 1, canvas), e, this.handler.getLastMove()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else { //black
            Set<ChessPosition> otherMovePositions = getOtherPiecesMovePositions();
            if (position.x == 5 && position.y == 8) { //basePosition
                Piece p = handler.getPiece(8, 8); //short castling
                Rook r;
                if (p instanceof Rook) {
                    if (handler.getPiece(7, 8) == null && handler.getPiece(6, 8) == null) { //no other pieces
                        r = (Rook) p;
                        if (handler.blackShortCastlingPossible()) {    //both not moved
                            if (!isChecked(position)) {
                                if (!otherMovePositions.contains(new ChessPosition(6, 8, canvas))) {
                                    if (!otherMovePositions.contains(new ChessPosition(7, 8, canvas))) {
                                        castlingMoves.add(new Castling(position, new ChessPosition(7, 8, canvas),
                                                new ChessPosition(8, 8, canvas), new ChessPosition(6, 8, canvas), e, this.handler.getLastMove()));
                                    }
                                }
                            }
                        }
                    }
                }
                p = handler.getPiece(1, 8); //long castling
                if (p instanceof Rook) {
                    if (handler.getPiece(2, 8) == null && handler.getPiece(3, 8) == null && handler.getPiece(4, 8) == null) {   //no pieces in the way
                        r = (Rook) p;
                        if (handler.blackLongCastlingPossible()) { //both not moved
                            if (!isChecked(position)) {
                                if (!otherMovePositions.contains(new ChessPosition(3, 8, canvas))) {
                                    if (!otherMovePositions.contains(new ChessPosition(4, 8, canvas))) {
                                        castlingMoves.add(new Castling(position, new ChessPosition(3, 8, canvas),
                                                    new ChessPosition(1, 8, canvas), new ChessPosition(4, 8, canvas), e, this.handler.getLastMove()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        castlingMoves.addAll(super.getMovesWithCheck(position));
        return castlingMoves;
    }

    /**
     * Gets the positions the other pieces can move to.
     * It excludes the positions of the king if it has not moved yet to prevent an infinite check for the moves of the kings
     * @return
     */
    private Set<ChessPosition> getOtherPiecesMovePositions() {
        return handler.getOppositeColorMoves(this.getColor()).stream().map(Move::getEndPosition).collect(Collectors.toSet());
    }

    public boolean isChecked(ChessPosition position) {
        Set<Move> otherColorMoves = handler.getOppositeColorMoves(this.getColor());
        for (Move m : otherColorMoves) {
            if (m.getEndPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMated(ChessPosition position) {
        Set<Move> possibleMoves = handler.getMovesWithCheck(this.getColor());
        if (isChecked(position) && possibleMoves.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isStaleMated(ChessPosition position) {
        Set<Move> possibleMoves = handler.getMovesWithCheck(this.getColor());
        if (!isChecked(position) && possibleMoves.isEmpty()) {
            return true;
        }
        return false;
    }
}

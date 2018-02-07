package pieces;

import Players.Castling;
import Players.Move;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Engine.*;

public class King extends Piece {

    public King(ChessPosition p, ChessColor c, int cellWidth, Engine e) {
        super(p, c, cellWidth, e);
        if (c == ChessColor.Black) {
            file += "bKing.png";
        } else {
            file += "wKing.png";
        }
        setupImg();
    }

    @Override
    public Piece copy() {
        return new King(this.getPosition(), this.getColor(), cellWidth, e);
    }

    @Override
    public Set<Move> getMoves() {
        Set<ChessPosition> possiblePositions = new HashSet<>();
        int x = getPosition().x;
        int y = getPosition().y;
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
        Set<Move> possibleMoves = possiblePositionsWithinBounds.stream().map(m -> new Move(this, m, handler.getPiece(m), e)).collect(Collectors.toSet());

        return possibleMoves;
    }

    /**
     * The castling moves should be add here to prevent a stackoverflow with continuous checking for the other king
     * @return
     */
    @Override
    public Set<Move> getMovesWithCheck() {
        Set<Move> castlingMoves = new HashSet<>();
        //add castling
        if (this.getColor() == ChessColor.White) {
            Set<ChessPosition> otherMovePositions = getOtherPiecesMovePositions();
            if (this.getPosition().x == 5 && this.getPosition().y == 1) {   //basePosition
                Piece p = handler.getPiece(8, 1);   //short castling
                Rook r;
                if (p instanceof Rook) {
                    if (handler.getPiece(7, 1) == null && handler.getPiece(6, 1) == null) { //no other pieces
                        r = (Rook) p;
                        if (!r.hasMoved() && !this.hasMoved()) { //both not moved
                            if (!isChecked()) { //not checked
                                if (!otherMovePositions.contains(new ChessPosition(6,1, canvas))) { //check if going over on on a checked square
                                    if (!otherMovePositions.contains(new ChessPosition(7, 1, canvas))) {
                                        castlingMoves.add(new Castling(this, new ChessPosition(7, 1, canvas),
                                                r, new ChessPosition(6, 1, canvas), e));
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
                        if (!r.hasMoved() && !this.hasMoved()) { //both not moved
                            if (!isChecked()) { //not checked
                                if (!otherMovePositions.contains(new ChessPosition(3, 1, canvas))) {    //not going over a checked square
                                    if (!otherMovePositions.contains(new ChessPosition(4, 1, canvas))) {
                                        castlingMoves.add(new Castling(this, new ChessPosition(3, 1, canvas),
                                                    r, new ChessPosition(4, 1, canvas), e));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else { //black
            Set<ChessPosition> otherMovePositions = getOtherPiecesMovePositions();
            if (this.getPosition().x == 5 && this.getPosition().y == 8) { //basePosition
                Piece p = handler.getPiece(8, 8); //short castling
                Rook r;
                if (p instanceof Rook) {
                    if (handler.getPiece(7, 8) == null && handler.getPiece(6, 8) == null) { //no other pieces
                        r = (Rook) p;
                        if (!r.hasMoved() && !this.hasMoved()) {    //both not moved
                            if (!isChecked()) {
                                if (!otherMovePositions.contains(new ChessPosition(6, 8, canvas))) {
                                    if (!otherMovePositions.contains(new ChessPosition(7, 8, canvas))) {
                                        castlingMoves.add(new Castling(this, new ChessPosition(7, 8, canvas),
                                                r, new ChessPosition(6, 8, canvas), e));
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
                        if (!r.hasMoved() && !this.hasMoved()) { //both not moved
                            if (!isChecked()) {
                                if (!otherMovePositions.contains(new ChessPosition(3, 8, canvas))) {
                                    if (!otherMovePositions.contains(new ChessPosition(4, 8, canvas))) {
                                        castlingMoves.add(new Castling(this, new ChessPosition(3, 8, canvas),
                                                    r, new ChessPosition(4, 8, canvas), e));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        castlingMoves.addAll(super.getMovesWithCheck());
        return castlingMoves;
    }

    /**
     * Gets the positions the other pieces can move to.
     * It excludes the positions of the king if it has not moved yet to prevent an infinite check for the moves of the kings
     * @return
     */
    private Set<ChessPosition> getOtherPiecesMovePositions() {
        return handler.getOppositeColorMoves(this.getColor()).stream().map(Move::getPosition).collect(Collectors.toSet());
    }

    public boolean isChecked() {
        Set<Move> otherColorMoves = handler.getOppositeColorMoves(this.getColor()); //these are always up-to-date because Move udpates it every time
        for (Move m : otherColorMoves) {
            if (m.getPosition().equals(this.getPosition())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMated() {
        Set<Move> possibleMoves = handler.getMovesWithCheck(this.getColor());
        if (isChecked() && possibleMoves.isEmpty()) {
            return true;
        }
        return false;
    }
}

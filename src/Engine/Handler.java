package Engine;

import GUI.ChessCanvas;
import Players.Move;
import pieces.ChessColor;
import pieces.ChessPosition;
import pieces.King;
import pieces.Piece;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all the game objects
 */
public class Handler {
    //needs to be volatile, because both the game loop and window updates can access at the same time.
    volatile private Set<Piece> pieces;
    private ChessCanvas canvas;

    //to be updated after every move. To prevent continuous fetching of moves.
    private Set<Move> whitePlayerMoves; //every move of the white player
    private Set<Move> blackPlayerMoves;
    private Set<Move> whitePlayerMovesWithCheck; //every valid move of the white player (that will not result in check)
    private Set<Move> blackPlayerMovesWithCheck;

    public Handler(Engine e) {
        pieces = Collections.synchronizedSet(new HashSet<>());
        canvas = e.getCanvas();
    }

    public synchronized void addPiece(Piece p) {
        pieces.add(p);
    }

    public synchronized void removePiece(Piece p) {
        pieces.remove(p);
    }

    public synchronized Piece getPiece(ChessPosition position) {
        Optional<Piece> piece = pieces.stream().filter(p -> p.getPosition().equals(position)).findFirst();
        return piece.orElse(null);
    }

    public Piece getPiece(int x, int y) {
        return getPiece(new ChessPosition(x, y, canvas));
    }

    /**
     * Get all pieces.
     */
    public Set<Piece> getPieces() {
        return this.pieces;
    }

    /**
     * Get all pieces of a specific color
     */
    public synchronized Set<Piece> getPieces(ChessColor c) {
        return new HashSet<>(pieces.stream().filter(p -> p.getColor() == c).collect(Collectors.toSet()));
    }

    public Set<Piece> getWhitePieces() {
        return getPieces(ChessColor.White);
    }

    public Set<Piece> getBlackPieces() {
        return getPieces(ChessColor.Black);
    }

    public synchronized Set<Piece> getOppositeColorPieces(ChessColor c) {
        if (c == ChessColor.White) {
            return getBlackPieces();
        } else {
            return getWhitePieces();
        }
    }

    public synchronized King getKing(ChessColor c) {
        if (c == ChessColor.White) {
            return getWhiteKing();
        } else {
            return getBlackKing();
        }
    }

    public synchronized King getOppositeColorKing(ChessColor c) {
        if (c == ChessColor.White) {
            return getBlackKing();
        } else {
            return getWhiteKing();
        }
    }

    public synchronized King getWhiteKing() {
        Optional<Piece> whiteKing = pieces.stream().filter(p -> (p instanceof King)&&(p.getColor() == ChessColor.White)).findFirst();
        //return the whiteking, or null if there is none
        return (King) whiteKing.orElse(null);
    }

    public synchronized King getBlackKing() {
        Optional<Piece> blackKing = pieces.stream().filter(p -> (p instanceof King)&&(p.getColor() == ChessColor.Black)).findFirst();
        //return the black king, or null if there is none
        return (King) blackKing.orElse(null);
    }

    /**
     * Updates the moves for every player. This happens whenever someone executes a move. To prevent continous calculation of the moves.
     */
    public synchronized void updateMoves() {
        whitePlayerMoves = new HashSet<>();
        blackPlayerMoves = new HashSet<>();
        whitePlayerMovesWithCheck = new HashSet<>();
        blackPlayerMovesWithCheck = new HashSet<>();
        Set<Piece> whitePieces = getPieces(ChessColor.White);
        for (Piece p : whitePieces) {
            whitePlayerMoves.addAll(p.getMoves());
            whitePlayerMovesWithCheck.addAll(p.getMovesWithCheck());
        }
        Set<Piece> blackPieces = getPieces(ChessColor.Black);
        for (Piece p : blackPieces) {
            blackPlayerMoves.addAll(p.getMoves());
            blackPlayerMovesWithCheck.addAll((p.getMovesWithCheck()));
        }
    }
    public synchronized void updateMovesWithoutCheck() {
        whitePlayerMoves = new HashSet<>();
        blackPlayerMoves = new HashSet<>();
        Set<Piece> whitePieces = getPieces(ChessColor.White);
        for (Piece p : whitePieces) {
            whitePlayerMoves.addAll(p.getMoves());
        }
        Set<Piece> blackPieces = getPieces(ChessColor.Black);
        for (Piece p : blackPieces) {
            blackPlayerMoves.addAll(p.getMoves());
        }
    }

    public synchronized Set<Move> getMoves(ChessColor c) {
        if (c == ChessColor.Black) {
            return this.blackPlayerMoves;
        } else {
            return this.whitePlayerMoves;
        }
    }
    public synchronized Set<Move> getMovesWithCheck(ChessColor c) {
        if (c == ChessColor.Black) {
            return this.blackPlayerMovesWithCheck;
        } else {
            return this.whitePlayerMovesWithCheck;
        }
    }

    public synchronized Set<Move> getOppositeColorMoves(ChessColor c) {
        if (c == ChessColor.Black) {
            return whitePlayerMoves;
        } else {
            return blackPlayerMoves;
        }
    }
    public synchronized Set<Move> getOppositeColorMovesWithCheck(ChessColor c) {
        if (c == ChessColor.Black) {
            return whitePlayerMovesWithCheck;
        } else {
            return blackPlayerMovesWithCheck;
        }
    }


}

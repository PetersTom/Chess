package Engine;

import GUI.ChessCanvas;
import javafx.util.Pair;
import pieces.ChessColor;
import pieces.ChessPosition;
import pieces.King;
import pieces.Piece;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles all the game objects
 */
public class Handler {
    //needs to be volatile, because both the game loop and window updates can access at the same time.
    volatile private Set<Piece> pieces;
    private ChessCanvas canvas;

    public Handler(Engine e) {
        pieces = Collections.synchronizedSet(new HashSet<>());
        canvas = e.getCanvas();
    }

    public void addPiece(Piece p) {
        pieces.add(p);
    }

    public void removePiece(Piece p) {
        pieces.remove(p);
    }

    public Piece getPiece(ChessPosition position) {
        synchronized(pieces) {
            for (Piece p : pieces) {
                if (p.getPosition().equals(position)) {
                    return p;
                }
            }
            return null;
        }
    }

    public Piece getPiece(int x, int y) {
        return getPiece(new ChessPosition(x, y, canvas));
    }

    /**
     * Get all pieces.
     * @return
     */
    public Set<Piece> getPieces() {
        return this.pieces;
    }

    /**
     * Get all pieces of a specific color
     * @param c
     * @return
     */
    public Set<Piece> getPieces(ChessColor c) {
        synchronized (pieces) {
            return pieces.stream().filter(p -> p.getColor() == c).collect(Collectors.toSet());
        }
    }

    public Set<Piece> getWhitePieces() {
        return getPieces(ChessColor.White);
    }

    public Set<Piece> getBlackPieces() {
        return getPieces(ChessColor.Black);
    }

    public Set<Piece> getOppositeColorPieces(ChessColor c) {
        synchronized (pieces) {
            if (c == ChessColor.White) {
                return getBlackPieces();
            } else {
                return getWhitePieces();
            }
        }
    }

    public King getKing(ChessColor c) {
        if (c == ChessColor.White) {
            return getWhiteKing();
        } else {
            return getBlackKing();
        }
    }

    public King getOppositeColorKing(ChessColor c) {
        if (c == ChessColor.White) {
            return getBlackKing();
        } else {
            return getWhiteKing();
        }
    }

    public King getWhiteKing() {
        synchronized (pieces) {
            Optional<Piece> whiteKing = pieces.stream().filter(p -> p instanceof King).filter(k -> k.getColor() == ChessColor.White).findFirst();
            //return the whiteking, or null if there is none
            return (King) whiteKing.orElse(null);
        }
    }

    public King getBlackKing() {
        synchronized (pieces) {
            Optional<Piece> blackKing = pieces.stream().filter(p -> p instanceof King).filter(k -> k.getColor() == ChessColor.Black).findFirst();
            //return the black king, or null if there is none
            return (King) blackKing.orElse(null);
        }
    }
}

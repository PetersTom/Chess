package Engine;

import GUI.ChessCanvas;
import Players.Move;
import pieces.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all the game objects
 */
public class Handler {
    //needs to be volatile, because both the game loop and window updates can access at the same time.
    volatile private Set<Piece> pieces;
    private boolean whiteTurn = true;

    private ChessCanvas canvas;
    private Engine e;

    //to be updated after every move. To prevent continuous fetching of moves.
    private Set<Move> whitePlayerMoves; //every move of the white player
    private Set<Move> blackPlayerMoves;
    private Set<Move> whitePlayerMovesWithCheck; //every valid move of the white player (that will not result in check)
    private Set<Move> blackPlayerMovesWithCheck;

    private Move lastMove; //used for en-passent

    public Handler(Engine e) {
        this.e = e;
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
    public synchronized Set<Piece> getPieces() {
        return new HashSet<>(this.pieces);
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

    /**
     * Changes the turn. If it was white's Turn, it is now black's turn and the other way arround.
     */
    public void changeTurn() {
        whiteTurn = !whiteTurn;
    }

    /**
     * Returns a new Handler instance. All pieces are copied as well, so that a modification to one of the handler's pieces
     * does not affect the pieces of the other handler.
     * @return
     */
    public synchronized Handler deepCopy() {
        Handler handlerCopy = new Handler(e);
        Set<Piece> piecesCopy = Collections.synchronizedSet(new HashSet<>());
        for (Piece p : pieces) {
            Piece copy = p.copy(handlerCopy);
            piecesCopy.add(copy);
        }
        handlerCopy.setPieces(piecesCopy);
        //update lastmove. first find the new piece on the end position (lastmove is already executed
        if (!lastMove.isExecuted()) {
            throw new IllegalArgumentException("lastMove is not yet executed");
        }
        //find the piece that corresponds to the move
        Piece p = handlerCopy.getPiece(lastMove.getEndPosition());
        handlerCopy.lastMove = lastMove.copy(handlerCopy, p);

        //updateMoves requires lastMove to be set correctly as it is used by pawns figuring out their moves.
        handlerCopy.updateMoves();

//        e.getHandler().copy(handlerCopy);
//        e.getCanvas().requestBoardRepaint();
        return handlerCopy;
    }

    /**
     * copies a handler object into another handler object
     */
    public synchronized void copy(Handler h) {
        this.pieces = h.pieces;
        this.blackPlayerMovesWithCheck = h.blackPlayerMovesWithCheck;
        this.blackPlayerMoves = h.blackPlayerMoves;
        this.whitePlayerMovesWithCheck = h.whitePlayerMovesWithCheck;
        this.whitePlayerMoves = h.whitePlayerMoves;
        this.lastMove = h.lastMove;
    }

    /**
     * A helper method for the deepCopy(). Sets the pieces of the copied handler to the copied pieces
     */
    private synchronized void setPieces(Set<Piece> p) {
        this.pieces = p;
    }

    public synchronized boolean isWhiteToMove() {
        return whiteTurn;
    }

    public boolean blackMated() {
        return getBlackKing().isMated();
    }

    public boolean whiteMated() {
        return getWhiteKing().isMated();
    }

    public boolean isLastMove() {
        return lastMove != null;
    }

    public void undoLastMove() {
        lastMove.undo(this);
    }

    public void setLastMove(Move m) {
        this.lastMove = m;
    }

    public Move getLastMove() {
        return this.lastMove;
    }

    public void execute(Move m) {
        if (!this.pieces.contains(m.getPiece())) { //this handler does not contain the piece.
            Piece p = this.getPiece(m.getPiece().getPosition());
            if (p != null) { //if this handler does contain a piece on that spot, correct the move with the correct piece
                             //I know that this is a dirty fix. The wrong piece has something to do with the handler copying
                             //the last move. I have absolutely no clue what the problem is.
                m = m.copy(this, p);
            } else {
                throw new IllegalArgumentException("this handler does not contain a piece on that position");
            }
        }
        m.execute(this);
    }

    public void undo(Move m) {
        if (!this.pieces.contains(m.getPiece())) { //this handler does not contain the piece.
            Piece p = this.getPiece(m.getPiece().getPosition());
            if (p != null) { //same holds as for the execute method
                m = m.copy(this, p);
            } else {
                throw new IllegalArgumentException("this handler does not contain a piece on that position");
            }
        }
        m.undo(this);
    }

    public void tryMove(Move m) {
        if (!this.pieces.contains(m.getPiece())) throw new IllegalArgumentException("This handler cannot execute this move");
        m.tryMove(this);
    }

    public void unTryMove(Move m) {
        if (!this.pieces.contains(m.getPiece())) throw new IllegalArgumentException("This handler cannot execute this move");
        m.unTryMove(this);
    }
}

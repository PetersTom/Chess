package pieces;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import Engine.Engine;
import Engine.Handler;
import GUI.ChessCanvas;
import Players.Move;

public abstract class Piece {

    private ChessPosition position;

    private ChessColor color;
    int cellWidth;
    protected Handler handler;
    protected ChessCanvas canvas;
    String file = "";
    protected Engine e;
    private Image img;
    private boolean moved; //whether or not this piece has already moved. Used for castling check.
    int pieceValue; //A value for the piece, used in calculating the value of a certain position

    public Piece(ChessPosition p, ChessColor c, int standardCellWidth, Engine e) {
        this.position = p;
        this.color = c;
        this.cellWidth = standardCellWidth;
        this.e = e;
        this.handler = e.getHandler();
        this.canvas = e.getCanvas();
    }

    void setupImg() {
        try {
            URL u = getClass().getClassLoader().getResource(file);
            assert u != null;   //Assume the resource is there, if it is not, exceptions will occur, but that is alright,
                                //because when that happens, the application cannot start anyways.
            img = ImageIO.read(u);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Can't read " + file);
            e.printStackTrace();
        }
    }

    public int getPieceValue() {
        return this.pieceValue;
    }

    public abstract Piece copy();

    public ChessColor getColor() {
        return this.color;
    }

    public ChessPosition getPosition() {
        return this.position;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * Returns all possible moves without checking if there exists a check if the move is played.
     */
    public abstract Set<Move> getMoves();

    /**
     * Returns all possible moves while checking for check if the move would be played.
     */
    public Set<Move> getMovesWithCheck() {
        Set<Move> possibleMoves = getMoves();
        Set<Move> movesWithoutCheck = new HashSet<>();
        King thisKing = handler.getKing(this.getColor());
        for (Move m : possibleMoves) {
            m.tryMove();
            if (!thisKing.isChecked()) {
                movesWithoutCheck.add(m);
            }
            m.unTryMove();
        }
        return movesWithoutCheck;
    }

    public void draw(Graphics g) {
        int drawX = position.getPositionOnCanvas().x;
        int drawY = position.getPositionOnCanvas().y;
        //This is a square and the width is the same as the height, therefore, the code is correct and the warning suppressed.
        //noinspection SuspiciousNameCombination
        g.drawImage(img, drawX, drawY, cellWidth, cellWidth, null);
    }

    public Image getImg() {
        return this.img;
    }

    public void setMoved(boolean m) {
        this.moved = m;
    }

    public boolean hasMoved() {
        return this.moved;
    }

    public void moveTo(ChessPosition p) {
        this.position = p;
    }
}

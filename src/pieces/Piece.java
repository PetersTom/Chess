package pieces;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.*;
import Engine.Engine;
import Engine.Handler;
import GUI.ChessCanvas;
import Players.Move;
import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * A class representing a Piece. It contains information about its color, position and if it has already moved.
 * The images corresponding to the pieces are fetched from the resources once in the constructors of
 * the respective classes.
 * Because it is immutable, pieces do not know their own positions. This is stored in the handler.
 */
@Immutable
public abstract class Piece implements Cloneable {

    private ChessColor color;
    protected Handler handler;
    protected ChessCanvas canvas;
    protected Engine e;
    int pieceValue; //A value for the piece, used in calculating the value of a certain position

    public Piece(ChessColor c, Engine e, Handler h) {
        this.color = c;
        this.e = e;
        this.handler = h;
        this.canvas = e.getCanvas();
    }

    public int getPieceValue() {
        return this.pieceValue;
    }

    public ChessColor getColor() {
        return this.color;
    }

    /**
     * Returns all possible moves without checking if there exists a check if the move is played.
     */
    public abstract Set<Move> getMoves(ChessPosition position);

    /**
     * Returns all possible moves while checking for check if the move would be played.
     */
    public Set<Move> getMovesWithCheck(ChessPosition position) {
        Set<Move> possibleMoves = getMoves(position);
        Set<Move> movesWithoutCheck = new HashSet<>();
        King thisKing = handler.getKing(this.getColor());
        for (Move m : possibleMoves) {
            handler.execute(m, false);
            if (!thisKing.isChecked(handler.getKingPosition(this.getColor()))) {
                movesWithoutCheck.add(m);
            }
            handler.undo(m);
        }
        return movesWithoutCheck;
    }

    public void draw(Graphics g, ChessPosition position) {
        int drawX = position.getPositionOnCanvas().x;
        int drawY = position.getPositionOnCanvas().y;
        //This is a square and the width is the same as the height, therefore, the code is correct and the warning suppressed.
        //noinspection SuspiciousNameCombination
        g.drawImage(getImg(), drawX, drawY, ChessCanvas.cellWidth, ChessCanvas.cellWidth, null);
    }

    public abstract Image getImg();

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * Return a new instance with a different handler.
     */
    public Piece clone(Handler h) {
        try {
            return this.getClass().getDeclaredConstructor(ChessColor.class, Engine.class, Handler.class).newInstance(this.color, this.e, h);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}

package pieces;

import java.awt.*;
import GUI.ChessCanvas;
import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * A chessposition ranging from 1 to 8 with 1,1 in the bottom left
 */
@Immutable
public class ChessPosition extends Point {

    ChessCanvas canvas;
    private static final int CELL_AMOUNT = 8;
    private ChessColor color;

    public ChessPosition(int x, int y, ChessCanvas canvas) {
        super(x, y);
        this.canvas = canvas;
        if ((x+y) % 2 == 0) {
            this.color = ChessColor.Black;
        } else {
            this.color = ChessColor.White;
        }
    }

    public ChessPosition(double graphicalX, double graphicalY, ChessCanvas canvas) {
        this((int)graphicalX / ChessCanvas.cellWidth + 1, CELL_AMOUNT - ((int)graphicalY / ChessCanvas.cellWidth), canvas);
    }

    /**
     * Get the relative position on the canvas
     * @return
     */
    public Point getPositionOnCanvas() {
        int cellWidth = ChessCanvas.cellWidth;
        int graphicalX = (this.x-1) * cellWidth;
        int graphicalY = (CELL_AMOUNT - this.y) * cellWidth;
        return new Point(graphicalX, graphicalY);
    }

    public ChessColor getColor() {
        return this.color;
    }
}

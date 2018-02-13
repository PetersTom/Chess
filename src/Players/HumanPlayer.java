package Players;

import GUI.ChessCanvas;
import pieces.ChessColor;
import pieces.ChessPosition;
import Engine.Engine;
import Engine.Handler;
import pieces.Piece;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HumanPlayer extends Player {

    ChessCanvas canvas;
    Handler handler;
    Piece selected;

    public HumanPlayer(ChessColor c, Engine e) {
        super(c, e);
        canvas = e.getCanvas();
        handler = e.getHandler();
    }

    /**
     * Just wait until the player clickes on a square that makes a move.
     */
    @Override
    public void run() {
        //do nothing. This method is only here because every player should have it.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ChessPosition clicked = new ChessPosition((double)e.getX(), (double)e.getY(), canvas);
        Piece clickedPiece = handler.getPiece(clicked);
        if (selected == null) { //nothing already selected
            if (clickedPiece == null) return;   //not clicked on a piece
            if (clickedPiece.getColor().equals(this.color)) {//piece of correct color
                selected = clickedPiece;
                canvas.setSelectedPiece(selected);
            }
        } else {    //there is a piece selected
            Set<Move> possibleMoves = selected.getMovesWithCheck();
            //There could be multiple possible moves in case of a pawn promotion
            Set<Move> optionalMoves = possibleMoves.stream().filter(m -> m.getEndPosition().equals(clicked)).collect(Collectors.toSet());
            //clicked on a possible move
            Iterator<Move> iterator = optionalMoves.iterator();
            if (iterator.hasNext()) {   //there is at least one move
                Move toReturn = iterator.next();
                if (iterator.hasNext()) { //there is more than one move, so it is a pawn promotion
                    toReturn = askUserForPromotion((Set)optionalMoves);
                }
                move = toReturn;
                selected = null;
                canvas.setSelectedPiece(selected);
            } else { //clicked on a non-move spot, so place back the piece
                selected = null;
                canvas.setSelectedPiece(null);
                return;
            }
        }
    }

    public PawnPromotion askUserForPromotion(Set<PawnPromotion> possiblePromotions) {
        String[] options = {"Queen", "Rook", "Knight", "Bishop"};
        int n = JOptionPane.showOptionDialog(e.getFrame(), "Which piece would you like to promote this pawn in to?", "Pawn Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        String answer = options[n];
        Optional<PawnPromotion> promotionChosen = possiblePromotions.stream().filter(m -> m.getPromotionType().equals(answer)).findAny();
        if (promotionChosen.isPresent()) {
            return promotionChosen.get();
        } else {
            throw new IllegalArgumentException("The correct promotion is not in the argument set");
        }
    }
}

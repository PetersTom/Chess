package Players.AI;

import Engine.Engine;
import Players.Move;
import Players.Player;
import pieces.ChessColor;

import java.util.Random;
import java.util.Set;

public class RandomPlayer extends Player {

    Random r;

    public RandomPlayer(ChessColor color, Engine e) {
        super(color, e);
        r = new Random();
    }

    @Override
    public void run() {
        Set<Move> possibleMoves = e.getMovesWithCheck(getColor());
        int size = possibleMoves.size();
        int number = r.nextInt(size);
        int i = 0;
        for (Move m : possibleMoves) {
            if (i == number) {
                move = m;
            }
            i++;
        }
    }
}

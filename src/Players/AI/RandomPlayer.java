package Players.AI;

import Engine.*;
import Players.Move;
import Players.Player;
import pieces.ChessColor;

import java.util.Random;
import java.util.Set;

public class RandomPlayer extends Player {

    public RandomPlayer(ChessColor color, Engine e) {
        super(color, e);
    }

    @Override
    public void run() {
        move = getRandomValidMove(handler);
    }
}

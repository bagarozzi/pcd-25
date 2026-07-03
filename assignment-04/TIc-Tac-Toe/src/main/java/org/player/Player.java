package org.player;

import org.utilities.Pair;

public interface Player{

    void joinGame();
    void makeMove(Pair pos);
    Character getSign();
    void clearMatch();
}

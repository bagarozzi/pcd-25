package org;


public interface Player{

    void joinGame();
    void makeMove(Pair pos);
    Character getSign();
    void clearMatch();
}

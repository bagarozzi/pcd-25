package it.unibo.assignment01.model;

import java.util.List;

import it.unibo.assignment01.model.ball.Ball;

public interface Board { 

    public List<Ball> getBalls();
    
    public Ball getPlayerBall();

    public Ball getEnemyBall();

    public int getHumanScore();

    public int getBotScore();
    
    public  Boundary getBounds();

    public void checkHole(Ball b);
    
    public void resolveCollision(Ball a, Ball b);

    public List<Ball> getAllBall();

    public boolean emptyBoard();

    public boolean endedGame();

    public String getWinner();
}

package it.unibo.assignment01.model;

import java.util.List;

public interface Board { 

    public List<Ball> getBalls();
    
    public Ball getPlayerBall();

    public Ball getEnemyBall();

    public int getHumanScore();

    public int getBotScore();
    
    public  Boundary getBounds();

    public void checkHole(Ball b);
    
    public List<CollisionPair> detectCollisions();

    public void resolveCollision(CollisionPair collision);
}

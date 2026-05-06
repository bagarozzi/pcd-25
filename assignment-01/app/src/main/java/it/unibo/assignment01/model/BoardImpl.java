package it.unibo.assignment01.model;

import java.util.ArrayList;
import java.util.List;

public class BoardImpl implements Board {
    private static final double Y1 = 1.0;
    private static final double X1 = 1.5;
    private static final double Y0 = -1.0;
    private static final double X0 = -1.5;
    private static final double HOLE_RADIUS = 0.2;

    private int playerScore = 0;
    private int enemyScore = 0;
    private Boundary bounds;
    private List<Ball> balls;
    private Ball playerBall;
    private Ball enemyBall;
    private Position playerHoles = new Position(X0,Y0);
    private Position enemyHoles = new Position(X0,Y1);
    private CollisionDetector collisionDetector;
    

    public BoardImpl(List<Ball> balls, CollisionDetector collisionDetector) {
        this.bounds = new Boundary(X0,Y0,X1,Y1);
        this.balls = balls;
        this.collisionDetector = collisionDetector;
        this.playerBall = new BallImpl(new Position(-0.5, -0.5), new Speed(0, 0), 0.05, 0.07);
        enemyBall = new EnemyBall(new Position(0.5, -0.5), new Speed(0, 0), 0.05, 0.07);
    }


    @Override
    public List<Ball> getBalls() {
        return balls;
    }

    @Override
    public Ball getPlayerBall() {
        return playerBall;
    }

    @Override
    public Ball getEnemyBall() {
        return enemyBall;
    }

    @Override
    public Boundary getBounds() {
        return bounds;
    }
    
    @Override
    public void checkHole(Ball b) {
        if (b.getPos().dist(playerHoles) < HOLE_RADIUS) {
            playerScore++;
            balls.remove(b);
        } else if (b.getPos().dist(enemyHoles) < HOLE_RADIUS) {
            enemyScore++;
            balls.remove(b);//TODO check if this is correct
        }
    }

    @Override
    public List<CollisionPair> detectCollisions(){
        List<Ball> allBalls = balls;
        allBalls.add(playerBall);
        return collisionDetector.detectCollisions(allBalls);

    }

    @Override
    public void resolveCollision(CollisionPair collision){
        collisionDetector.resolveCollision(collision);
    }


    @Override
    public int getHumanScore() {
        return playerScore;
    }


    @Override
    public int getBotScore() {
        return enemyScore;
    }

}

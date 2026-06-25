package it.unibo.assignment01.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import it.unibo.assignment01.model.ball.Ball;

public class TestBoard implements Board {
    private static final double Y1 = 1.0;
    private static final double X1 = 1.5;
    private static final double Y0 = -1.0;
    private static final double X0 = -1.5;

    public static final double HOLE_RADIUS = 0.2;

    private int playerScore = 0;
    private int enemyScore = 0;
    private Boundary bounds;
    private List<Ball> balls;
    private Ball playerBall;
    private Ball enemyBall;
    private List<Ball> allBalls;
    private Position playerHoles = new Position(X0, Y1);
    private Position enemyHoles = new Position(X1, Y1);
    private CollisionDetector collisionDetector;
    private boolean gameEnded = false;
    private String winner;

    public TestBoard(List<Ball> balls, CollisionDetector collisionDetector) {
        this.bounds = new Boundary(X0, Y0, X1, Y1);
        this.balls = new CopyOnWriteArrayList<>(balls);
        this.collisionDetector = collisionDetector;
        allBalls = new CopyOnWriteArrayList<>(balls);
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
        if(balls.contains(b)){
            if (inTheHole(b, playerHoles)) {
                playerScore++;
                b.setPos(new Position(-100, -100));
                balls.remove(b);
            } else if (inTheHole(b, enemyHoles)) {
                enemyScore++;
                balls.remove(b);
            }
        }else if(b.equals(playerBall)){
            if (inTheHole(b, enemyHoles) || inTheHole(b, playerHoles)) {
                endGame();
                this.winner = "Enemy is the winner!";
            }
        }else if(b.equals(enemyBall)){
            if (inTheHole(b, enemyHoles) || inTheHole(b, playerHoles)) {
                endGame();
                this.winner = "You are the winner!";
            }
        }
    }

    private boolean inTheHole(Ball b, Position hole) {
        return b.getPos().dist(hole) < HOLE_RADIUS;
    }

    @Override
    public void resolveCollision(Ball a, Ball b) {
        collisionDetector.resolveCollision(a, b);
    }

    @Override
    public int getHumanScore() {
        return playerScore;
    }

    @Override
    public int getBotScore() {
        return enemyScore;
    }

    @Override
    public List<Ball> getAllBall() {
        return allBalls;
    }

    @Override
    public boolean emptyBoard() {
        if(balls.isEmpty()){
            winner = playerScore > enemyScore ? "You are the winner!" : "Enemy is the winner!";
            return true;
        }
        return false;
    }

    private void endGame() {
        this.gameEnded = true;
    }

    @Override
    public boolean endedGame() {
        return emptyBoard() || gameEnded;
    }

    @Override
    public String getWinner() {
        return winner;
    }
}

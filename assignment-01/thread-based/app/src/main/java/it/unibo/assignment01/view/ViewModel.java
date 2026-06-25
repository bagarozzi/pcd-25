package it.unibo.assignment01.view;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.ball.Ball;

import java.util.List;

public class ViewModel {

    // Posizioni delle palline
    private List<Ball> smallBalls;
    private Ball humanBall;
    private Ball botBall;

    // Punteggi
    private int humanScore;
    private int botScore;

    public ViewModel(final Board board) {
        this.smallBalls = board.getBalls();
        this.humanScore = board.getHumanScore();
        this.botScore = board.getBotScore();
        this.humanBall = board.getPlayerBall();
        this.botBall = board.getEnemyBall();
    }

    public void update(final Board board) {
        this.smallBalls = board.getBalls();
        this.humanScore = board.getHumanScore();
        this.botScore = board.getBotScore();
        this.humanBall = board.getPlayerBall();
        this.botBall = board.getEnemyBall();
    }

    // --- GETTER ---

    public List<Ball> getSmallBalls() {
        return smallBalls;
    }

    public Ball getHumanBall() {
        return humanBall;
    }

    public Ball getBotBall() {
        return botBall;
    }

    public int getHumanScore() {
        return humanScore;
    }

    public int getBotScore() {
        return botScore;
    }

    // --- SETTER ---

    public void setSmallBalls(List<Ball> smallBalls) {
        this.smallBalls = smallBalls;
    }

    public void setHumanBall(Ball humanBall) {
        this.humanBall = humanBall;
    }

    public void setBotBall(Ball botBall) {
        this.botBall = botBall;
    }

    public void setHumanScore(int humanScore) {
        this.humanScore = humanScore;
    }

    public void setBotScore(int botScore) {
        this.botScore = botScore;
    }
}
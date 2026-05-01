package it.unibo.assignment01.view;

import it.unibo.assignment01.model.Position;
import java.util.ArrayList;
import java.util.List;

public class ViewModel {

    // Posizioni delle palline
    private List<Position> smallBalls;
    private Position humanBall;
    private Position botBall;

    // Punteggi
    private int humanScore;
    private int botScore;

    public ViewModel() {
        this.smallBalls = new ArrayList<>();
        this.humanScore = 0;
        this.botScore = 0;
    }

    // --- GETTER ---

    public List<Position> getSmallBalls() {
        return smallBalls;
    }

    public Position getHumanBall() {
        return humanBall;
    }

    public Position getBotBall() {
        return botBall;
    }

    public int getHumanScore() {
        return humanScore;
    }

    public int getBotScore() {
        return botScore;
    }

    // --- SETTER ---

    public void setSmallBalls(List<Position> smallBalls) {
        this.smallBalls = smallBalls;
    }

    public void setHumanBall(Position humanBall) {
        this.humanBall = humanBall;
    }

    public void setBotBall(Position botBall) {
        this.botBall = botBall;
    }

    public void setHumanScore(int humanScore) {
        this.humanScore = humanScore;
    }

    public void setBotScore(int botScore) {
        this.botScore = botScore;
    }
}
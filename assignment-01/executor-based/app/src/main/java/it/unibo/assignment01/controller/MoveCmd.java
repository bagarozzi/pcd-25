package it.unibo.assignment01.controller;

import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.ball.Ball;

/**
 * Comando inviato dalla View al Controller per applicare
 * un impulso alla pallina del giocatore umano.
 */
public class MoveCmd implements Cmd {

    private final static double IMPULSE = 0.4;

    private final double dx;
    private final double dy;

    public MoveCmd(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    @Override
    public void execute(Ball ball) {
        ball.kick(ball.getVel().sum(new Speed(dx, dy)));
    }

    public static MoveCmd UP_CMD() {
        return new MoveCmd(0, IMPULSE);
    }

    public static MoveCmd DOWN_CMD() {
        return new MoveCmd(0, -IMPULSE);
    }

    public static MoveCmd LEFT_CMD() {
        return new MoveCmd(-IMPULSE, 0);
    }

    public static MoveCmd RIGHT_CMD() {
        return new MoveCmd(IMPULSE, 0);
    }
}
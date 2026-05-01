package it.unibo.assignment01.controller;

/**
 * Comando inviato dalla View al Controller per applicare
 * un impulso alla pallina del giocatore umano.
 */
public class MoveCmd implements Cmd {

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
}
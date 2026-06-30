package it.unibo.assignment01.model;

import it.unibo.assignment01.model.ball.Ball;

public interface CollisionDetector {

    public void resolveCollision(Ball a, Ball b);

}

package it.unibo.assignment01.worker;

import it.unibo.assignment01.controller.Barrier;

/**
 * A BallWorker is a thread that computes, in the following order, the movements
 * and the collisions of a group of balls.
 * After each phase each thread waits for the others to finish through a Barrier.
 */
public class BallWorker extends Thread {
    
    private final Barrier barrier; 

    public BallWorker(final Barrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            computeMovements();
            barrier.hitAndWait();
            computeCollisions();
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void computeCollisions() {
        // TODO: compute collisions
    }

    private void computeMovements() {
        // TODO: compute movements
    }
}
package it.unibo.assignment01.worker;

import it.unibo.assignment01.controller.Barrier;

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
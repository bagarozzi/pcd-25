package it.unibo.assignment01.worker;

import java.util.List;

import it.unibo.assignment01.controller.Barrier;
import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.util.BoundedBuffer;

/**
 * A BallWorker is a thread that computes, in the following order, the movements
 * and the collisions of a group of balls.
 * After each phase each thread waits for the others to finish through a Barrier.
 */
public class BallWorker extends Thread {
    
    private final BoundedBuffer<Runnable> queueTask; 
    private final Barrier barrier;
    private final List<Ball> balls;

    public BallWorker(final BoundedBuffer<Runnable> queueTask, final List<Ball> balls, Barrier barrier) {
        this.queueTask = queueTask;
        this.barrier = barrier;
        this.balls = balls;
    }

    @Override
    public void run() {
        try {
            queueTask.get().run();
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
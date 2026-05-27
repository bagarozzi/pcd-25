package it.unibo.assignment01.worker;

import java.util.concurrent.CountDownLatch;

import it.unibo.assignment01.controller.Barrier;
import it.unibo.assignment01.util.BoundedBuffer;

/**
 * A BallWorker is a thread that computes, in the following order, the movements
 * and the collisions of a group of balls.
 * After each phase each thread waits for the others to finish through a Barrier.
 */
public class BallWorker extends Thread {
    
    private final BoundedBuffer<Runnable> queueTask; 

    public BallWorker(final BoundedBuffer<Runnable> queueTask) {
        this.queueTask = queueTask;
    }

    @Override
    public void run() {
        while(true) {
            try {
                queueTask.get().run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
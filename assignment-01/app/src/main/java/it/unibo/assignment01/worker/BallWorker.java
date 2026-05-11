package it.unibo.assignment01.worker;

import java.util.List;
import java.util.function.Consumer;

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
    private final Barrier collideBarrier;

    public BallWorker(final BoundedBuffer<Runnable> queueTask, final Barrier barrier, Barrier collideBarrier) {
        this.queueTask = queueTask;
        this.barrier = barrier;
        this.collideBarrier = collideBarrier;
    }

    @Override
    public void run() {
        System.out.println("Worker " + Thread.currentThread().getName() + " created");
        while(true) {
            try {
                queueTask.get().run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
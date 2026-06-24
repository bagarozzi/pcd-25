package it.unibo.assignment01.worker;


import it.unibo.assignment01.util.SynchCell;

/**
 * A BallWorker is a thread that computes, in the following order, the movements
 * and the collisions of a group of balls.
 * After each phase each thread waits for the others to finish through a Barrier.
 */
public class BallWorker extends Thread {
    
    private final SynchCell<Runnable> queueTask; 

    public BallWorker(final SynchCell<Runnable> queueTask) {
        this.queueTask = queueTask;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                queueTask.get().run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
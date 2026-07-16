package it.unibo.assignment01.util;


public class Barrier {
    private int numPartecipants;
    private int numArrived;
    private boolean everyoneArrived;

    public Barrier(int numPartecipants) {
        this.numPartecipants = numPartecipants;
        numArrived = 0;
        everyoneArrived = false;
    }

    public synchronized void hitAndWait() throws InterruptedException {
        numArrived++;
        if (numArrived == numPartecipants) {
            everyoneArrived = true;
            notifyAll();
        } else {
            while (!everyoneArrived) {
                wait();
            }
        }

        numArrived--;
        if (numArrived == 0) {
            everyoneArrived = false; 
        }
    }

}

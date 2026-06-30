package it.unibo.assignment01.util;


public class DoubleSidedBarrier {

    private final int workerNum;
    private final int masterNum;
    private int workerArrived = 0;
    private int masterArrived = 0;
    private Turn turn;

    public enum Turn {
        MASTER, WORKERS;
    }

    public DoubleSidedBarrier(int workerNum) {
        this.workerNum = workerNum;
        this.masterNum = 1;
    }

    public DoubleSidedBarrier(int workersNum, int masterNum) {
        this.workerNum = workersNum;
        this.masterNum = masterNum;
    }

    public synchronized void workerHitAndWait() throws InterruptedException {
        if (turn == Turn.WORKERS) {            
            workerArrived++;
            if (workerArrived == workerNum) {
                notifyAll();
            } else {
                while (workerArrived < masterNum) {
                    wait();
                }
            }

            workerArrived--;
            if (workerArrived == 0) {
                turn = Turn.MASTER;
            }
        }
    }

    public synchronized void masterHitAndWait() throws InterruptedException {
        if (turn == Turn.MASTER) {
            masterArrived++;
            if (masterArrived == masterNum) {
                notifyAll();
            } else {
                while (masterArrived < workerNum) {
                    wait();
                }
            }

            masterArrived--;
            if (masterArrived == 0) {
                turn = Turn.WORKERS;
            }
        }
    }

}

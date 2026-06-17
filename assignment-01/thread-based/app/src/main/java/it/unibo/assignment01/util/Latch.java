package it.unibo.assignment01.util;

public class Latch {

    private int count;

    public Latch(int n) {
        count = n;
    }

    public synchronized void countDown() {
        count--;
        if(count == 0) {
            notifyAll();
        }
    }
    
    // Add this missing method
    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait(); 
        }
    }
}

package jpftesting.util;

public class Latch {

    private int count;
    private final int number;

    public Latch(int n) {
        number = n;
        refresh();
    }

    public synchronized void countDown() {
        count--;
        if(count == 0) {
            notifyAll();
        }
    }

    public synchronized void refresh() {
        count = number;
    }
    
    // Add this missing method
    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait(); 
        }
    }
}

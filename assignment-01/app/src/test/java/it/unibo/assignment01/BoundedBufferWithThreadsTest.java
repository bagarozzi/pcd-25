package it.unibo.assignment01;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.unibo.assignment01.util.BoundedBufferImpl;

public class BoundedBufferWithThreadsTest {

    private final int THREADS_NUM = 4;
    private BoundedBufferImpl<String> buffer;

    @Test
    public void testBoundedBufferWithThreads() {
        buffer = new BoundedBufferImpl<>(10);
        for(int i = 0; i < THREADS_NUM; i++) {
            new Thread(this::consumer).start();
        }
        for(int i = 0; i < THREADS_NUM; i++) {
            try {
                Thread.sleep(500);
                buffer.put("item number " + i);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void consumer() {
        try {
            System.out.println(Thread.currentThread().getName() + " started");
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + " enters get() ");
            String item = buffer.get();
            System.out.println(Thread.currentThread().getName() + " leaves get() ");
            System.out.println(Thread.currentThread().getName() + " consumed: " + item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

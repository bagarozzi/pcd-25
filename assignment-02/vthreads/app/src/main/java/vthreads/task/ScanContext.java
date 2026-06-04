package vthreads.task;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import vthreads.lib.Histogram;

/** 
 * Contains all the information regarding a scan. From here the user accesses statistics
 * and controls the scan. 
 */
public class ScanContext {

    public final Histogram histogram;
    private final Path initialDirectory;
    private final ExecutorService executor;
    private final AtomicInteger currentTasksNumber = new AtomicInteger(0);
    private final CountDownLatch latch = new CountDownLatch(1);
    private Boolean stopRequested = false;
    
    public ScanContext(Path initialDirectory, int numBands, long maxFileSize) {
        this.initialDirectory = initialDirectory;
        this.histogram = new Histogram(numBands, maxFileSize);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void startScan() {
        executor.submit(new DirectoryTask(executor, initialDirectory, latch, currentTasksNumber, histogram));
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public void waitForScan() {
        if(!stopRequested) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopScan();
        }
    }

    public Histogram stopScan() {
        stopRequested = true;
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return histogram;
    }
}

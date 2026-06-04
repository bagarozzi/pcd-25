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
    
    /**
     * Creates a new Scan Context with a starting directory, togheter with a histogram
     * with the specified bands and size.
     * @param initialDirectory the directory from which to start the scan.
     * @param numBands the number of bands that has to have the histogram.
     * @param maxFileSize the maximum file size the histogram has to track.
     */
    public ScanContext(Path initialDirectory, int numBands, long maxFileSize) {
        this.initialDirectory = initialDirectory;
        this.histogram = new Histogram(numBands, maxFileSize);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /** 
     * Start the scan on the directory specified upon creation of this object.
     */
    public void startScan() {
        executor.submit(new DirectoryTask(executor, initialDirectory, latch, currentTasksNumber, histogram));
    }

    /** 
     * Returns the histogram created by the scan up to the time of calling this method.
    */
    public Histogram getHistogram() {
        return histogram;
    }

    /**
     * Synchronously waits for the finishing of the scan.
     */
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

    /** 
     * Stops the scan, it may require some time.
     */
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

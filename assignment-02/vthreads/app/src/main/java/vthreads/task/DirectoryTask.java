package vthreads.task;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import vthreads.lib.Histogram;

/**
 * A task run by a single thread. This task takes a directory and lists its content,
 * updating the histogram and signaling it's end.
 */
public class DirectoryTask implements Runnable {

    private final ExecutorService executor;
    private final Path directoryPath;
    private final AtomicInteger taskCounter;
    private final Histogram histogram;
    private final CountDownLatch latch;


    public DirectoryTask(final ExecutorService executor,
        final Path directoryPath,
        final CountDownLatch latch,
        final AtomicInteger taskCounter,
        final Histogram histogram) {
        this.executor = executor;
        this.directoryPath = directoryPath;
        this.taskCounter = taskCounter;
        this.histogram = histogram;
        this.latch = latch;
        taskCounter.incrementAndGet();
    }
    
    @Override
    public void run() {

        histogram.updateDirectory();

        try {
            File dir = new File(directoryPath.toFile().getAbsolutePath());
            
            Stream.of(dir.listFiles())
            .forEach(file -> {
                if(file.isDirectory()) {
                    executor.submit(new DirectoryTask(executor, file.toPath(), latch, taskCounter, histogram));
                }
                else if(file.isFile()) {
                    histogram.addFile(file.length());
                }
            });
        } finally {
            if(taskCounter.decrementAndGet() == 0) {
                latch.countDown();
            }
        }

    }
}

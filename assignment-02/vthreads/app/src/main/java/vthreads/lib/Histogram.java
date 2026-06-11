package vthreads.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import vthreads.util.Pair;

/** 
 * Thread-safe histogram for file size distribution. It maintains a count of files in specified size bands and the total number of directories scanned.
 */
public class Histogram {

    private final int numBands;
    private final long maxFileSize;
    private AtomicInteger dirCount = new AtomicInteger(0);
    private final AtomicIntegerArray bands;

    public Histogram(int numBands, long maxFileSize) {
        this.numBands = numBands;
        this.maxFileSize = maxFileSize;
        this.bands = new AtomicIntegerArray(numBands + 1);
    }

    /**
     * Adds the passed file size to the appropriate histogram band.
     * @param fileSize
     */
    public void addFile(final long fileSize) {
        if (fileSize > maxFileSize) {
            bands.addAndGet(numBands, 1);
        }
        else {
            int bandIndex = (int) ((fileSize * numBands) / maxFileSize);
            bands.addAndGet(bandIndex, 1);
        }
    }

    /** 
     * Updates the directory count.
     */
    public void updateDirectory() {
        dirCount.addAndGet(1);
    }

    public List<Entry<Pair<Long>, Integer>> getDistribution() {
        List<Entry<Pair<Long>, Integer>> distribution = new ArrayList<>();
        long step = (long) (maxFileSize / numBands);
        long floor = 0;
        long ceiling = step;
        for(int i = 0; i <= numBands; i++) {
            distribution.add(Map.entry(new Pair<>(floor, ceiling), bands.get(i)));
            floor = ceiling;
            ceiling += step;
            if(ceiling > maxFileSize) {
                ceiling = Long.MAX_VALUE;
            }
        }
        return distribution;
    }

    public int getDirectoryCount() {
        return dirCount.get();
    }

    public int getTotalFiles() {
        int sum = 0;
        for(int i = 0; i <= numBands; i++) {
            sum += bands.get(i);
        }
        return sum;
    }
}

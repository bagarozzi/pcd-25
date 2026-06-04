package vthreads.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vthreads.util.Pair;

/** 
 * Thread-safe histogram for file size distribution. It maintains a count of files in specified size bands and the total number of directories scanned.
 */
public class Histogram {

    private final int numBands;
    private final long maxFileSize;
    private int dirCount = 0;
    private final List<Integer> bands;

    public Histogram(int numBands, long maxFileSize) {
        this.numBands = numBands;
        this.maxFileSize = maxFileSize;
        this.bands = new ArrayList<>();
        for(int i = 0; i <= numBands; i++) {
            bands.add(0);
        }
    }

    /**
     * Adds the passed file size to the appropriate histogram band.
     * @param fileSize
     */
    public synchronized void addFile(final long fileSize) {
        if (fileSize > maxFileSize) {
            bands.set(numBands, bands.get(numBands) + 1);
        }
        else {
            int bandIndex = (int) ((fileSize * numBands) / maxFileSize);
            bands.set(bandIndex, bands.get(bandIndex) + 1);
        }
    }

    /** 
     * Updates the directory count.
     */
    public synchronized void updateDirectory() {
        dirCount++;
    }

    public List<Entry<Pair<Long>, Integer>> getDistribution() {
        List<Entry<Pair<Long>, Integer>> distribution = new ArrayList<>();
        long step = (long) (maxFileSize / numBands);
        long floor = 0;
        long ceiling = step;
        for(int count : bands) {
            distribution.add(Map.entry(new Pair<>(floor, ceiling), count));
            floor = ceiling;
            ceiling += step;
            if(ceiling > maxFileSize) {
                ceiling = Long.MAX_VALUE;
            }
        }
        return distribution;
    }

    public int getDirectoryCount() {
        return dirCount;
    }

    public int getTotalFiles() {
        return bands.stream().mapToInt(Integer::intValue).sum();
    }
}

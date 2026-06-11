package vthreads;

import java.nio.file.Path;
import java.util.Map.Entry;

import vthreads.lib.Histogram;
import vthreads.task.ScanContext;
import vthreads.util.Pair;

public class FsStat {
    
    private int numBands;
    private long maxFileSize;
    private Path initialDirectory;
    private final ScanContext scanContext;

    public FsStat(String[] args) {
        parseArgs(args);
        printGreeting();
        this.scanContext = new ScanContext(initialDirectory, numBands, maxFileSize);
    }

    public void run() {
        long t0 = System.currentTimeMillis();
        scanContext.startScan();
        scanContext.waitForScan();
        long t1 = System.currentTimeMillis();
        Histogram hist = scanContext.getHistogram();
        System.out.println("Scanned " + hist.getTotalFiles() + " files in " + hist.getDirectoryCount() + " directories.");
        System.out.println("File size distribution:");
        for(Entry<Pair<Long>, Integer> entry : hist.getDistribution()) {
            System.out.println("Band [" + formatBytes(entry.getKey().floor()) + " - " + formatBytes(entry.getKey().ceiling()) + "]: " + entry.getValue() + " files");
        }
        System.out.println("\nTime taken:" + (t1 - t0));
    }

    private void parseArgs(String[] args) {
        if(args.length < 3) {
            printUsage();
            System.exit(2);
        }
        else {
            this.initialDirectory = Path.of(args[0]).toAbsolutePath();
            this.maxFileSize = Long.parseLong(args[1]);
            this.numBands = Integer.parseInt(args[2]);
        }
    }

    private void printGreeting() {
        System.out.println("fsstat -- file system statistics");
        System.out.println("Directory to scan: " + initialDirectory);
    }

    private void printUsage() {
        System.err.println("fsstat -- file system statistics");
        System.err.println("Arguments: <directory> <maxFileSize> <numBands> [--interactive]");
        System.err.println("  directory    - Path to scan (absolute or relative)");
        System.err.println("  maxFileSize  - Maximum file size for band distribution (in bytes)");
        System.err.println("  numBands     - Number of size bands to create");
        System.err.println("\n /home 1000000 10");
    }

    private String formatBytes(long bytes) {
        if (bytes == 0) return "0 B";
        if (bytes == Long.MAX_VALUE) return "inf B";
        final long k = 1024;
        final String[] sizes = {"B", "KB", "MB", "GB", "TB"};
        int i = (int) Math.floor(Math.log(bytes) / Math.log(k));
        return String.format("%.2f %s", (double) bytes / Math.pow(k, i), sizes[i]);
    }
}

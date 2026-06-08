package rxFSStat;

public class BandStats {

    private final int band;
    private final long count;
    private final long lowerBound;
    private final long upperBound;

    public BandStats(int bucket, long count, long lowerBound, long upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.band = bucket;
        this.count = count;
    }

    public int getBand() {
        return band;
    }

    public long getCount() {
        return count;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "Band " + band + ": [" + lowerBound + " - " + upperBound + "] " + count;
    }

}

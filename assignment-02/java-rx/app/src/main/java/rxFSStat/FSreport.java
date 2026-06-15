package rxFSStat;


import io.reactivex.rxjava3.core.Observable;

public class FSreport {
    private Observable<BandStats> bandStats;


    public FSreport(Observable<BandStats> bandStats) {
        this.bandStats = bandStats;
    }

    public long getTotalFiles() {
        return bandStats.reduce(0L, (acc, band) -> acc + band.getCount()).blockingGet();
    }

    public Observable<BandStats> getBandStats() {
        return bandStats;
    }
}

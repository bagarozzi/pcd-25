package rxFSStat;


import java.util.ArrayList;
import java.util.List;

public class FSreport {
    private List<BandStats> bandStats;
    private long count;


    public FSreport() {
        this.bandStats = new ArrayList<BandStats>();
        this.count = 0;
    }

    public long getTotalFiles() {
        return count;
    }

    public List<BandStats> getBandStats() {
        return bandStats;
    }

    public void addBand(BandStats bandStat) {
        bandStats.add(bandStat);
    }

    public void addCount(long count){
        this.count += count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total files: ").append(count).append("\n");
        for (BandStats bandStat : bandStats) {
            sb.append(bandStat.toString()).append("\n");
        }
        return sb.toString();
    }
}

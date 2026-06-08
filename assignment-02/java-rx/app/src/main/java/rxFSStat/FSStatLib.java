package rxFSStat;

import java.io.File;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FSStatLib {

    public static Observable<BandStats> getFSReport(String d, int MaxFS, int NB) {
        Observable<File> files = Observable.create(emitter -> {
            scanDir(new File(d), emitter);
            emitter.onComplete();
        });

        Observable<BandStats> computed = files.subscribeOn(Schedulers.io()).map(f -> f.length())
                .map(size -> getBand(size, MaxFS, NB))
                .groupBy(bucket -> bucket)
                .flatMapSingle(group -> group.count().map(count -> {
                    long bandSize = MaxFS / NB;
                    long lowerBound = group.getKey() * bandSize;
                    long upperBound = group.getKey() == NB ? Long.MAX_VALUE : (group.getKey() + 1) * bandSize;
                    return new BandStats(group.getKey(), count, lowerBound, upperBound);
                }));

        Observable<BandStats> allBands = Observable.range(0, NB + 1)
                .map(i -> {
                    long bandSize = MaxFS / NB;

                    long lower = i * bandSize;
                    long upper = (i == NB)
                            ? Long.MAX_VALUE
                            : (i + 1) * bandSize;

                    return new BandStats(i, 0, lower, upper);
                });

        Observable<BandStats> result = allBands
                .flatMap(band -> computed
                        .filter(c -> c.getBand() == band.getBand())
                        .defaultIfEmpty(band));
        
        result.subscribe(System.out::println);
        return result;

    }

    private static void scanDir(File dir, ObservableEmitter<File> emitter) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f, emitter);
            } else {
                emitter.onNext(f);
            }
        }
    }

    private static int getBand(long size, long maxFS, int NB) {
        if (size > maxFS) {
            return NB; // bucket extra (files > maxFS)
        }
        long bandSize = maxFS / NB;
        return (int) (size / bandSize);
    }

}
package co.uk.sentinelweb.lantv.net.smb;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by robert on 16/02/2017.
 */

public class LocalFileListInteractor {

    public Observable<List<Media>> getListObservable(final File directory) {
        return Observable.defer(new Func0<Observable<List<Media>>>() {
            @Override
            public Observable<List<Media>> call() {
                return Observable.just(getList(directory));
            }
        });
    }

    public List<Media> getList(final File directory) {
        final List<Media> list = new ArrayList<>();
        if (directory.exists() && directory.canRead()) {
            final File[] files = directory.listFiles();
            for (final File f : files) {
                if (f.isFile()) {
                    list.add(Media.create("file://"+f.getAbsolutePath(),
                            f.getName(),
                            f.length(),
                            new Date(f.lastModified()),
                            false,
                            false,
                            false,
                            false,
                            f.isDirectory(),
                            f.isFile()));
                }
            }
        }
        return list;
    }
}

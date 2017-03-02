package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import co.uk.sentinelweb.lantv.net.smb.SmbFileReadInteractor;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by robert on 19/02/2017.
 */
public class CacheFileController {

    public static final String TAG = CacheFileController.class.getSimpleName();
    SmbFileReadInteractor _smbFileReadInteractor;
    //SmbShareListInteractor _smbShareListInteractor;
    private Subscription _subscription;
    private InputStream _inputStream;

    public CacheFileController(final SmbFileReadInteractor smbFileReadInteractor
                               /*, final SmbShareListInteractor smbShareListInteractor*/) {
        _smbFileReadInteractor = smbFileReadInteractor;
        //_smbShareListInteractor = smbShareListInteractor;
    }

    public Observable<Long> downloadInBackground(final Context c, final SmbLocation f) {
        final PublishSubject<Long> progressPublish = PublishSubject.create();
        FileUtils.clearBufferedFile(c);
        final File bufferFile = FileUtils.getBufferFile(c, f.getFileName());
        _subscription =
                _smbFileReadInteractor
                        .openFileObservable(f)
                        .observeOn(Schedulers.io())
                        .map((inputStream) -> this._inputStream = inputStream)
                        .doOnNext(inputStream -> FileUtils.copyFileFromStream(bufferFile, inputStream, progressPublish))
                        .doOnUnsubscribe(()->FileUtils.closeStream(_inputStream))
                        .subscribeOn(Schedulers.io())
                        .subscribe((inputStream) -> {
                                },
                                (throwable) -> Log.d(TAG, "Error caching file:" + f.getFileName(), throwable),
                                () -> {
                                });
        return progressPublish;
    }

    public void unsubscribe() {
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
    }
}

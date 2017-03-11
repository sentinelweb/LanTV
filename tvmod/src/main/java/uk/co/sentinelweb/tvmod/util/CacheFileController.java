package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
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
    private Subscription _subscription;
    private InputStream _inputStream;
    private SmbLocation _f;

    public CacheFileController(final SmbFileReadInteractor smbFileReadInteractor) {
        _smbFileReadInteractor = smbFileReadInteractor;
    }

    public Observable<Long> downloadInBackground(final Context c, final SmbLocation f) {
        closeStream();
        final PublishSubject<Long> progressPublish = PublishSubject.create();
        FileUtils.clearBufferedFile(c);
        _f = f;
        final File bufferFile = FileUtils.getBufferFile(c, _f.getFileName());
        _subscription =
                _smbFileReadInteractor
                        .openFileObservable(f)
                        .observeOn(Schedulers.io())
                        .map((inputStream) -> this._inputStream = inputStream)
                        .doOnUnsubscribe(() -> FileUtils.closeStream(_inputStream))
                        .subscribeOn(Schedulers.io())
                        .subscribe((inputStream) -> {
                                    FileUtils.copyFileFromStream(bufferFile, inputStream, progressPublish);
                                },
                                (throwable) -> {
                                    Log.d(TAG, "Error caching file:" + f.getFileName(), throwable);
                                    closeStream();
                                });
        return progressPublish;
    }

    public void unsubscribe() {
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
    }

    public void closeStream() {
        if (_inputStream != null) {
            try {
                _inputStream.close();
            } catch (final IOException e) {
                Log.d(TAG, "Error closing file:" + _f.getFileName(), e);
            }
        }
    }
}

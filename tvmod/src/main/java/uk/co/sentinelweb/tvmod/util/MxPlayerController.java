package uk.co.sentinelweb.tvmod.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;
import rx.Observable;
import uk.co.sentinelweb.tvmod.model.Movie;

/**
 * Created by robert on 19/02/2017.
 */

public class MxPlayerController {
    private static final int REQUEST_CODE = 42;


    public static final String PACKAGE_FREE = "com.mxtech.videoplayer.ad";
    public static final String PACKAGE_PRO = "com.mxtech.videoplayer.pro";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_POSITION = "position";

    CacheFileController downloadController;

    public MxPlayerController(final CacheFileController cacheFileController) {
        this.downloadController = cacheFileController;
    }

    // RESPONSE CODES
//    Activity.RESULT_OK : Playback was completed or stopped by user request.
//    Activity.RESULT_CANCELED: User canceled before starting any playback. Added in 1.8.4
//    RESULT_ERROR (=Activity.RESULT_FIRST_USER): Last playback was ended with an error. Added in 1.8.4

    /**
     * <a href="https://sites.google.com/site/mxvpen/api">MxPlayer android intents</a>
     *
     * @param c     context
     * @param movie selected movie
     */
    public void launchMxPlayer(final Activity c, final Movie movie) {
        final SmbLocation location = new SmbLocationParser().parse(movie.getVideoUrl());
        final File bufferFile = FileUtils.getBufferFile(c, location.getFileName());
        if ( !bufferFile.exists()) {
            downloadController.unsubscribe();
            final Observable<Long> longObservable = downloadController.downloadInBackground(c, location);
            longObservable.subscribe((bytesDownloaded) -> {
                        if (bytesDownloaded == 20 * FileUtils.MB) {
                            launchIntent(c, movie, Uri.fromFile(bufferFile));
                        }
                    },
                    (throwable) -> Log.d(MxPlayerController.class.getSimpleName(), "error getting progress", throwable),
                    () -> {
                    }
            );
        } else {
            launchIntent(c, movie, Uri.fromFile(bufferFile));
        }
    }

    private void launchIntent(final Activity c, final Movie movie, final Uri parse) {
        final Uri uri = parse;
        final Intent mxIntent = new Intent(Intent.ACTION_VIEW, uri);
        mxIntent.setPackage(PACKAGE_FREE);
        mxIntent.putExtra(EXTRA_TITLE, movie.getTitle());
        if (movie.getPosition() > 0) {
            mxIntent.putExtra(EXTRA_POSITION, movie.getPosition());// msec
        }
        c.startActivityForResult(mxIntent, REQUEST_CODE);
    }
}

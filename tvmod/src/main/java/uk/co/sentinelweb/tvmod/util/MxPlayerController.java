package uk.co.sentinelweb.tvmod.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.net.URLEncoder;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;
import rx.Subscription;
import uk.co.sentinelweb.microservice.MicroService;
import uk.co.sentinelweb.tvmod.model.Movie;


public class MxPlayerController {

    public static final String TAG = MxPlayerController.class.getSimpleName();
    public static final int REQUEST_CODE = 43;


    public static final String PACKAGE_FREE = "com.mxtech.videoplayer.ad";
    public static final String PACKAGE_PRO = "com.mxtech.videoplayer.pro";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_POSITION = "position";

    CacheFileController downloadController;
    private Subscription _serviceSubscribe;

    public MxPlayerController(final CacheFileController cacheFileController) {
        this.downloadController = cacheFileController;
    }

    // RESPONSE CODES
//    Activity.RESULT_OK : Playback was completed or stopped by user request.
//    Activity.RESULT_CANCELED: User canceled before starting any playback. Added in 1.8.4
//    RESULT_ERROR (=Activity.RESULT_FIRST_USER): Last playback was ended with an error. Added in 1.8.4

    /**
     *
     *
     * @param c     context
     * @param movie selected movie
     */
    public void launchMxPlayer(final Activity c, final Movie movie) {
        final SmbLocation location = new SmbLocationParser().parse(movie.getVideoUrl());
        c.startService(MicroService.getStartIntent(c));
        _serviceSubscribe = MicroService.statusPublishSubject.subscribe((status) -> {
            if (status == MicroService.Status.STARTED) {
                final String proxyUriString = "http://localhost:4443/s/" + URLEncoder.encode(movie.getVideoUrl());
                Log.d(TAG,"Server started: proxying:"+proxyUriString);
                final Uri proxyUrl = Uri.parse(proxyUriString);
                launchIntent(c, movie, proxyUrl);
            }
        }, (throwable) -> Log.d(TAG,"Error starting server", throwable));
    }


    /**
     * Intent launcher
     * <a href="https://sites.google.com/site/mxvpen/api">MxPlayer android intents</a>
     * @param c
     * @param movie
     * @param proxy
     */
    private void launchIntent(final Activity c, final Movie movie, final Uri proxy) {
        final Intent mxIntent = new Intent(Intent.ACTION_VIEW, proxy);
        mxIntent.setPackage(PACKAGE_FREE);
        mxIntent.putExtra(EXTRA_TITLE, movie.getTitle());
        mxIntent.putExtra("return_result", true);
        //mxIntent.putExtra("size", movie.size());
        if (movie.getPosition() > 0) {
            mxIntent.putExtra(EXTRA_POSITION, (long)(movie.getPosition()));// msec
        }
        c.startActivityForResult(mxIntent, REQUEST_CODE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data, final Movie selectedMovie) {
        Log.d(TAG, "got result:"+data+":"+"for movie"+selectedMovie.getTitle());
    }
}

//    public void stopDownload() {
//        downloadController.unsubscribe();
//    }

//        final File bufferFile = FileUtils.getBufferFile(c, location.getFileName());
//if ( !bufferFile.exists()) {
//        stopDownload();
//        final Observable<Long> progressObservable = downloadController.downloadInBackground(c, location);
//        progressObservable.subscribe(new Observer<Long>() {
//            boolean launched = false;
//
//            @Override
//            public void onCompleted() {}
//
//            @Override
//            public void onError(final Throwable e) {
//                Log.d(MxPlayerController.class.getSimpleName(), "error getting progress", e);
//            }
//
//            @Override
//            public void onNext(final Long bytesDownloaded) {
//                if (bytesDownloaded > 20 * FileUtils.MB && !launched) {
//                    launchIntent(c, movie, Uri.fromFile(bufferFile));
//                    launched = true;
//                }
//            }
//        });
//        } else {
//            launchIntent(c, movie, Uri.fromFile(bufferFile));
//        }
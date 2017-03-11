package uk.co.sentinelweb.tvmod.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import uk.co.sentinelweb.tvmod.model.Item;


public class MxPlayerController {

    public static final String TAG = MxPlayerController.class.getSimpleName();
    public static final int REQUEST_CODE = 43;

    public static final String PACKAGE_FREE = "com.mxtech.videoplayer.ad";
    public static final String PACKAGE_PRO = "com.mxtech.videoplayer.pro";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_POSITION = "position";

    final WebProxyManager _webProxyManager;
    final Activity c;

    @Inject
    public MxPlayerController(final Activity c, final WebProxyManager webProxyManager) {
        this._webProxyManager = webProxyManager;
        this.c=c;
    }

    // RESPONSE CODES
//    Activity.RESULT_OK : Playback was completed or stopped by user request.
//    Activity.RESULT_CANCELED: User canceled before starting any playback. Added in 1.8.4
//    RESULT_ERROR (=Activity.RESULT_FIRST_USER): Last playback was ended with an error. Added in 1.8.4

    public boolean checkInstalled() {
        final boolean proInstalled = PackageUtils.isAppInstalled(c, PACKAGE_PRO);
        final boolean freeInstalled = PackageUtils.isAppInstalled(c, PACKAGE_FREE);
        return proInstalled || freeInstalled;
    }

    public String getInstalled() {
        if (PackageUtils.isAppInstalled(c, PACKAGE_PRO)) {
            return PACKAGE_PRO;
        } else if (PackageUtils.isAppInstalled(c, PACKAGE_FREE)) {
            return PACKAGE_FREE;
        }
        return null;
    }

    /**
     * @param item selected movie
     */
    public void launchMxPlayer( final Item item) {
        final String packageInstalled = getInstalled();
        if (packageInstalled != null) {
            _webProxyManager.launchProxyMovieAction(item, this::launchIntent);
        } else {
            Toast.makeText(c, "No MXplayer installed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Intent launcher
     * <a href="https://sites.google.com/site/mxvpen/api">MxPlayer android intents</a>
     *
     * @param c
     * @param item
     * @param proxy
     */
    private void launchIntent(final Item item, final Uri proxy) {
        final Intent mxIntent = new Intent(Intent.ACTION_VIEW, proxy);
        mxIntent.setPackage(PACKAGE_FREE);
        mxIntent.putExtra(EXTRA_TITLE, item.getTitle());
        mxIntent.putExtra("return_result", true);
        //mxIntent.putExtra("size", movie.size());
        if (item.getPosition() > 0) {
            mxIntent.putExtra(EXTRA_POSITION, (long) (item.getPosition()));// msec
        }
        c.startActivityForResult(mxIntent, REQUEST_CODE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data, final Item selectedItem) {
        Log.d(TAG, "got result:" + data + ":" + "for movie" + selectedItem.getTitle());
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
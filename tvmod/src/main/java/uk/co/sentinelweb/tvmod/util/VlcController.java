package uk.co.sentinelweb.tvmod.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import uk.co.sentinelweb.tvmod.model.Item;

/**
 * Created by robertm on 17/02/2017.
 */
public class VlcController {
    private static final String VLC_PKG_NAME = "org.videolan.vlc";
    private static final String VLC_VIDEO_ACTIVITY = "org.videolan.vlc.gui.video.VideoPlayerActivity";
    private static final String VLC_INTENT_ACTION_RESULT = "org.videolan.vlc.player.result";


    public static final int REQUEST_CODE = 42;
    private static final String EXTRA_POSITION_OUT = "extra_position";
    private static final String EXTRA_DURATION_OUT = "extra_duration";

    private static final int RESULT_OK = -1; //	Video finished or user ended playback
    private static final int RESULT_CANCELED = 0; //	No compatible cpu, incorrect VLC abi variant installed
    private static final int RESULT_CONNECTION_FAILED = 2; //	Connection failed to audio service
    private static final int RESULT_PLAYBACK_ERROR = 3; //	VLC is not able to play this file, it could be incorrect path/uri, not supported codec or broken file
    private static final int RESULT_HARDWARE_ACCELERATION_ERROR = 4; //	Error with hardware acceleration, user refused to switch to software decoding
    private static final int RESULT_VIDEO_TRACK_LOST = 5; //
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_FROM_START = "from_start";
    public static final String EXTRA_POSITION = "position";
    public static final String VIDEO_MIMETYPE = "video/*";

    final Activity c;
    final WebProxyManager _webProxyManager;

    @Inject
    public VlcController(final Activity c, final WebProxyManager webProxyManager) {
        this._webProxyManager = webProxyManager;
        this.c = c;
    }

    public boolean checkInstalled() {
        final boolean installed = PackageUtils.isAppInstalled(c, VLC_PKG_NAME);
        return installed;
    }

    /**
     * @param item selected movie
     */
    public void launchVlcProxy(final Item item) {
        if (checkInstalled()) {
            _webProxyManager.launchProxyMovieAction(item, this::launchIntent);
        } else {
            Toast.makeText(c, "No MXplayer installed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Launch the video uri for VLC using the smb:// url
     * <a href="https://wiki.videolan.org/Android_Player_Intents/">VLC android intents</a>
     *
     * @param item selectred movie
     */
    public void launchVlcSmb(final Item item) {
        //Uri uri = Uri.parse("file:///storage/emulated/0/Movies/KUNG FURY Official Movie.mp4");
        final Uri uri = Uri.parse(item.getVideoUrl());
        launchIntent(item, uri);
    }

    public void launchIntent(final Item item, final Uri uri) {
        final Intent vlcIntent = new Intent(VLC_INTENT_ACTION_RESULT);
        vlcIntent.setComponent(new ComponentName(VLC_PKG_NAME, VLC_VIDEO_ACTIVITY));
        //vlcIntent.setPackage(VLC_PKG_NAME);
        vlcIntent.setDataAndTypeAndNormalize(uri, VIDEO_MIMETYPE);
        vlcIntent.putExtra(EXTRA_TITLE, item.getTitle());
        if (item.getPosition() > 0) {
            vlcIntent.putExtra(EXTRA_FROM_START, false);
            vlcIntent.putExtra(EXTRA_POSITION, item.getPosition());// msec?
        }
        //vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt");
        c.startActivityForResult(vlcIntent, REQUEST_CODE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data, final Item m) {
        if (REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                final long pos = data.getLongExtra(EXTRA_POSITION_OUT, -1);//Last position in media when player exited
                if (pos > -1) {
                    m.setPosition(pos);
                }
                final long dur = data.getLongExtra(EXTRA_DURATION_OUT, -1);//	long	Total duration of the media
                if (dur > -1) {
                    m.setDuration(dur);
                }
                Log.d(VlcController.class.getSimpleName(), "VLC: Got results code: pos:" + pos+" dur:"+dur);
            } else {
                Log.d(VlcController.class.getSimpleName(), "VLC: Got result code:" + resultCode);
            }

        }
    }
}

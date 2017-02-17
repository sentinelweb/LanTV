package uk.co.sentinelweb.tvmod.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import uk.co.sentinelweb.tvmod.model.Movie;

/**
 * Created by robertm on 17/02/2017.
 */
public class VlcUtil {

    private static final String VLC_PKG_NAME = "org.videolan.vlc";
    private static final String VLC_VIDEO_ACTIVITY = "org.videolan.vlc.gui.video.VideoPlayerActivity";

    private static final int VLC_REQUEST_CODE = 42;
    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_DURATION = "extra_duration";

    private static final int RESULT_OK = -1; //	Video finished or user ended playback
    private static final int RESULT_CANCELED = 0; //	No compatible cpu, incorrect VLC abi variant installed
    private static final int RESULT_CONNECTION_FAILED = 2; //	Connection failed to audio service
    private static final int RESULT_PLAYBACK_ERROR = 3; //	VLC is not able to play this file, it could be incorrect path/uri, not supported codec or broken file
    private static final int RESULT_HARDWARE_ACCELERATION_ERROR = 4; //	Error with hardware acceleration, user refused to switch to software decoding
    private static final int RESULT_VIDEO_TRACK_LOST = 5; //

    /**
     * <a href="https://wiki.videolan.org/Android_Player_Intents/">VLC android intents</a>
     *
     * @param c     context
     * @param movie selectred movie
     */
    public void launchVlc(final Activity c, final Movie movie) {
        Uri uri = Uri.parse("file:///storage/emulated/0/Movies/KUNG FURY Official Movie.mp4");
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setComponent(new ComponentName(VLC_PKG_NAME, VLC_VIDEO_ACTIVITY));
        //vlcIntent.setPackage(VLC_PKG_NAME);
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", movie.getTitle());
        //vlcIntent.putExtra("from_start", false);
        //vlcIntent.putExtra("position", 90000l);
        //vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt");
        c.startActivityForResult(vlcIntent, VLC_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VLC_REQUEST_CODE == requestCode) {

            // TODO check result code
            data.getLongExtra(EXTRA_POSITION, -1); //Last position in media when player exited
            data.getLongExtra(EXTRA_DURATION, -1); //	long	Total duration of the media


        }
    }
}

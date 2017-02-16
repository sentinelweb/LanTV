package uk.co.sentinelweb.tvmod.exoplayer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;

/**
 * Created by robert on 15/02/2017.
 */

public class ExoConfig {

    private boolean useExtensionRenderers;
    private final Context c;

    public ExoConfig(final Context c, final boolean useExtensionRenderers) {
        this.c = c;
    }

    public DataSource.Factory buildDataSourceFactory(final DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(c, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(final DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(c, "LanTv"), bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return useExtensionRenderers;
    }

    /**
     * Returns a user agent string based on the given application name and the library version.
     *
     * @param context         A valid context of the calling application.
     * @param applicationName String that will be prefix'ed to the generated user agent.
     * @return A user agent string generated using the applicationName and the library version.
     */
    public static String getUserAgent(final Context context, final String applicationName) {
        String versionName;
        try {
            final String packageName = context.getPackageName();
            final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (final PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + "ExoPlayerLib/" + ExoPlayerLibraryInfo.VERSION;
    }

}

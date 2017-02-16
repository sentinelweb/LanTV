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

import uk.co.sentinelweb.tvmod.exoplayer.upstream.NetworkDataSourceFactory;
import uk.co.sentinelweb.tvmod.exoplayer.upstream.SmbDataSourceFactory;

/**
 * Created by robert on 15/02/2017.
 */

public class ExoConfig {

    private boolean useExtensionRenderers;
    private final Context context;

    /**
     *
     * @param context
     * @param useExtensionRenderers
     */
    public ExoConfig(final Context context, final boolean useExtensionRenderers) {
        this.context = context;
    }

    /**
     *
     * @param bandwidthMeter bw
     * @return
     */
    public DataSource.Factory buildDataSourceFactory(final DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, buildNetworkDataSourceFactory(bandwidthMeter));
    }

    /**
     * Builds a {@link com.google.android.exoplayer2.upstream.HttpDataSource.Factory} only
     * @param bandwidthMeter
     * @return
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory(final DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(context, "LanTv"), bandwidthMeter);
    }
    /**
     * Builds a {@link uk.co.sentinelweb.tvmod.exoplayer.upstream.NetworkDataSourceFactory}
     * remove the bandwidth meter as it not really need i think
     *
     * @return
     */
    public NetworkDataSourceFactory buildNetworkDataSourceFactory(final DefaultBandwidthMeter bandwidthMeter) {
        final DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent(context, "LanTv"), bandwidthMeter);
        final SmbDataSourceFactory smbDataSourceFactory = new SmbDataSourceFactory();
        return new NetworkDataSourceFactory(httpDataSourceFactory, smbDataSourceFactory);
    }

    /**
     *
     * @return
     */
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

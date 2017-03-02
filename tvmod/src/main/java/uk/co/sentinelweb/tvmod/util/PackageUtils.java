package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by robert on 20/02/2017.
 */

public class PackageUtils {
    public static boolean isAppInstalled(final Context context, final String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by robert on 15/02/2017.
 */

public class FileUtils {

    public static String getExt(final String name) {
        return MimeTypeMap.getFileExtensionFromUrl(name);
    }

    public static void copyFileFromAsset(final Context c, final String fileName, final File target) {
        OutputStream out = null;
        InputStream in = null;
        try {
            in = c.getAssets().open(fileName);
            out = new FileOutputStream(target);
            final byte[] buffer = new byte[1000];
            int read = -1;
            while ((read = in.read(buffer)) > -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static long getFreeSpace() {
        final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        final double sdAvailSize = (double) stat.getAvailableBlocksLong()
                * (double) stat.getBlockSizeLong();
        final double mbAvailable = sdAvailSize / 1024 / 1024;
        return (long) mbAvailable;
    }

}

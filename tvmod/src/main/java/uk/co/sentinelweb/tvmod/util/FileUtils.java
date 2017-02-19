package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for handling files
 */
public final class FileUtils {
    private FileUtils() {
    }

    /**
     * Get the file extension for a string / url
     *
     * @param name name or url
     * @return extension
     */
    public static String getExt(final String name) {
        final String ext = name.substring(name.lastIndexOf(".") + 1);
        return ext;
    }

    public static File getBufferFile(final Context c, final String inputName) {
        final File targetParent = c.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!targetParent.exists()) {
            targetParent.mkdirs();
        }
        return new File(targetParent, "buffer." + getExt(inputName));
    }

    public static long toMb(final long bytes) {
        return bytes/1024/1024;
    }

    /**
     * Generic copy logic to write local file from stream
     * @param target
     * @param inputStream
     */
    public static void copyFileFromStream(final File target, final InputStream inputStream) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
            final byte[] buffer = new byte[1000000];
            int count = 0;
            int read = -1;
            while ((read = inputStream.read(buffer)) > -1) {
                out.write(buffer, 0, read);
                count+=read;
                Log.d(FileUtils.class.getSimpleName(), "read:"+toMb(count));
            }
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(out);
            closeStream(inputStream);
        }
    }

    public static void copyFileFromAsset(final Context c, final String fileName, final File target) {
        InputStream in = null;
        try {
            in = c.getAssets().open(fileName);
            copyFileFromStream(target, in);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
        }
    }

    private static void closeStream(final Closeable out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Free space in Mb
     * @return
     */
    public static long getFreeSpace() {
        final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        final double sdAvailSize = (double) stat.getAvailableBlocksLong()
                * (double) stat.getBlockSizeLong();
        final double mbAvailable = sdAvailSize / 1024 / 1024;
        return (long) mbAvailable;
    }




}

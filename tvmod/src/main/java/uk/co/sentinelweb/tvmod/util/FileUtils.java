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

import rx.subjects.PublishSubject;

/**
 * Utilities for handling files
 */
public final class FileUtils {

    public static final String BUFFER_DIRECTORY = "buffer";
    public static final int MB = (1024 * 1024);

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

    public static final long RESULT_NO_FILE = -1l;

    public static long checkBufferFile(final Context c, final String inputName) {
        final File bufferFile = getBufferFile(c, inputName);
        if (bufferFile.exists()) {
            return bufferFile.length();
        } else {
            return RESULT_NO_FILE;
        }
    }

    public static void clearBufferedFile(final Context c) {
        final File bufferDir = c.getExternalFilesDir(BUFFER_DIRECTORY);
        if (bufferDir.exists()) {
            for (final File f : bufferDir.listFiles()) {
                f.delete();
            }
        }
    }

    public static File getBufferFile(final Context c, final String inputName) {
        final File targetParent = c.getExternalFilesDir(BUFFER_DIRECTORY);
        if (!targetParent.exists()) {
            targetParent.mkdirs();
        }
        return new File(targetParent, inputName);
    }

    public static long toMb(final long bytes) {
        return bytes / MB;
    }

    /**
     * Generic copy logic to write local file from stream
     *
     * @param target
     * @param inputStream
     */
    public static void copyFileFromStream(final File target, final InputStream inputStream, final PublishSubject<Long> progressObservable) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
            final byte[] buffer = new byte[1000000];
            long count = 0;
            int read = -1;
            long lastPublish = -1;
            while ((read = inputStream.read(buffer)) > -1) {
                out.write(buffer, 0, read);
                count += read;
                Log.d(FileUtils.class.getSimpleName(), "read:" + toMb(count));

                if (progressObservable != null && count > lastPublish + MB) {
                    progressObservable.onNext(count);
                    lastPublish = count;
                }
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
            copyFileFromStream(target, in, null);
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
     *
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

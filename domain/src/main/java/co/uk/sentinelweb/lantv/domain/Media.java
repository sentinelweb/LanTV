package co.uk.sentinelweb.lantv.domain;

import com.google.auto.value.AutoValue;

import java.util.Date;

/**
 * Created by robert on 12/02/2017.
 */
@AutoValue
public abstract class Media {
    public static Media create(final String url, final String title, final Long size, final Date modified,
                               final boolean isWorkgroup,final boolean isComputer,final boolean isShare,  final boolean isPrinter, final boolean isDir, final boolean isFile
                               ) {
        return new AutoValue_Media(url, title, size, modified, isDir, isFile, isShare, isComputer, isWorkgroup, isPrinter);
    }

    public static Media mutateUrl(final String url, final Media m) {
        return new AutoValue_Media(url, m.title(), m.size(), m.modified(), m.isDirectory(), m.isFile(), m.isShare(), m.isComputer(), m.isWorkgroup(), m.isPrinter());
    }

    public abstract String url();

    public abstract String title();

    public abstract Long size();

    public abstract Date modified();

    public abstract boolean isDirectory();

    public abstract boolean isFile();
    public abstract boolean isShare();
    public abstract boolean isComputer();
    public abstract boolean isWorkgroup();
    public abstract boolean isPrinter();


}

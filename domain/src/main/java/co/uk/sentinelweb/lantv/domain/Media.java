package co.uk.sentinelweb.lantv.domain;

import com.google.auto.value.AutoValue;

import java.util.Date;

/**
 * Created by robert on 12/02/2017.
 */
@AutoValue
public abstract class Media {
    public static Media create(final String url, final String title, final Long size, final Date modified) {
        return new AutoValue_Media(url, title, size, modified);
    }

    public abstract String url();

    public abstract String title();

    public abstract Long size();

    public abstract Date modified();


}

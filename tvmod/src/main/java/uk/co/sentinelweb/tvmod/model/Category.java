package uk.co.sentinelweb.tvmod.model;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Created by robert on 16/02/2017.
 */

@AutoValue
public abstract class Category {
    public static Category builder(final List<Movie> movies, final String name, final int count) {
        return new AutoValue_Category(movies, movies.size(),  name);
    }

    public abstract List<Movie> movies() ;

    public abstract  int count();

    public abstract String name();
}

package uk.co.sentinelweb.tvmod.model;

import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.util.List;

/**
 * Created by robert on 16/02/2017.
 */

@AutoValue
public abstract class Category implements Serializable {


    private static final long serialVersionUID = 7498636430317059873L;

    public static Category builder(final List<Item> movies, final String name, final int count) {
        return new AutoValue_Category(movies, movies.size(),  name);
    }

    public abstract List<Item> items() ;

    public abstract  int count();

    public abstract String name();
}

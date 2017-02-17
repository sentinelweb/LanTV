package uk.co.sentinelweb.tvmod.mapper;

import java.util.List;

import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;

/**
 * Created by robert on 16/02/2017.
 */

public class CategoryMapper {
    public Category map(final String name, final List<Movie> movies) {
        return Category.builder(movies, name, movies.size());
    }
}

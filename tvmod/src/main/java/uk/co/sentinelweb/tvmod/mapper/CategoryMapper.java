package uk.co.sentinelweb.tvmod.mapper;

import java.util.List;

import javax.inject.Inject;

import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Item;

/**
 * Created by robert on 16/02/2017.
 */

public class CategoryMapper {

    @Inject
    public CategoryMapper() {
    }

    public Category map(final String name, final List<Item> movies) {
        return Category.builder(movies, name, movies.size());
    }
}

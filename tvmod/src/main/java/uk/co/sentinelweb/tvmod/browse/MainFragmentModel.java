package uk.co.sentinelweb.tvmod.browse;

import java.util.List;

import uk.co.sentinelweb.tvmod.model.Category;

/**
 * Created by robert on 16/02/2017.
 */
public class MainFragmentModel {

    public final List<Category> _categories;

    public MainFragmentModel(final List<Category> movies) {
        this._categories = movies;
    }
}

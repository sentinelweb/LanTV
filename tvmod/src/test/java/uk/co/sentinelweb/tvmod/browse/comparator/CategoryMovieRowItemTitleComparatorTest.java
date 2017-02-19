package uk.co.sentinelweb.tvmod.browse.comparator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;

import static junit.framework.Assert.assertEquals;

/**
 * Created by robert on 19/02/2017.
 */
public class CategoryMovieRowItemTitleComparatorTest {

    CategoryMovieRowItemTitleComparator sut = new CategoryMovieRowItemTitleComparator();

    @Test
    public void sort() throws Exception {
        final Category category = buildCategory(Arrays.asList(new String[]{"a", "b", "c", "d", null, null}));

        sut.sort(category);

        checkOrder(category, new String[]{"a", "b", "c", "d", null, null});
    }

    @Test
    public void sortWithCurrrent() throws Exception {
        final Category category = buildCategory(Arrays.asList(new String[]{"a", null, "d","b", null,  "c", C.CURRENT_DIR_TITLE}));

        sut.sort(category);

        checkOrder(category, new String[]{C.CURRENT_DIR_TITLE, "a", "b", "c", "d", null, null});
    }

    private void checkOrder(final Category category, final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], category.movies().get(i).getTitle());
        }
    }

    private Category buildCategory(final List<String> titles) {
        final List<Movie> movies = new ArrayList<>();
        for (final String title : titles) {
            final Movie m = new Movie();
            m.setTitle(title);
            movies.add(m);
        }
        return Category.builder(movies, "", titles.size());
    }

}
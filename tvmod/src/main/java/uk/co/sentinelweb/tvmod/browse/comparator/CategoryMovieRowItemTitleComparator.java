package uk.co.sentinelweb.tvmod.browse.comparator;

import java.util.Collections;
import java.util.Comparator;

import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;

/**
 * Created by robert on 19/02/2017.
 */

public class CategoryMovieRowItemTitleComparator implements Comparator<Movie>{

    @Override
    public int compare(final Movie o1, final Movie o2) {
        if (C.CURRENT_DIR_TITLE.equals(o1.getTitle()) && !C.CURRENT_DIR_TITLE.equals(o2.getTitle())) {
            return -1;
        } else if (C.CURRENT_DIR_TITLE.equals(o2.getTitle()) && !C.CURRENT_DIR_TITLE.equals(o1.getTitle()))  {
            return 1;
        } else if (o1.getTitle()!=null) {
            if (o2.getTitle() != null) {
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            } else {
                return -1;
            }
        } else {
            return 1;// sort null title to end
        }
    }

    public Category sort(final Category cat) {
        Collections.sort(cat.movies(), this);
        return cat;
    }
}

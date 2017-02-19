package uk.co.sentinelweb.tvmod.browse.comparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;

/**
 * Sort for directory list
 * Created by robert on 19/02/2017.
 */
public class MediaTitleSortComparator implements Comparator<Media>{

    @Override
    public int compare(final Media o1, final Media o2) {
        return o1.title().compareTo(o2.title());
    }

    public List<Media> sort(final List<Media> medias) {
        Collections.sort(medias, this);
        return medias;
    }
}

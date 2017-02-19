package uk.co.sentinelweb.tvmod.util;

import android.net.Uri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertm on 17/02/2017.
 */

public class HiddenFiles {
    public static final Set<Exclusion> EXCLUDED = new HashSet<>(Arrays.asList(
            new Exclusion(Pattern.IS, ".DS_Store"),
            new Exclusion(Pattern.IS, "._.DS_Store"),
            new Exclusion(Pattern.CONTAINS, "Trash"),
            new Exclusion(Pattern.CONTAINS, ".Temporary"),
            new Exclusion(Pattern.IS, "lost+found")
    ));

    public static boolean isExcluded(final String name) {
        for (final Exclusion e : EXCLUDED) {
            if (e.pattern == Pattern.IS && name.equals(e.text)) {
                return true;
            } else if (e.pattern == Pattern.CONTAINS && name.contains(e.text)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExcludedUrl(final String url) {
        final String name = Uri.parse(url).getLastPathSegment();
        return isExcluded(name);
    }

    private static enum Pattern {STARTS, ENDS, IS, CONTAINS}

    private static class Exclusion {
        private final String text;
        private final Pattern pattern;

        public Exclusion(final Pattern pattern, final String text) {
            this.pattern = pattern;
            this.text = text;
        }
    }

}

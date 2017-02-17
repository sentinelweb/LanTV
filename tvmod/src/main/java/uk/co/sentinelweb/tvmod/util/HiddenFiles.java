package uk.co.sentinelweb.tvmod.util;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertm on 17/02/2017.
 */

public class HiddenFiles {
    private static enum Pattern {STARTS, ENDS, IS, CONTAINS}

    private static class Exclusion {
        private final String text;
        private final Pattern pattern;

        public Exclusion(Pattern pattern, String text) {
            this.pattern = pattern;
            this.text = text;
        }
    }
    public static final Set<Exclusion> EXCLUDED = new HashSet<>(Arrays.asList(
            new Exclusion(Pattern.IS, ".DS_Store")
    ));

    public static boolean isExcluded(String name) {
        for (Exclusion e : EXCLUDED) {
            if (e.pattern == Pattern.IS && name.equals(e.text)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExcludedUrl(String url) {
        String name = Uri.parse(url).getLastPathSegment();
        return isExcluded(name);
    }
}

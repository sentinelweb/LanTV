package co.uk.sentinelweb.lantv.util;

import java.util.List;

/**
 * Created by robert on 18/02/2017.
 */

public class StringUtils {
    public static String concat(final List<String> pathSegments, final String joinString) {
        return concat(pathSegments, joinString, 0, 0);
    }

    public static String concat(final List<String> pathSegments, final String joinString, final int start, final int end) {
        String path = "";
        final int iterEnd = end < 1 ? pathSegments.size() + end : end;
        if (start > iterEnd) {
            return null;
        }
        for (int i = start; i < iterEnd; i++) {
            path+=pathSegments.get(i);
            if (i<iterEnd-1) {
                path+=joinString;
            }
        }
        return path;
    }

    public static String concat(final String[] pathSegments, final String joinString) {
        return concat(pathSegments, joinString, 0, 0);
    }

    public static String concat(final String[] pathSegments, final String joinString, final int start, final int end) {
        String path = "";
        final int iterEnd = end < 1 ? pathSegments.length + end : end;
        if (start > iterEnd) {
            return null;
        }
        for (int i = start; i < iterEnd; i++) {
            path+=pathSegments[i];
            if (i<iterEnd-1) {
                path+=joinString;
            }
        }
        return path;
    }

    public static boolean isBlank(final String s) {
        return s==null || s.isEmpty();
    }
}

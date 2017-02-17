package uk.co.sentinelweb.tvmod.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertm on 17/02/2017.
 */

public enum SupportedMedia {
    MP4, MP3, M4V, M4A, OGG;
    static Map<String, SupportedMedia> valueMap;
    static {
        valueMap = new HashMap<>();
        for (SupportedMedia s:values()) {
            valueMap.put(s.toString(), s);
        }
    }

    public static boolean isSupported(String ext) {
        return valueMap.containsKey(ext);
    }
}

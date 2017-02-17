package uk.co.sentinelweb.tvmod.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertm on 17/02/2017.
 */

public enum SupportedMedia {
    MP4(true), MP3(true), M4V(true), M4A(true), OGG(true), AVI(false), MKV(false);
    Boolean supported = null;

    static Map<String, SupportedMedia> valueMap;
    static {
        valueMap = new HashMap<>();
        for (SupportedMedia s:values()) {
            valueMap.put(s.toString(), s);
        }
    }

    public static boolean isSupported(final String ext) {
        return valueMap.containsKey(ext);
    }

    SupportedMedia(final Boolean supported) {
        this.supported = supported;
    }
}

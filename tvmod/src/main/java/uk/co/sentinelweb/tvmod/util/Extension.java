package uk.co.sentinelweb.tvmod.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertm on 17/02/2017.
 */

public enum Extension {

    MP4(true, MediaType.VIDEO),
    M4V(true, MediaType.VIDEO),
    OGG(true, MediaType.VIDEO),
    OGV(true, MediaType.VIDEO),
    AVI(false, MediaType.VIDEO),
    MKV(false, MediaType.VIDEO),

    MP3(true, MediaType.AUDIO),
    M4A(true, MediaType.AUDIO),
    FLAC(true, MediaType.AUDIO),
    OGA(true, MediaType.AUDIO),

    JPG(false, MediaType.IMAGE),
    JPEG(false, MediaType.IMAGE),
    PNG(false, MediaType.IMAGE),
    GIF(false, MediaType.IMAGE),

    DIR(true, MediaType.FOLDER),
    SRVR(true, MediaType.COMPUTER),
    WKGP(true, MediaType.WORKGROUP),
    PRN(false, MediaType.PRINTER),
    SHARE(true, MediaType.SHARE),

    UNKNOWN(true, MediaType.OTHER),;

    Boolean _supported = null;
    MediaType _mediaType;

    static Map<String, Extension> valueMap;

    static {
        valueMap = new HashMap<>();
        for (final Extension s : values()) {
            valueMap.put(s.toString(), s);
        }
    }


    public static boolean isSupported(final String ext) {
        if (ext != null) {
            final String key = ext.toUpperCase();
            return (valueMap.containsKey(key) ? valueMap.get(key)._supported : false);
        } else {
            return true;
        }
    }

    public static boolean shouldDisplay(final String ext) {
        if (ext != null) {
            final String key = ext.toUpperCase();
            return valueMap.containsKey(key);
        } else {
            return true;
        }
    }

    public Boolean getSupported() {
        return _supported;
    }

    public MediaType getMediaType() {
        return _mediaType;
    }

    public int getIcon() {
        return _mediaType.drawableResId;
    }

    Extension(final Boolean supported, final MediaType other) {
        this._supported = supported;
        _mediaType = other;
    }


    public static Extension from(final String extString) {
        if (extString == null) {
            return null;
        }
        try {
            return valueOf(extString.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}

package uk.co.sentinelweb.tvmod.util;

import uk.co.sentinelweb.tvmod.R;

public enum MediaType {
        AUDIO(R.drawable.ic_music_video_48px),
        VIDEO(R.drawable.ic_movie_48px),
        IMAGE(R.drawable.ic_image_48px),
        FOLDER(R.drawable.ic_folder_48px),
        OTHER(R.drawable.ic_block_48px);
        final int resId;

        MediaType(final int resId) {
            this.resId = resId;
        }
    }
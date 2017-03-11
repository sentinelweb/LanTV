package uk.co.sentinelweb.tvmod.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import uk.co.sentinelweb.tvmod.R;

public enum MediaType {
    AUDIO(R.drawable.ic_music_video_48px, R.string.audio),
    VIDEO(R.drawable.ic_movie_48px, R.string.video),
    IMAGE(R.drawable.ic_image_48px, R.string.image),
    OTHER(R.drawable.ic_block_48px, R.string.other),
    FOLDER(R.drawable.ic_folder_48px, R.string.dir),
    COMPUTER(R.drawable.ic_laptop_windows_black_24dp, R.string.computer),
    WORKGROUP(R.drawable.ic_settings_ethernet_black_24dp, R.string.workgroup),
    PRINTER(R.drawable.ic_print_black_24dp, R.string.printer),
    SHARE(R.drawable.ic_folder_shared_black_24dp, R.string.share),
    ;
    @DrawableRes
    final int drawableResId;
    @StringRes
    final int stringResId;

    MediaType(final @DrawableRes int drawableResId, final @StringRes int stringResId) {
        this.drawableResId = drawableResId;
        this.stringResId = stringResId;
    }
}
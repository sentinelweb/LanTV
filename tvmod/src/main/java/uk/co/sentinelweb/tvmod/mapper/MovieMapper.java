package uk.co.sentinelweb.tvmod.mapper;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import co.uk.sentinelweb.lantv.domain.Media;
import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.util.Extension;
import uk.co.sentinelweb.tvmod.util.FileUtils;
import uk.co.sentinelweb.tvmod.util.HiddenFiles;


public class MovieMapper {

    public static final String VIDEO_ICON = "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png";
    public static final String FOLDER_ICON = "http://icons.iconarchive.com/icons/artua/mac/512/Folder-icon.png";

    @Inject
    public MovieMapper() {
    }

    public List<Item> mapList(final List<Media> medias) {
        final List<Item> movies = new ArrayList<>();
        for (final Media m : medias) {
            final Item map = map(m);
            if (map != null) {
                movies.add(map);
            }
        }
        return movies;
    }

    public Item map(final Media media) {
        if (media.isWorkgroup() || media.isComputer() || media.isShare() || media.isPrinter()) {
            return mapNetworkMedia(media);
        } else if (media.isDirectory() || media.isFile() ) {
            final String extString = media.isDirectory() ? C.DIR_EXTENSION : FileUtils.getExt(media.url());
            final List<String> pathSegments = Uri.parse(media.url()).getPathSegments();
            final String lastPathSegment = pathSegments.get(pathSegments.size() - 1);
            final Extension ext = Extension.from(extString);
            if (ext != null && !HiddenFiles.isExcluded(lastPathSegment)) {
                final String category = pathSegments.get(pathSegments.size() - 2);
                return buildMovieInfo(
                        category,
                        media.title(),
                        "description of movie",
                        ext,
                        media.url(),
                        null,// TODO get image from somewhere
                        null

                );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private Item mapNetworkMedia(final Media media) {
        final Extension extension;
        if (media.isWorkgroup()) {
            extension = Extension.WKGP;
        } else if (media.isComputer()) {
            extension = Extension.SRVR;
        } else if (media.isShare()) {
            extension = Extension.SHARE;
        } else if (media.isPrinter()) {
            extension = Extension.PRN;
        } else {
            extension = Extension.UNKNOWN;
        }
        return buildMovieInfo(
                media.title(),
                media.title(),
                "description of movie",
                extension,
                media.url(),
                null,// TODO get image from somewhere
                null

        );
    }

    public static Item buildMovieInfo(final String category, final String title,
                                      final String description, final Extension extension, final String videoUrl, final String cardImageUrl,
                                      final String bgImageUrl) {
        final Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setExtension(extension);
        item.setCategory(category);
        item.setCardImageUrl(cardImageUrl);
        item.setBackgroundImageUrl(bgImageUrl);
        item.setVideoUrl(videoUrl);
        return item;
    }

}

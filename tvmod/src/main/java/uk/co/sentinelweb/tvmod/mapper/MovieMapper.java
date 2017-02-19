package uk.co.sentinelweb.tvmod.mapper;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.Extension;
import uk.co.sentinelweb.tvmod.util.FileUtils;
import uk.co.sentinelweb.tvmod.util.HiddenFiles;


public class MovieMapper {

    public static final String VIDEO_ICON = "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png";
    public static final String FOLDER_ICON = "http://icons.iconarchive.com/icons/artua/mac/512/Folder-icon.png";

    public List<Movie> mapList(final List<Media> medias) {
        final List<Movie> movies = new ArrayList<>();
        for (final Media m: medias) {
            final Movie map = map(m);
            if (map!= null) {
                movies.add(map);
            }
        }
        return movies;
    }

    public Movie map(final Media media) {
        final String extension = media.isDirectory() ? C.DIR_EXTENSION : FileUtils.getExt(media.url());
        final List<String> pathSegments = Uri.parse(media.url()).getPathSegments();
        final String lastPathSegment = pathSegments.get(pathSegments.size() - 1);
        if (Extension.shouldDisplay(extension) && !HiddenFiles.isExcluded(lastPathSegment)) {
            final String category = pathSegments.get(pathSegments.size() - 2);
            return buildMovieInfo(
                    category,
                    media.title(),
                    "description of movie",
                    extension,
                    media.url(),
                    null,// TODO get image from somewhere
                    null//MovieList.TEST_BG_IMAGE//"http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg"
            );
        } else {
            return null;
        }
    }

    public static Movie buildMovieInfo(final String category, final String title,
                                        final String description, final String extension, final String videoUrl, final String cardImageUrl,
                                        final String bgImageUrl) {
        final Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setExtension(extension);
        movie.setCategory(category);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(bgImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }

}

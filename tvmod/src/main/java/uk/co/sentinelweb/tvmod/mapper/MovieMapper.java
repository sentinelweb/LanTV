package uk.co.sentinelweb.tvmod.mapper;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.FileUtils;


public class MovieMapper {

    public static final String VIDEO_ICON = "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png";
    public static final String FOLDER_ICON = "http://icons.iconarchive.com/icons/artua/mac/512/Folder-icon.png";

    public List<Movie> mapList(final List<Media> medias) {
        final List<Movie> movies = new ArrayList<>();
        for (final Media m: medias) {
            movies.add(map(m));
        }
        return movies;
    }

    public Movie map(final Media media) {
        final List<String> pathSegments = Uri.parse(media.url()).getPathSegments();
        final String category = pathSegments.get(pathSegments.size()-2);
        return buildMovieInfo(
                category,
                media.title(),
                "description of movie",
                media.isDirectory() ?  "Dir":FileUtils.getExt(media.url()),
                media.url(),
                media.isDirectory() ? FOLDER_ICON : VIDEO_ICON,
                "http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg");
    }

    public static Movie buildMovieInfo(final String category, final String title,
                                        final String description, final String studio, final String videoUrl, final String cardImageUrl,
                                        final String bgImageUrl) {
        final Movie movie = new Movie();
        movie.setId(Movie.getCount());
        Movie.incCount();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setExtension(studio);
        movie.setCategory(category);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(bgImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }

}

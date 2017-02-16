package uk.co.sentinelweb.tvmod;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.LocalFileListInteractor;
import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import co.uk.sentinelweb.lantv.net.smb.TestData;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.sentinelweb.tvmod.browse.MainActivity;

public final class MovieList {
    public static final String MOVIE_CATEGORY[] = {
            "Category Zero",
            "Category One",
            "Category Two",
            "Category Three",
            "Category Four",
            "Category Five",
    };

    private static List<Movie> list;

    public static Observable<List<Movie>> setupMovies(final Context c) {
        if (list != null) {
            return Observable.just(list);
        } else {
            list = new ArrayList<>();
            //return getTestFileListObservable();
            //return getSambaListObservable();
            return getLocalPodcastListObservable();
//            return getTestRemoteListObservable();

        }

    }

    @NonNull
    private static Observable<List<Movie>> getSambaListObservable() {
        final Observable<List<Media>> listObservable = new SmbShareListInteractor()
                .getListObservable(TestData.IP_ADDR, TestData.SHARE, TestData.PATH, TestData.USER, TestData.PASS);
        return toMovieListObservable(listObservable);
    }

    @NonNull
    private static Observable<List<Movie>> toMovieListObservable(final Observable<List<Media>> listObservable) {
        return listObservable
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<Media>, Observable<Media>>() {
                    @Override
                    public Observable<Media> call(final List<Media> medias) {
                        return Observable.from(medias);
                    }
                }).map(new Func1<Media, Movie>() {
                    @Override
                    public Movie call(final Media media) {
                        return buildMovieInfo(
                                "category",
                                media.title(),
                                "description of movie",
                                "Studio Zero",
                                media.url(),
                                "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png",
                                "http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg");
                    }
                }).doOnNext(new Action1<Movie>() {
                    @Override
                    public void call(final Movie movie) {
                        list.add(movie);
                    }
                }).toList();
    }

    @NonNull
    private static Observable<List<Movie>> getLocalPodcastListObservable() {
        final File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
        final Observable<List<Media>> listObservable = new LocalFileListInteractor().getListObservable(externalStoragePublicDirectory);
        return toMovieListObservable(listObservable);
    }

    @NonNull
    private static Observable<List<Movie>> getTestRemoteListObservable() {

        final String[] title = {
                "Zeitgeist 2010_ Year in Review",
                "Google Demo Slam_ 20ft Search",
                "Introducing Gmail Blue",
                "Introducing Google Fiber to the Pole",
                "Introducing Google Nose"
        };

        final String description = "Fusce id nisi turpis. Praesent viverra bibendum semper. "
                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";

        final String[] videoUrl = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
        };
        final String[] bgImageUrl = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",
        };
        final String[] cardImageUrl = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
        };

        list.add(buildMovieInfo("category", title[0],
                description, "Studio Zero", videoUrl[0], cardImageUrl[0], bgImageUrl[0]));
        list.add(buildMovieInfo("category", title[1],
                description, "Studio One", videoUrl[1], cardImageUrl[1], bgImageUrl[1]));
        list.add(buildMovieInfo("category", title[2],
                description, "Studio Two", videoUrl[2], cardImageUrl[2], bgImageUrl[2]));
        list.add(buildMovieInfo("category", title[3],
                description, "Studio Three", videoUrl[3], cardImageUrl[3], bgImageUrl[3]));
        list.add(buildMovieInfo("category", title[4],
                description, "Studio Four", videoUrl[4], cardImageUrl[4], bgImageUrl[4]));
        return Observable.just(list);
    }

    @NonNull
    private static Observable<List<Movie>> getTestFileListObservable() {
        list.add(buildMovieInfo(
                "category",
                MainActivity.TEST_MOVIE_FILE.getName(),
                "description of movie: " +
                        MainActivity.TEST_MOVIE_FILE.getAbsolutePath() + ":" +
                        MainActivity.TEST_MOVIE_FILE.exists() + ":" +
                        getSpace() + " free",
                "Studio Zero",
                //MainActivity.TEST_MOVIE_FILE.getAbsolutePath(),
                Uri.fromFile(MainActivity.TEST_MOVIE_FILE).toString(),
                "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png",
                "http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg"));
        return Observable.just(list);
    }

    private static long getSpace() {
        final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        final double sdAvailSize = (double) stat.getAvailableBlocksLong()
                * (double) stat.getBlockSizeLong();
//One binary gigabyte equals 1,073,741,824 bytes.
        final double mbAvailable = sdAvailSize / 1024 / 1024;
        return (long) mbAvailable;
    }

    private static Movie buildMovieInfo(final String category, final String title,
                                        final String description, final String studio, final String videoUrl, final String cardImageUrl,
                                        final String bgImageUrl) {
        final Movie movie = new Movie();
        movie.setId(Movie.getCount());
        Movie.incCount();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setStudio(studio);
        movie.setCategory(category);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(bgImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }


}

package uk.co.sentinelweb.tvmod;

import java.util.Arrays;

import co.uk.sentinelweb.lantv.net.smb.TestData;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.util.Extension;

/**
 * Constants
 */
public final class C {
    public static final SmbLocation TEST_LOCATION = new SmbLocation("192.168.0.13", "Drobo", "video/items/", null,TestData.USER, TestData.PASS);
    public static final String TEST_CARD_IMAGE = "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg";
    public static final String TEST_BG_IMAGE ="http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg";

    public static final String DIR_EXTENSION = "Dir";
    public static final String PARENT_DIR_TITLE = "..";
    public static final String CURRENT_DIR_TITLE = ".";
    public static final Item PARENT_DIR_ITEM = new Item(PARENT_DIR_TITLE,"Parent directory",null,null, null,"Parent", 0l, Extension.DIR, 0l);
    public static final Category PARENT_CATEGORY = Category.builder(Arrays.asList(PARENT_DIR_ITEM),"..",0);
    public static final Item CURRRENT_DIR_ITEM = new Item(CURRENT_DIR_TITLE,"Current directory",null,null, null,"Current", 0l, Extension.DIR, 0l);

//    public static final String MOVIE_CATEGORY[] = {
//            "Category Zero",
//            "Category One",
//            "Category Two",
//            "Category Three",
//            "Category Four",
//            "Category Five",
//    };
//
//    private static List<Movie> list;
//
//    public static Observable<List<Movie>> setupMovies() {
//        if (list != null) {
//            return Observable.just(list);
//        } else {
//            list = new ArrayList<>();
//            //return getTestFileListObservable();
//            return getSambaListObservable();
//            //return getLocalPodcastListObservable();
////            return getTestRemoteListObservable();
//
//        }
//
//    }
//
//    @NonNull
//    private static Observable<List<Movie>> getSambaListObservable() {
//        final Observable<List<Media>> listObservable = new SmbShareListInteractor()
//                .getListObservable(TestData.IP_ADDR, TestData.SHARE, TestData.PATH, TestData.USER, TestData.PASS);
//        return toMovieListObservable(listObservable);
//    }
//
//    @NonNull
//    private static Observable<List<Movie>> toMovieListObservable(final Observable<List<Media>> listObservable) {
//        return listObservable
//                .observeOn(Schedulers.io())
//                .flatMap((medias) -> Observable.from(medias))
//                .map((media) -> MovieMapper.buildMovieInfo(
//                        "category",
//                        media.title(),
//                        "description of movie",
//                        FileUtils.getExt(media.url()),
//                        media.url(),
//                        "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png",
//                        "http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg")
//                )
//                .doOnNext((movie) -> list.add(movie))
//                .toList();
//    }
//
//    @NonNull
//    private static Observable<List<Movie>> getLocalPodcastListObservable() {
//        final File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
//        final Observable<List<Media>> listObservable = new LocalFileListInteractor().getListObservable(externalStoragePublicDirectory);
//        return toMovieListObservable(listObservable);
//    }
//
//    @NonNull
//    private static Observable<List<Movie>> getTestRemoteListObservable() {
//
//        final String[] title = {
//                "Zeitgeist 2010_ Year in Review",
//                "Google Demo Slam_ 20ft Search",
//                "Introducing Gmail Blue",
//                "Introducing Google Fiber to the Pole",
//                "Introducing Google Nose"
//        };
//
//        final String description = "Fusce id nisi turpis. Praesent viverra bibendum semper. "
//                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
//                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
//                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
//                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
//                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";
//
//        final String[] videoUrl = {
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
//        };
//        final String[] bgImageUrl = {
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/bg.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/bg.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/bg.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",
//        };
//        final String[] cardImageUrl = {
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
//                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
//        };
//
//        list.add(MovieMapper.buildMovieInfo("category", title[0],
//                description, "Studio Zero", videoUrl[0], cardImageUrl[0], bgImageUrl[0]));
//        list.add(MovieMapper.buildMovieInfo("category", title[1],
//                description, "Studio One", videoUrl[1], cardImageUrl[1], bgImageUrl[1]));
//        list.add(MovieMapper.buildMovieInfo("category", title[2],
//                description, "Studio Two", videoUrl[2], cardImageUrl[2], bgImageUrl[2]));
//        list.add(MovieMapper.buildMovieInfo("category", title[3],
//                description, "Studio Three", videoUrl[3], cardImageUrl[3], bgImageUrl[3]));
//        list.add(MovieMapper.buildMovieInfo("category", title[4],
//                description, "Studio Four", videoUrl[4], cardImageUrl[4], bgImageUrl[4]));
//        return Observable.just(list);
//    }
//
////    @NonNull
////    private static Observable<List<Movie>> getTestFileListObservable() {
////        list.add(buildMovieInfo(
////                "category",
////                MainActivity.TEST_MOVIE_FILE.getName(),
////                "description of movie: " +
////                        MainActivity.TEST_MOVIE_FILE.getAbsolutePath() + ":" +
////                        MainActivity.TEST_MOVIE_FILE.exists() + ":" +
////                        getFreeSpace() + " free",
////                "Studio Zero",
////                Uri.fromFile(MainActivity.TEST_MOVIE_FILE).toString(),
////                "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Player-Video-icon.png",
////                "http://www.freegreatpicture.com/files/147/14928-high-resolution-color-background.jpg"));
////        return Observable.just(list);
////    }



}

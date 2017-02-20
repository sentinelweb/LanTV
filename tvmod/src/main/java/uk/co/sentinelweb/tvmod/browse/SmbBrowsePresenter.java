package uk.co.sentinelweb.tvmod.browse;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import co.uk.sentinelweb.lantv.net.smb.TestData;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;
import co.uk.sentinelweb.lantv.net.smb.url.SmbUrlBuilder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.browse.comparator.CategoryMovieRowItemTitleComparator;
import uk.co.sentinelweb.tvmod.browse.comparator.MediaTitleSortComparator;
import uk.co.sentinelweb.tvmod.mapper.CategoryMapper;
import uk.co.sentinelweb.tvmod.mapper.MovieMapper;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.Extension;
import uk.co.sentinelweb.tvmod.util.FileUtils;
import uk.co.sentinelweb.tvmod.util.HiddenFiles;

import static co.uk.sentinelweb.lantv.util.StringUtils.concat;
import static co.uk.sentinelweb.lantv.util.StringUtils.isBlank;

public class SmbBrowsePresenter implements SmbBrowseMvpContract.Presenter {
    public static final int LARGE_FILE_TEST_SIZE = 50 * 1024 * 1024;
    private final SmbBrowseMvpContract.View view;
    private final MovieMapper _movieMapper;
    private final CategoryMapper _categoryMapper;
    private final SmbShareListInteractor _smbShareListInteractor;
    //private final SmbFileReadInteractor smbFileReadInteractor;

    private CompositeSubscription _subscription;
    private SmbBrowseFragmentModel model;

    private SmbLocation location;

    private List<Media> topLevelList;

    public SmbBrowsePresenter(final SmbBrowseMvpContract.View view) {
        this.view = view;
        _movieMapper = new MovieMapper();
        _categoryMapper = new CategoryMapper();
        _smbShareListInteractor = new SmbShareListInteractor();
        //smbFileReadInteractor = new SmbFileReadInteractor();
    }

    @Override
    public void setupData(final SmbLocation location) {
        this.location = location;
        String title = "Loading";
        if (!isBlank(location.getDirname())) {
            title = location.getDirname();
        } else if (!isBlank(location.getShareName())) {
            title = location.getShareName();
        } else if (!isBlank(location.getIpAddr())) {
            title = location.getIpAddr();
        }
        model = new SmbBrowseFragmentModel(location, title, new ArrayList<>());
    }

    @Override
    public void subscribe() {
        model.getCategories().clear();
        model.getCategories().add(C.PARENT_CATEGORY);
        final Subscription _loadSubscriber = getSambaListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((model) -> view.setData(model),
                        (throwable) -> Log.d(SmbBrowsePresenter.class.getSimpleName(), "Load error:", throwable),
                        () -> {
                            // check for low level directory
                            if (model.getCategories().size() == 1) {// has parent dir
                                checkFoLaunchableMediaInShallowDir(topLevelList);
                            }
                        }
                );
        final Subscription _timerSubscription = Observable.timer(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movies) -> view.updateBackGround(),
                        (throwable) -> Log.d(SmbBrowsePresenter.class.getSimpleName(), "Timer error:", throwable),
                        () -> {
                        }
                );
        _subscription = new CompositeSubscription(_loadSubscriber, _timerSubscription);
    }

    private void checkFoLaunchableMediaInShallowDir(final List<Media> topLevelList) {
        boolean hasSubdirectories = false;
        Media largestMediaFile = null;
        long largestMediaFileSize = 0;
        long largeFileCount = 0;
        for (final Media m : topLevelList) {
            hasSubdirectories |= m.isDirectory();
            if (m.size() != null && m.size() > largestMediaFileSize) {
                largestMediaFile = m;
                largestMediaFileSize = m.size();

            }
            if (m.size() > LARGE_FILE_TEST_SIZE) {
                largeFileCount++;
            }

        }
        if (!hasSubdirectories &&
                largeFileCount ==1 &&
                largestMediaFile != null &&
                Extension.shouldDisplay(FileUtils.getExt(largestMediaFile.url())) &&
                largestMediaFile.size() > LARGE_FILE_TEST_SIZE) {
            final Movie movie = _movieMapper.map(largestMediaFile);
            if (movie != null) {
                launchMovie(movie);
                view.finish();
            }
        } else {
            // load the list
            addToMovieListObservable(Observable.just(topLevelList))
                    .subscribe((model) -> view.setData(model),
                            (throwable) -> Log.d(SmbBrowsePresenter.class.getSimpleName(), "Load error:", throwable),
                            () -> {});
        }
    }

    @Override
    public void unsubscribe() {
        if (_subscription != null) {
            _subscription.unsubscribe();
            _subscription = null;
        }
    }

    @NonNull
    private Observable<SmbBrowseFragmentModel> getSambaListObservable() {
        final Observable<List<Media>> map =
                _smbShareListInteractor
                        .getListObservable(location)
                        .observeOn(Schedulers.io())// on bg thread
                        .doOnNext((medialist) -> topLevelList = medialist)
                        .map((medialist) -> new MediaTitleSortComparator().sort(medialist))// sort the list by title
                        .flatMap((medialist) -> Observable.from(medialist))// read subdirs
                        .filter((media -> media.isDirectory() && !HiddenFiles.isExcludedUrl(media.url())))// remove exclusions & files
                        .map((media -> media.url().substring(media.url().indexOf(TestData.SHARE) + TestData.SHARE.length() + 1)))// make url TODO use parser
                        .doOnNext(path -> Log.d(SmbBrowsePresenter.class.getSimpleName(), "path:" + path))// log
                        //.onErrorResumeNext((throwable)-> {Log.d(MainMvpPresenter.class.getSimpleName(), "error getting directory:", throwable);}, () ->{})
                        .map((path) -> _smbShareListInteractor.getList(TestData.IP_ADDR, TestData.SHARE, path, TestData.USER, TestData.PASS));// TODO use SmbLocation
        return addToMovieListObservable(map);
    }

    @NonNull
    private Observable<SmbBrowseFragmentModel> addToMovieListObservable(final Observable<List<Media>> listObservable) {
        return listObservable
                .map((medias) -> _movieMapper.mapList(medias))// map media -> movie
                .filter((movies) -> movies.size() > 0)// remove empties
                .map((list) -> _categoryMapper.map(list.get(0).getCategory(), list)) // make category
                .map((category) -> addCurrentDirMovie(category)) // add . link
                .map((category) -> new CategoryMovieRowItemTitleComparator().sort(category))// sort the row by title
                .observeOn(AndroidSchedulers.mainThread()) // move to main thread
                .map((category) -> {// add category to list
                    model.getCategories().add(category);
                    return model;
                });
    }

    @NonNull
    private Category addCurrentDirMovie(final Category category) {
        final Movie currentDir = C.CURRRENT_DIR_MOVIE.clone();
        final Movie firstItem = category.movies().get(0);
        final SmbLocation location = new SmbLocationParser().parse(firstItem.getVideoUrl());
        if (C.DIR_EXTENSION.equals(firstItem.getExtension())) {
            location.setDirname(concat(location.getDirname().split("/"), "/", 0, -1));
        } else {
            location.setFileName(null);
        }
        currentDir.setVideoUrl(SmbUrlBuilder.build(location));
        category.movies().add(0, currentDir);
        return category;
    }

    @Override
    public void launchMovie(final Movie movie) {
        if (movie != null) {
            if (movie == C.PARENT_DIR_MOVIE) {
                final SmbLocation parent = getParentLocation();
                if (parent != null) {
                    view.launchBrowser(parent);
                }
            } else if (C.CURRENT_DIR_TITLE.equals(movie.getTitle())) {
                view.launchBrowser(new SmbLocationParser().parse(movie.getVideoUrl()));
            } else if (movie.getExtension().equals(C.DIR_EXTENSION)) {
                final SmbLocation newLocation = new SmbLocationParser().parse(movie.getVideoUrl());
                view.launchBrowser(newLocation);
            } else if (Extension.isSupported(movie.getExtension())) {
                view.launchExoplayer(movie);
            } else {
                //view.launchDetails(getLocationForSelectedMovie(movie), movie);
                //view.launchVlc(movie);
                view.launchMxPlayer(movie);

            }
        }
    }

    private SmbLocation getLocationForSelectedMovie(final Movie movie) {
        final SmbLocation childLocation = new SmbLocationParser().parse(movie.getVideoUrl());
        childLocation.setFileName(null);
        return childLocation;
    }

    private SmbLocation getParentLocation() {
        final SmbLocation parentLocation = location.clone();
        if (location.getShareName() == null) {
            view.showError("This is the top level");
            return null;
        } else if (isBlank(location.getDirname())) {
            parentLocation.setShareName(null);
            parentLocation.setDirname(null);
        } else {
            final String[] pathSegments = location.getDirname().split("/");
            parentLocation.setDirname(concat(pathSegments, "/", 0, -1));

        }
        return parentLocation;
    }

    // cache file and use VLC
//            final File bufferFile = view.getBufferFile(movie);
//            view.showDownloadDialog();
//            view.updateDownloadDialog("Downloading file:"+movie.getTitle());
//            Log.d(FileUtils.class.getSimpleName(), "Downloading file:"+movie.getTitle());
//            final Subscription subscription = smbFileReadInteractor
//                    .openFileObservable(movie.getVideoUrl(), TestData.USER, TestData.PASS)
//                    .observeOn(Schedulers.io())
//                    .doOnNext(inputStream -> FileUtils.copyFileFromStream(bufferFile, inputStream))
//                    .subscribeOn(Schedulers.io())
//                    .subscribe((inputStream) -> { },
//                            (throwable) -> view.showError(throwable),
//                            () -> {
//                                view.closeDownloadDialog();
//                                final Movie cachedMovie = new Movie();
//                                cachedMovie.setVideoUrl(Uri.fromFile(bufferFile).toString());
//                                view.launchVlc(movie);
//                            });
//            _subscription.add(subscription);

}

package uk.co.sentinelweb.tvmod.browse;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.SmbFileReadInteractor;
import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;
import co.uk.sentinelweb.lantv.net.smb.url.SmbUrlBuilder;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.browse.comparator.CategoryMovieRowItemTitleComparator;
import uk.co.sentinelweb.tvmod.browse.comparator.MediaTitleSortComparator;
import uk.co.sentinelweb.tvmod.mapper.CategoryMapper;
import uk.co.sentinelweb.tvmod.mapper.MovieMapper;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.util.Extension;
import uk.co.sentinelweb.tvmod.util.FileUtils;
import uk.co.sentinelweb.tvmod.util.HiddenFiles;
import uk.co.sentinelweb.tvmod.util.LanTvPreferences;

import static co.uk.sentinelweb.lantv.util.StringUtils.concat;
import static co.uk.sentinelweb.lantv.util.StringUtils.isBlank;

public class SmbBrowsePresenter implements SmbBrowseMvpContract.Presenter {
    public static final String TAG = SmbBrowsePresenter.class.getSimpleName();
    public static final int LARGE_FILE_TEST_SIZE = 50 * 1024 * 1024;
    private final SmbBrowseMvpContract.View view;
    private final MovieMapper _movieMapper;
    private final CategoryMapper _categoryMapper;
    private final SmbShareListInteractor _smbShareListInteractor;
    private final SmbFileReadInteractor _smbFileReadInteractor;
    final LanTvPreferences _preferences;
    private final SmbLocationParser _smbLocationParser = new SmbLocationParser();
    private final SmbUrlBuilder _smbUrlBuilder = new SmbUrlBuilder();

    private CompositeSubscription _subscription;
    private SmbBrowseFragmentModel model;

    private SmbLocation _location;

    private List<Media> topLevelList;
    private Media _mediaForLocation;

    @Inject
    public SmbBrowsePresenter(final SmbBrowseMvpContract.View view,
                              final MovieMapper movieMapper,
                              final CategoryMapper categoryMapper,
                              final SmbShareListInteractor smbShareListInteractor,
                              final SmbFileReadInteractor smbFileReadInteractor,
                              final LanTvPreferences preferences) {
        this.view = view;
        _movieMapper = movieMapper;
        _categoryMapper = categoryMapper;
        _smbShareListInteractor = smbShareListInteractor;
        _smbFileReadInteractor = smbFileReadInteractor;
        _preferences = preferences;
    }

    @Override
    public void onStart() {
        //_preferences.saveLastLocation(this._location);
    }

    @Override
    public void setupData(final SmbLocation location) {
        if (location != null) {
            this._location = location;
        } else {
            this._location = _preferences.getLastLocation();
        }
        String title = "Loading";
        if (!isBlank(_location.getDirname())) {
            title = _location.getDirname();
        } else if (!isBlank(_location.getShareName())) {
            title = _location.getShareName();
        } else if (!isBlank(_location.getIpAddr())) {
            title = _location.getIpAddr();
        } else {
            title = "Network";
        }
        model = new SmbBrowseFragmentModel(_location, title, new ArrayList<>());
    }

    @Override
    public void subscribe() {
        model.getCategories().clear();
        view.showLoading(true);

        final Subscription mediaSubscription = getSambaMediaObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::loadSmbList, getErrorAction("Timer error:"));

        final Subscription timerSubscription = Observable.timer(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movies) -> view.updateBackGround(),
                        getErrorAction("Timer error:"),
                        () -> {
                        }
                );
        _subscription = new CompositeSubscription(mediaSubscription, timerSubscription);
    }

    @NonNull
    public Action1<Throwable> getErrorAction(final String prefix) {
        return (throwable) -> {
            Log.d(TAG, prefix, throwable);
            view.showLoading(false);
            view.showError(prefix+throwable.getMessage());
        };
    }

    public void loadSmbList(final Media m) {
        model.getCategories().add(C.PARENT_CATEGORY);
        _mediaForLocation=m;
        _location = _smbLocationParser.parse(m.url());// updates location with auth
        final Subscription subscribe = getSambaListObservable(_location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((model) -> {
                            view.setData(model);
                            Log.d(TAG, "setModel:" + model.getCategories().size());
                        },
                        getErrorAction("Load error:"),
                        () -> {
                            // check for low level directory
                            if (model.getCategories().size() == 1) {// has parent dir
                                checkFoLaunchableMediaInShallowDir(topLevelList);
                            } else {
                                view.showLoading(false);
                            }
                        }
                );
        _subscription.add(subscribe);// todo unsub later
    }

    @NonNull
    public Media addCredentialsForComputer(Media m) {
        if (m != null && m.isComputer()) {
            final SmbLocation parse = _smbLocationParser.parse(m.url());
            parse.setUsername(_preferences.getUserName());
            parse.setPassword(_preferences.getPassword());
            final String urlWithCredentials = _smbUrlBuilder.build(parse);
            m = Media.mutateUrl(urlWithCredentials, m);
        }
        return m;
    }

    @Override
    public void unsubscribe() {
        if (_subscription != null) {
            _subscription.unsubscribe();
            _subscription = null;
        }
    }
    @NonNull
    private Single<Media> getSambaMediaObservable() {
        return _smbFileReadInteractor.getMediaObservable(_location)
                .doOnSuccess((m) -> Log.d(TAG, "topmedia:" + m))// log
                .map(this::addCredentialsForComputer)
                .doOnSuccess((m) -> Log.d(TAG, "topmedia w auth:" + m));
    }
    @NonNull
    private Observable<SmbBrowseFragmentModel> getSambaListObservable(final SmbLocation smbLocation) {
        Log.d(TAG, new SmbUrlBuilder().build(smbLocation));
        Observable<List<Media>> listObservable;
        listObservable = _smbShareListInteractor.getListObservable(_location)
                .observeOn(Schedulers.io())
                .doOnNext((medialist) -> topLevelList = medialist)// on bg thread;
                .doOnNext((medialist) -> {
                    for (final Media m : medialist) {
                        Log.d(TAG, "toppath:" +isTopLevel()+":"+ m.url());
                    }
                });
        if (!isTopLevel()) {
            listObservable =
                    listObservable.map((medialist) -> new MediaTitleSortComparator().sort(medialist))// sort the list by title
                    .flatMap((medialist) -> Observable.from(medialist))// read subdirs
                    .filter((media -> media.isDirectory() && !HiddenFiles.isExcludedUrl(media.url())))// remove exclusions & files
                    .map((media) -> _smbLocationParser.parse(media.url()))
                    .doOnNext(location -> Log.d(TAG, "subpath:" + _smbUrlBuilder.build(location)))// log
                    //.onErrorResumeNext((throwable)-> {Log.d(MainMvpPresenter.class.getSimpleName(), "error getting directory:", throwable);}, () ->{})
                    .map((location) -> _smbShareListInteractor.getList(location));
        }
        Log.d(TAG, "return");
        return addToMovieListObservable(listObservable);
    }

    private boolean isTopLevel() {
        return _mediaForLocation.isWorkgroup()||_mediaForLocation.isComputer();
    }

    @NonNull
    private Observable<SmbBrowseFragmentModel> addToMovieListObservable(final Observable<List<Media>> listObservable) {
        return listObservable
                .observeOn(Schedulers.io())// on bg thread
                .map((medias) -> _movieMapper.mapList(medias))// map media -> movie
                .filter((movies) -> movies.size() > 0)// remove empties
                .map((list) -> _categoryMapper.map(list.get(0).getCategory(), list)) // make category
                .map((category) -> addCurrentDirItem(category)) // add . link
                .map((category) -> new CategoryMovieRowItemTitleComparator().sort(category))// sort the row by title
                .observeOn(AndroidSchedulers.mainThread()) // move to main thread
                .map((category) -> {// add category to list
                    model.getCategories().add(category);
                    return model;
                });
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
        if (!isTopLevel() &&
                !hasSubdirectories &&
                largeFileCount == 1 &&
                largestMediaFile != null &&
                Extension.shouldDisplay(FileUtils.getExt(largestMediaFile.url())) &&
                largestMediaFile.size() > LARGE_FILE_TEST_SIZE) {
            final Item item = _movieMapper.map(largestMediaFile);
            if (item != null) {
                launchMovie(item);
                view.finishActivity();
            }
        } else {
            // load the list
            final Subscription subscribe = addToMovieListObservable(Observable.just(topLevelList))
                    .subscribeOn(Schedulers.io())
                    .subscribe((model) -> view.setData(model),
                            getErrorAction("Load error:"),
                            () -> {
                                view.showLoading(false);
                            });
            _subscription.add(subscribe);
        }
    }

    @NonNull
    private Category addCurrentDirItem(final Category category) {
        if (!isTopLevel()) {
            final Item currentDir = C.CURRRENT_DIR_ITEM.clone();
            final Item firstItem = category.items().get(0);
            final SmbLocation location = new SmbLocationParser().parse(firstItem.getVideoUrl());
            if (firstItem.getExtension()==Extension.DIR) {
                final String dirname = location.getDirname();
                if (dirname != null) {
                    location.setDirname(concat(dirname.split("/"), "/", 0, -1));
                } else {
                    return category;// don't add . item
                }
            } else {
                location.setFileName(null);
            }
            currentDir.setVideoUrl(_smbUrlBuilder.build(location));
            category.items().add(0, currentDir);
        }
        return category;
    }

    @Override
    public void launchMovie(final Item item) {
        if (item != null) {
            if (item == C.PARENT_DIR_ITEM) {
                final SmbLocation parent = getParentLocation();
                if (parent != null) {
                    view.launchBrowser(parent);
                }
            } else if (C.CURRENT_DIR_TITLE.equals(item.getTitle())) {
                view.launchBrowser(_smbLocationParser.parse(item.getVideoUrl()));
            } else if (item.getExtension()==Extension.DIR) {
                loadDirectory(item);
            } else if (item.getExtension()==Extension.WKGP || item.getExtension()==Extension.SRVR || item.getExtension()==Extension.SHARE) {
                loadDirectory(item);
            } else if (item.getExtension()==Extension.PRN) {
                view.showError("Printers are unsupported");
            } else if ((item.getExtension().getSupported())) {
                view.launchExoplayer(item);
            } else {
                //view.launchDetails(getLocationForSelectedMovie(movie), movie);
                view.launchVlc(item);
                //view.launchMxPlayer(item);
            }
        }
    }

    @Override
    public void loadDirectory(final Item item) {
        final SmbLocation newLocation = new SmbLocationParser().parse(item.getVideoUrl());
        view.launchBrowser(newLocation);
    }

    @Override
    public void launchDetails(final Item m) {
        view.launchDetails(getLocationForSelectedMovie(m), m);
    }


    private SmbLocation getLocationForSelectedMovie(final Item item) {
        final SmbLocation childLocation = new SmbLocationParser().parse(item.getVideoUrl());
        childLocation.setFileName(null);
        return childLocation;
    }

    private SmbLocation getParentLocation() {
        final SmbLocation parentLocation = _location.clone();
        if (_location.getShareName() == null) {
            view.showError("This is the top level");
            return null;
        } else if (isBlank(_location.getDirname())) {
            parentLocation.setShareName(null);
            parentLocation.setDirname(null);
        } else {
            final String[] pathSegments = _location.getDirname().split("/");
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
//                                view.launchVlcSmb(movie);
//                            });
//            _subscription.add(subscription);

}

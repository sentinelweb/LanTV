package uk.co.sentinelweb.tvmod.browse;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import co.uk.sentinelweb.lantv.net.smb.TestData;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.sentinelweb.tvmod.mapper.CategoryMapper;
import uk.co.sentinelweb.tvmod.mapper.MovieMapper;

public class MainMvpPresenter implements MainMvpContract.Presenter {
    private CompositeSubscription _subscription;
    private final MainMvpContract.View view;
    MovieMapper _movieMapper = new MovieMapper();
    CategoryMapper _categoryMapper = new CategoryMapper();
    MainFragmentModel model;

    public MainMvpPresenter(final MainMvpContract.View view) {
        this.view = view;
        model = new MainFragmentModel(new ArrayList<>());
    }

    @Override
    public void subscribe() {
        model._categories.clear();
        final Subscription _loadSubscriber = getSambaListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movies) -> view.setData(movies),
                        (throwable) -> {
                        },
                        () -> {
                        }
                );
        final Subscription _timerSubscription = Observable.timer(30, TimeUnit.SECONDS)
                .subscribe((movies) -> view.updateBackGround(),
                        (throwable) -> {
                        },
                        () -> {
                        }
                );
        _subscription = new CompositeSubscription(_loadSubscriber, _timerSubscription);
    }

    @Override
    public void unsubscribe() {
        if (_subscription != null) {
            _subscription.unsubscribe();
            _subscription = null;
        }
    }

    @NonNull
    private Observable<MainFragmentModel> getSambaListObservable() {
        final SmbShareListInteractor smbShareListInteractor = new SmbShareListInteractor();

        final Observable<List<Media>> map =
                //Observable.just("superhero/", "fantasy/", "action/", "sci-fi/")// TODO list top dir
                smbShareListInteractor.getListObservable(TestData.IP_ADDR, TestData.SHARE, TestData.PATH, TestData.USER, TestData.PASS)
                        .observeOn(Schedulers.io())
                        .flatMap((directoryList) -> Observable.from(directoryList))
                        .filter((media -> media.isDirectory() && media.url().indexOf(".DS_Store")==-1))
                        .map((media -> media.url().substring(media.url().indexOf(TestData.SHARE)+TestData.SHARE.length()+1)))
                        .doOnNext(path -> Log.d(MainMvpPresenter.class.getSimpleName(), "path:" + path))
                        //.onErrorResumeNext((throwable)-> {Log.d(MainMvpPresenter.class.getSimpleName(), "error getting directory:", throwable);}, () ->{})
                        .map((path) -> smbShareListInteractor.getList(TestData.IP_ADDR, TestData.SHARE, path, TestData.USER, TestData.PASS));

//        final Observable<List<Media>> listObservable = smbShareListInteractor
//                .getListObservable(TestData.IP_ADDR, TestData.SHARE, TestData.PATH, TestData.USER, TestData.PASS);
        return toMovieListObservable(map);
    }

    @NonNull
    private Observable<MainFragmentModel> toMovieListObservable(final Observable<List<Media>> listObservable) {
        return listObservable
                .map((medias) -> _movieMapper.mapList(medias))
                .map((list) -> {
                    model._categories.add(_categoryMapper.map(list.get(0).getCategory(), list));
                    return model;
                });
    }
}

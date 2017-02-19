package uk.co.sentinelweb.tvmod.details;

import android.util.Log;

import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.sentinelweb.tvmod.browse.SmbBrowsePresenter;
import uk.co.sentinelweb.tvmod.mapper.CategoryMapper;
import uk.co.sentinelweb.tvmod.mapper.MovieMapper;
import uk.co.sentinelweb.tvmod.model.Movie;

/**
 * Created by robert on 18/02/2017.
 */
public class DetailsPresenter implements DetailsMvpContract.Presenter {
    private final SmbShareListInteractor _smbShareListInteractor;
    private final MovieMapper _movieMapper;
    private final CategoryMapper _categoryMapper;
    private final DetailsMvpContract.View _view;

    private CompositeSubscription _subscription;
    private DetailsFragmentModel _model;

    public DetailsPresenter(final DetailsMvpContract.View view) {
        _view = view;
        _movieMapper = new MovieMapper();
        _categoryMapper = new CategoryMapper();
        _smbShareListInteractor = new SmbShareListInteractor();

    }

    @Override
    public void subscribe() {
        final Subscription _loadSubscriber = getCategoryObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((model) -> _view.setData(model),
                        (throwable) -> Log.d(SmbBrowsePresenter.class.getSimpleName(), "Load error:", throwable),
                        () -> {
                        }
                );
        _subscription = new CompositeSubscription(_loadSubscriber);
    }

    private Observable<DetailsFragmentModel> getCategoryObservable() {
        return _smbShareListInteractor.getListObservable(_model.getLocation())
                .observeOn(Schedulers.io())
                .map((medias) -> _movieMapper.mapList(medias))
                .map((list) -> {
                    _model.setCategory(_categoryMapper.map(/*name*/list.get(0).getCategory(), list));
                    return _model;
                });
    }


    @Override
    public void unsubscribe() {
        if (_subscription!=null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
            _subscription = null;
        }
    }

    @Override
    public void setupData(final SmbLocation location, final Movie m) {
        _model = new DetailsFragmentModel();
        _model.setLocation(location);
        _model.setMovie(m);
    }
}

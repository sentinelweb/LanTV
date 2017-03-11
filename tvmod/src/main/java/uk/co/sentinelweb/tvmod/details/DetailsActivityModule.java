package uk.co.sentinelweb.tvmod.details;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
/**
 * Created by robert on 10/03/2017.
 */
@Module(includes = DetailsActivityModule.Bindings.class)
public class DetailsActivityModule {
    private final DetailsActivity _activity;
    private final DetailsFragment _fragment;

    public DetailsActivityModule(final DetailsActivity activity, final DetailsFragment fragment) {
        _activity = activity;
        _fragment = fragment;
    }

    @Provides
    DetailsActivity provideActivity() {
        return _activity;
    }

    @Provides
    DetailsFragment provideFragment() {
        return _fragment;
    }

    @Module
    public interface Bindings{
        @Binds
        DetailsMvpContract.View provideView(DetailsFragment f) ;

        @Binds
        DetailsMvpContract.Presenter providePresenter(final DetailsPresenter pres) ;

        @Binds
        Activity provideActivity(DetailsActivity activity);
    }

}

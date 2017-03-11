package uk.co.sentinelweb.tvmod.browse;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by robert on 10/03/2017.
 */
@Module(includes = SmbBrowseActivityModule.Bindings.class)
public class SmbBrowseActivityModule {
    private final SmbBrowseActivity _activity;
    private final SmbBrowseFragment _fragment;

    public SmbBrowseActivityModule(final SmbBrowseActivity activity, final SmbBrowseFragment fragment) {
        _activity = activity;
        _fragment = fragment;
    }

    @Provides
    SmbBrowseActivity provideActivity() {
        return _activity;
    }

    @Provides
    SmbBrowseFragment provideFragment() {
        return _fragment;
    }

    @Module
    public interface Bindings{
        @Binds
        SmbBrowseMvpContract.View provideView(SmbBrowseFragment f) ;

        @Binds
        SmbBrowseMvpContract.Presenter providePresenter(final SmbBrowsePresenter pres) ;

        @Binds
        Activity provideActivity(SmbBrowseActivity activity);
    }

}

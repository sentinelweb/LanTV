package uk.co.sentinelweb.tvmod;

import javax.inject.Singleton;

import dagger.Component;
import uk.co.sentinelweb.tvmod.browse.SmbBrowseActivityComponent;
import uk.co.sentinelweb.tvmod.browse.SmbBrowseActivityModule;
import uk.co.sentinelweb.tvmod.details.DetailsActivityComponent;
import uk.co.sentinelweb.tvmod.details.DetailsActivityModule;

/**
 * Created by robert on 10/03/2017.
 */
@Singleton
@Component(modules = {AppModule.class, SmbModule.class})
public interface AppComponent {
    public void inject(LanTvApplication app);

    @ActivityScope
    public SmbBrowseActivityComponent plus(SmbBrowseActivityModule module);

    @ActivityScope
    public DetailsActivityComponent plus(DetailsActivityModule module);
}

package uk.co.sentinelweb.tvmod;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import uk.co.sentinelweb.tvmod.util.WebProxyManager;

/**
 * Created by robert on 10/03/2017.
 */

@Module(includes = AppModule.Bindings.class)
public class AppModule {

    LanTvApplication app;

    public AppModule(final LanTvApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public LanTvApplication provideApp() {
        return app;
    }

    @Provides
    @Singleton
    public WebProxyManager provideWebProxyManager() {
        return new WebProxyManager(app);
    }

    @Module
    public interface Bindings {

        @Binds
        Application provideApplication(LanTvApplication app);
    }
}

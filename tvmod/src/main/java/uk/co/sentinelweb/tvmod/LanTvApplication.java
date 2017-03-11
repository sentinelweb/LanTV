package uk.co.sentinelweb.tvmod;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import uk.co.sentinelweb.microserver.server.WebServerConfig;
import uk.co.sentinelweb.microservice.cp.AssetCommandProcessor;
import uk.co.sentinelweb.microservice.cp.DrawableCommandProcessor;
import uk.co.sentinelweb.tvmod.microserver.SmbStreamCommandProcessor;

import static uk.co.sentinelweb.microservice.MicroService.setWebServerConfig;

/**
 * Created by robert on 18/02/2017.
 */

public class LanTvApplication extends Application {

    static {
        jcifs.Config.registerSmbURLHandler();
    }

    private AppComponent _appComponent;

    public AppComponent getAppComponent() {
        return _appComponent;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .smbModule(new SmbModule())
                .build();

        final WebServerConfig config = new WebServerConfig.Builder()
                .addProcessor(new SmbStreamCommandProcessor(/*"/s/"*/))
                .addProcessor(new AssetCommandProcessor("/a/", this))
                .addProcessor(new DrawableCommandProcessor("/d/", this))
                .build();
        setWebServerConfig(config);
    }
}

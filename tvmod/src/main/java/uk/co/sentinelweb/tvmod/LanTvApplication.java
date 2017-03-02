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

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final WebServerConfig config = new WebServerConfig.Builder()
//                .addProcessor(new PingCommandProcessor("/ping"))
                .addProcessor(new SmbStreamCommandProcessor(/*"/s/"*/))
                .addProcessor(new AssetCommandProcessor("/a/", this))
                .addProcessor(new DrawableCommandProcessor("/d/", this))
//                .addForward("/favicon.ico", "/d/mipmap/ic_launcher")
//                .addRedirect("/google", "http://www.google.com")
                .build();
        setWebServerConfig(config);
    }
}

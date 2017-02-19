package uk.co.sentinelweb.tvmod;

import android.app.Application;

/**
 * Created by robert on 18/02/2017.
 */

public class LanTvApplication extends Application {

    static {
        jcifs.Config.registerSmbURLHandler();
    }
}

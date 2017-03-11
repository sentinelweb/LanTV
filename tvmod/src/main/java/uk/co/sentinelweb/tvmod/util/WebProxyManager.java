package uk.co.sentinelweb.tvmod.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.URLEncoder;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action2;
import uk.co.sentinelweb.microservice.MicroService;
import uk.co.sentinelweb.tvmod.LanTvApplication;
import uk.co.sentinelweb.tvmod.model.Item;

/**
 * Created by robert on 10/03/2017.
 */

public class WebProxyManager {
    public static final String TAG = WebProxyManager.class.getSimpleName();
    public static final String PROXY_BASE = "http://localhost:4443/s/";
    private final Context c;
    private Subscription _serviceSubscribe;

    @Inject
    public WebProxyManager(final LanTvApplication c) {
        this.c = c;
    }

    /**
     * @param launchAction target Launcher
     */
    public void launchProxyMovieAction(final Item m, final Action2<Item, Uri> launchAction) {
        unsubscribe();
        c.startService(MicroService.getStartIntent(c));
        _serviceSubscribe = MicroService.statusPublishSubject.subscribe((status) -> {
            if (status == MicroService.Status.STARTED) {
                final String proxyUriString = PROXY_BASE + URLEncoder.encode(m.getVideoUrl());
                Log.d(TAG, "Server started: proxying:" + proxyUriString);
                final Uri proxyUrl = Uri.parse(proxyUriString);
                launchAction.call(m, proxyUrl);
            }
        }, (throwable) -> Log.d(TAG, "Error starting server", throwable));
    }

    public void unsubscribe() {
        if (_serviceSubscribe!=null && !_serviceSubscribe.isUnsubscribed()) {
            _serviceSubscribe.unsubscribe();
        }
    }
}

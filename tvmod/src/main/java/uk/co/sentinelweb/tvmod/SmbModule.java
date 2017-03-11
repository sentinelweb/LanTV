package uk.co.sentinelweb.tvmod;

import javax.inject.Singleton;

import co.uk.sentinelweb.lantv.net.smb.SmbFileReadInteractor;
import co.uk.sentinelweb.lantv.net.smb.SmbShareListInteractor;
import dagger.Module;
import dagger.Provides;

/**
 * Module for Smb objects
 * Created by robert on 10/03/2017.
 */
@Module
public class SmbModule {

    @Provides
    @Singleton
    public SmbShareListInteractor provideSmbShareListInteractor() {
        return new SmbShareListInteractor();
    }

    @Provides
    @Singleton
    public SmbFileReadInteractor provideFileReadInteractor() {
        return new SmbFileReadInteractor();
    }
}

package uk.co.sentinelweb.tvmod.exoplayer.upstream;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;


/**
 * Created by robertm on 16/02/2017.
 */

public class NetworkDataSourceFactory implements DataSource.Factory {

    private final HttpDataSource.BaseFactory _httpDataSourceFactory;
    private final SmbDataSourceFactory _smbDataSourceFactory;

    public NetworkDataSourceFactory(HttpDataSource.BaseFactory httpDataSourceFactory, SmbDataSourceFactory smbDataSourceFactory) {
        this._httpDataSourceFactory = httpDataSourceFactory;
        this._smbDataSourceFactory = smbDataSourceFactory;
    }

    @Override
    public DataSource createDataSource() {
        return new NetworkDataSource(_httpDataSourceFactory.createDataSource(), (SmbDataSource) _smbDataSourceFactory.createDataSource());
    }
}

package uk.co.sentinelweb.tvmod.exoplayer.upstream;

import com.google.android.exoplayer2.upstream.DataSource;

/**
 * TODO pass in usrname and password config?
 */
public class SmbDataSourceFactory implements DataSource.Factory {

    @Override
    public DataSource createDataSource() {
        return new SmbDataSource(null/*listener - not needed*/);
    }
}
package uk.co.sentinelweb.tvmod.exoplayer.upstream;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;

/**
 * Created by robertm on 16/02/2017.
 */

public class SmbDataSourceFactory implements DataSource.Factory {

    @Override
    public DataSource createDataSource() {
        return new SmbDataSource(null/*listener - not needed*/);
    }
}
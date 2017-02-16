package uk.co.sentinelweb.tvmod.exoplayer.upstream;

import android.net.Uri;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.io.IOException;

/**
 * Created by robertm on 16/02/2017.
 */
public class NetworkDataSource implements DataSource {
    public static final String SMB_PROTO = "smb";
    public static final String HTTP_PROTO = "http";

    private DataSource _selectedDataSource;
    final HttpDataSource _httpDataSource;
    final SmbDataSource _smbDataSource;

    public NetworkDataSource(HttpDataSource httpDataSource, SmbDataSource smbDataSource) {
        this._httpDataSource = httpDataSource;
        this._smbDataSource = smbDataSource;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        String scheme = dataSpec.uri.getScheme();
        switch (scheme) {
            case SMB_PROTO:
                _selectedDataSource = _smbDataSource;
                break;
            case HTTP_PROTO:
                _selectedDataSource = _httpDataSource;
                break;
        }
        return _selectedDataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return _selectedDataSource.read(buffer, offset, readLength);
    }

    @Override
    public Uri getUri() {
        return _selectedDataSource == null ? null : _selectedDataSource.getUri();
    }

    @Override
    public void close() throws IOException {
        if (_selectedDataSource != null) {
            try {
                _selectedDataSource.close();
            } finally {
                _selectedDataSource = null;
            }
        }
    }
}

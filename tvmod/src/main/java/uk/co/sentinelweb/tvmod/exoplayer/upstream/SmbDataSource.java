/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.sentinelweb.tvmod.exoplayer.upstream;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import co.uk.sentinelweb.lantv.net.smb.TestData;
import jcifs.smb.SmbFile;

/**
 * A {@link DataSource} for reading local files.
 */
public final class SmbDataSource implements DataSource {

    /**
     * Thrown when IOException is encountered during local file read operation.
     */
    public static class SmbDataSourceException extends IOException {

        public SmbDataSourceException(final IOException cause) {
            super(cause);
        }

    }

    private final TransferListener<? super SmbDataSource> listener;
    private SmbFile file;
    private Uri uri;
    private InputStream inputStream;
    private long bytesRemaining;
    private boolean opened;

    /**
     * @param listener An optional listener.
     */
    public SmbDataSource(final TransferListener<? super SmbDataSource> listener) {
        this.listener = listener;
    }

    @Override
    public long open(final DataSpec dataSpec) throws SmbDataSourceException {
        try {
            uri = dataSpec.uri;
//            if (uri.getQueryParameter("u") != null && uri.getQueryParameter("p") != null) {
//                jcifs.Config.setProperty("jcifs.smb.client.username", uri.getQueryParameter("u"));
//                jcifs.Config.setProperty("jcifs.smb.client.password", uri.getQueryParameter("p"));
//            }
            jcifs.Config.setProperty("jcifs.smb.client.username", TestData.USER);
            jcifs.Config.setProperty("jcifs.smb.client.password", TestData.PASS);

            file = new SmbFile(uri.toString());

            inputStream = file.getInputStream();
            final long skipped = inputStream.skip(dataSpec.position);
            if (skipped < dataSpec.position) {
                // assetManager.open() returns an AssetInputStream, whose skip() implementation only skips
                // fewer bytes than requested if the skip is beyond the end of the asset's data.
                throw new EOFException();
            }
            if (dataSpec.length != C.LENGTH_UNSET) {
                bytesRemaining = dataSpec.length;
            } else {
                bytesRemaining = file.getContentLength();//inputStream.available();
//                if (bytesRemaining == Integer.MAX_VALUE) {
//                    // assetManager.open() returns an AssetInputStream, whose available() implementation
//                    // returns Integer.MAX_VALUE if the remaining length is greater than (or equal to)
//                    // Integer.MAX_VALUE. We don't know the true length in this case, so treat as unbounded.
//                    bytesRemaining = C.LENGTH_UNSET;
//                }
            }
        } catch (final IOException e) {
            throw new SmbDataSourceException(e);
        }

        opened = true;
        if (listener != null) {
            listener.onTransferStart(this, dataSpec);
        }
        return bytesRemaining;
    }

    @Override
    public int read(final byte[] buffer, final int offset, final int readLength) throws AssetDataSource.AssetDataSourceException {
        if (readLength == 0) {
            return 0;
        } else if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        final int bytesRead;
        try {
            final int bytesToRead = bytesRemaining == C.LENGTH_UNSET ? readLength : (int) Math.min(bytesRemaining, readLength);
            bytesRead = inputStream.read(buffer, offset, bytesToRead);
        } catch (final IOException e) {
            throw new AssetDataSource.AssetDataSourceException(e);
        }

        if (bytesRead == -1) {
            if (bytesRemaining != C.LENGTH_UNSET) {
                // End of stream reached having not read sufficient data.
                throw new AssetDataSource.AssetDataSourceException(new EOFException());
            }
            return C.RESULT_END_OF_INPUT;
        }
        if (bytesRemaining != C.LENGTH_UNSET) {
            bytesRemaining -= bytesRead;
        }
        if (listener != null) {
            listener.onBytesTransferred(this, bytesRead);
        }
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() throws SmbDataSourceException {
        uri = null;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (final IOException e) {
            throw new SmbDataSourceException(e);
        } finally {
            inputStream = null;
            if (opened) {
                opened = false;
                if (listener != null) {
                    listener.onTransferEnd(this);
                }
            }
        }
    }
}

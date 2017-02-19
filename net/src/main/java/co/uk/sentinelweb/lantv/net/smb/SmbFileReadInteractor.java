package co.uk.sentinelweb.lantv.net.smb;

import java.io.InputStream;
import java.net.MalformedURLException;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbUrlBuilder;
import jcifs.smb.SmbFile;
import rx.Observable;

/**
 * Created by robert on 12/02/2017.
 */

public class SmbFileReadInteractor {
    public Observable<InputStream> openFileObservable(final SmbLocation location) {
        return Observable.defer(() -> Observable.just(openFile(location)));
    }

    public InputStream openFile(final SmbLocation location) {
//        jcifs.Config.setProperty("jcifs.smb.client.username", TestData.USER);
//        jcifs.Config.setProperty("jcifs.smb.client.password", TestData.PASS);
        final SmbFile file;
        final String url = SmbUrlBuilder.build(location);
        try {
            System.out.println("Samba: " + "smb file url: " + url);
            file = new SmbFile(url);
            final InputStream inputStream = file.getInputStream();
            return inputStream;
        } catch (final MalformedURLException e) {
            System.out.println("Samba: badurl" + url);
            e.printStackTrace();
        } catch (final Exception e) {
            System.out.println("Samba: exception" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

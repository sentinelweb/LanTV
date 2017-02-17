package co.uk.sentinelweb.lantv.net.smb;

import java.io.InputStream;
import java.net.MalformedURLException;

import jcifs.smb.SmbFile;
import rx.Observable;

/**
 * Created by robert on 12/02/2017.
 */

public class SmbFileReadInteractor {
    public Observable<InputStream> openFileObservable(final String url, final String username, final String password) {
        return Observable.defer(() -> Observable.just(openFile(url, username, password)));
    }

    public InputStream openFile(final String url, final String username, final String password) {
        jcifs.Config.setProperty("jcifs.smb.client.username", username);
        jcifs.Config.setProperty("jcifs.smb.client.password", password);
        final SmbFile file;

        try {
            System.out.println("Samba: " + "testing url: " + url);
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

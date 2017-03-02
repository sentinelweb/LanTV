package co.uk.sentinelweb.lantv.net.smb;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbUrlBuilder;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import rx.Observable;

public class SmbShareListInteractor {

    public Observable<List<Media>> getListObservable(final String ipAddr, final String shareName, final String dirname, final String username, final String password) {
        return Observable.defer(() -> Observable.just(getList(new SmbLocation(ipAddr, shareName, dirname, null, username, password))));
    }

    public Observable<List<Media>> getListObservable(final SmbLocation location) {
        return Observable.defer(() -> Observable.just(getList(location)));
    }

    public List<Media> getList(final String ipAddr, final String shareName, final String dirname, final String username, final String password) {
        return getList(new SmbLocation(ipAddr, shareName, dirname, null, username, password));
    }

    public List<Media> getList(final SmbLocation location) {
//        jcifs.Config.setProperty("jcifs.smb.client.username", username);
//        jcifs.Config.setProperty("jcifs.smb.client.password", password);

        //final String url = buildUrl(ipAddr, shareName, dirname, username, password);
        final String url = SmbUrlBuilder.build(location);
        return getList(url);
    }

    public List<Media> getList(final String url) {
        final SmbFile parent;
        SmbFile[] files = new SmbFile[0];

        final List<Media> list = new ArrayList<>();
        try {
            System.out.println("Samba: " + "testing url: " + url);
            parent = new SmbFile(url);

            final long t1 = System.currentTimeMillis();
            try {
                files = parent.listFiles();
                System.out.println("Samba: " + "list complete: " + url);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            final long t2 = System.currentTimeMillis() - t1;
            int fileCount = 0;
            int directoryCount = 0;
            for (int i = 0; i < files.length; i++) {
                try {
                    final SmbFile child = files[i];
                    list.add(Media.create(
                            url + child.getName(),
                            child.getName(),
                            child.length(),
                            new Date(child.lastModified()),
                            child.isDirectory(),
                            child.isFile()));
                    fileCount += child.isFile() ? 1 : 0;
                    directoryCount += child.isDirectory() ? 1 : 0;
                } catch (final SmbException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Samba: files:" + fileCount + " dirs:" + directoryCount + " total" + files.length + " files in " + t2 + "ms");
        } catch (final MalformedURLException e) {
            System.out.println("Samba: badurl" + url);
            e.printStackTrace();
        } catch (final Exception e) {
            System.out.println("Samba: exception" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

}

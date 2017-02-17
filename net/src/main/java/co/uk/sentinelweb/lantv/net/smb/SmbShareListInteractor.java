package co.uk.sentinelweb.lantv.net.smb;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import rx.Observable;
import rx.functions.Func0;

public class SmbShareListInteractor {

    public Observable<List<Media>> getListObservable(final String ipAddr, final String shareName, final String dirname, final String username, final String password) {
        return Observable.defer(new Func0<Observable<List<Media>>>() {
            @Override
            public Observable<List<Media>> call() {
                return Observable.just(getList(ipAddr, shareName, dirname, username, password));
            }
        });
    }

    public List<Media> getList(final String ipAddr, final String shareName, final String dirname, final String username, final String password) {
        jcifs.Config.setProperty("jcifs.smb.client.username", username);
        jcifs.Config.setProperty("jcifs.smb.client.password", password);
        final SmbFile parent;
        SmbFile[] files = new SmbFile[0];

        final String url = "smb://" + ipAddr + "/" +
                shareName + "/" +
                dirname + (!dirname.endsWith("/") ? "/" : "");
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
                    list.add(Media.create(url + child.getName(),
                            child.getName(),
                            child.length(),
                            new Date(child.lastModified()), child.isDirectory(), child.isFile()));// isDirectory is always true?

                    fileCount += child.isFile() ? 1 : 0;
                    directoryCount += child.isDirectory() ? 1 : 0;
                } catch (final SmbException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Samba: files:" + fileCount+" dirs:"+directoryCount+" total"+files.length + " files in " + t2 + "ms");
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

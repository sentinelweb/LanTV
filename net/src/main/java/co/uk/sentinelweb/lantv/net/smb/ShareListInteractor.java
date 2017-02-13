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

public class ShareListInteractor {

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
        final SmbFile file;
        SmbFile[] files = new SmbFile[0];

        final String url = "smb://" + ipAddr + "/" +
                shareName + "/" +
                dirname + (!dirname.endsWith("/") ? "/" : "");
        try {


            System.out.println("Samba: " + "testing url: " + url);
            file = new SmbFile(url);

            final long t1 = System.currentTimeMillis();
            try {
                files = file.listFiles();
                System.out.println("Samba: " + "list complete: " + url);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            final long t2 = System.currentTimeMillis() - t1;

            final List<Media> list = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                //System.out.println("Samba: " + files[i].getName());
                try {
                    if (files[i].isFile()) {
                        list.add(Media.create(url + files[i].getName(),
                                files[i].getName(),
                                files[i].length(),
                                new Date(files[i].lastModified())));
                    }
                } catch (final SmbException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Samba: " + files.length + " files in " + t2 + "ms");
            return list;

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

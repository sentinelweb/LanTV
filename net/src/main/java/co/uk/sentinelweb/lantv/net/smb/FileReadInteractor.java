package co.uk.sentinelweb.lantv.net.smb;

import java.io.InputStream;
import java.net.MalformedURLException;

import jcifs.smb.SmbFile;

/**
 * Created by robert on 12/02/2017.
 */

public class FileReadInteractor {
    public InputStream openFile(final String url, final String username, final String password) {
        jcifs.Config.setProperty("jcifs.smb.client.username", username);
        jcifs.Config.setProperty("jcifs.smb.client.password", password);
        final SmbFile file;

        try {
            System.out.println("Samba: " + "testing url: " + url);
            file = new SmbFile(url);

            return file.getInputStream();
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

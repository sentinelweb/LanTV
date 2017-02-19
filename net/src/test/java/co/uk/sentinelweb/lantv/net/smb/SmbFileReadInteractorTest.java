package co.uk.sentinelweb.lantv.net.smb;

import org.junit.Test;

import java.io.InputStream;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;

/**
 * Created by robert on 13/02/2017.
 */
public class SmbFileReadInteractorTest {

    @Test
    public void openFileSmbLocation() throws Exception {
        final SmbLocation location = new SmbLocation("192.168.0.13", "Drobo", "video/movies/superhero/", "dark_knight.iso.mp4",TestData.USER, TestData.PASS);//
        final InputStream inputStream = new SmbFileReadInteractor().openFile(location);
        while (inputStream.read() > 1) {
            inputStream.close();
        }
    }

}
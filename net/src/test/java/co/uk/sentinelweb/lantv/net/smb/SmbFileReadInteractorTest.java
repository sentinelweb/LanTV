package co.uk.sentinelweb.lantv.net.smb;

import org.junit.Test;

import java.io.InputStream;

/**
 * Created by robert on 13/02/2017.
 */
public class SmbFileReadInteractorTest {

    public static final String IRON_MAN = "smb://192.168.0.13/Drobo/video/movies/superhero/Iron.Man.avi";
    public static final String DARK_KNIGHT = "smb://192.168.0.13/Drobo/video/movies/superhero/dark_knight.iso.mp4";

    @Test
    public void openFile() throws Exception {
        final InputStream inputStream = new SmbFileReadInteractor().openFile(
                DARK_KNIGHT,
                TestData.USER,
                TestData.PASS);
        while (inputStream.read() > 1) {
            inputStream.close();
        }
    }



}
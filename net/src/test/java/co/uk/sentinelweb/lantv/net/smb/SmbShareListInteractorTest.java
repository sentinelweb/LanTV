package co.uk.sentinelweb.lantv.net.smb;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;

/**
 * Created by robert on 12/02/2017.
 */
public class SmbShareListInteractorTest {
    static {
        jcifs.Config.registerSmbURLHandler();
    }
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getList() throws Exception {
        final List<Media> list = new SmbShareListInteractor().getList(TestData.IP_ADDR,
                TestData.SHARE,
                TestData.PATH,
                TestData.USER,
                TestData.PASS
        );
        if (list != null) {
            for (final Media m : list) {
                System.out.println(m.toString());
            }
        }
    }

    @Test
    public void getShareList() throws Exception {
        final List<Media> list = new SmbShareListInteractor().getList(TestData.IP_ADDR,
                null,
                null,
                TestData.USER,
                TestData.PASS
        );
        if (list != null) {
            for (final Media m : list) {
                System.out.println(m.toString());
            }
        }
    }

    @Test
    public void getNetworkList() throws Exception {
        final List<Media> list = new SmbShareListInteractor().getList(null,
                null,
                null,
                null,
                null
        );
        if (list != null) {
            for (final Media m : list) {
                System.out.println(m.toString());
            }
        }
    }

    @Test
    public void getWorkgroupList() throws Exception {
        final List<Media> list = new SmbShareListInteractor().getList("WORKGROUP",
                null,
                null,
                null,
                null
        );
        if (list != null) {
            for (final Media m : list) {
                System.out.println(m.toString());
            }
        }
    }

    @Test
    public void getComputerList() throws Exception {
        final List<Media> list = new SmbShareListInteractor().getList(new SmbLocationParser().parse("smb://robert:satori@TIGER/"));
        if (list != null) {
            for (final Media m : list) {
                System.out.println(m.toString());
            }
        }
    }

//    @Test
//    public void getFileData() throws Exception {
//        final SmbLocation location = new SmbLocation("192.168.0.13", "Drobo", "video/movies/superhero/", "dark_knight.iso.mp4",TestData.USER, TestData.PASS);//
//        final Media m = new SmbShareListInteractor().getList(location );
//        System.out.println(m.toString());
//    }

}
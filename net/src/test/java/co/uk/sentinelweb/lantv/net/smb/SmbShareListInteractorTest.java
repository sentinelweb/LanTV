package co.uk.sentinelweb.lantv.net.smb;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import co.uk.sentinelweb.lantv.domain.Media;

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

}
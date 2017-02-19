package co.uk.sentinelweb.lantv.net.smb.url;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 18/02/2017.
 */
public class SmbUrlBuilderTest {

    public static final String IP_ADDR = "1.1.1.1";
    public static final String SHARE = "share";
    public static final String PATH_SINGLE = "path";
    public static final String PATH_MULTI = "path/path2";
    public static final String PATH_MULTI_SPACE = "path/path2 and";
    public static final String FILE = "file.ext";
    public static final String USER = "user";
    public static final String PASS = "pass";

    static {
        jcifs.Config.registerSmbURLHandler();
    }

    @Test
    public void buildSimple() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_SINGLE);

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" + PATH_SINGLE + "/", SmbUrlBuilder.build(l));
    }

    @Test
    public void buildComputer() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, null, null);

        assertEquals("smb://" + IP_ADDR + "/" , SmbUrlBuilder.build(l));
    }

    @Test
    public void buildShare() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, null);

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" , SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathMulti() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI);

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI + "/", SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathMultiSlash() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI + "/");

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI + "/", SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathMultiSpace() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI_SPACE);

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI_SPACE + "/", SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathMultiFile() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI + "/", FILE);

        assertEquals("smb://" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI + "/" + FILE, SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathUserMultiFile() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI + "/", FILE, USER, null);

        assertEquals("smb://" + USER + "@" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI + "/" + FILE, SmbUrlBuilder.build(l));
    }

    @Test
    public void buildPathUserPassMultiFile() throws Exception {
        final SmbLocation l = new SmbLocation(IP_ADDR, SHARE, PATH_MULTI + "/", FILE, USER, PASS);

        assertEquals("smb://" + USER + ":" + PASS + "@" + IP_ADDR + "/" + SHARE + "/" + PATH_MULTI + "/" + FILE, SmbUrlBuilder.build(l));
    }


}
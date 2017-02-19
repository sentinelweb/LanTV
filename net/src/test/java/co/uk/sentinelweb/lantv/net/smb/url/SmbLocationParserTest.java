package co.uk.sentinelweb.lantv.net.smb.url;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by robert on 18/02/2017.
 */
public class SmbLocationParserTest {
    static {
        jcifs.Config.registerSmbURLHandler();
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void parseUserPassHostShareMultiPathFile() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://user:pass@host.com/shareName/path/path/file.ext");
        assertEquals("host.com", loc.getIpAddr());
        assertEquals("shareName", loc.getShareName());
        assertEquals("path/path", loc.getDirname());
        assertEquals("file.ext", loc.getFileName());
        assertEquals("user", loc.getUsername());
        assertEquals("pass", loc.getPassword());
    }

    @Test
    public void parseUserPassHostSharePathFile() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://user:pass@host.com/shareName/path/file.ext");
        assertEquals("host.com", loc.getIpAddr());
        assertEquals("shareName", loc.getShareName());
        assertEquals("path", loc.getDirname());
        assertEquals("file.ext", loc.getFileName());
        assertEquals("user", loc.getUsername());
        assertEquals("pass", loc.getPassword());
    }

    @Test
    public void parseUserPassHostSharePath() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://user:pass@host.com/shareName/path/");
        assertEquals("host.com", loc.getIpAddr());
        assertEquals("shareName", loc.getShareName());
        assertEquals("path", loc.getDirname());
        assertNull(loc.getFileName());
        assertEquals("user", loc.getUsername());
        assertEquals("pass", loc.getPassword());
    }

    @Test
    public void parseHostShare() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://host.com/shareName/");
        assertEquals("host.com", loc.getIpAddr());
        assertEquals("shareName", loc.getShareName());
        assertNull(loc.getDirname());
        assertNull(loc.getFileName());
        assertNull(loc.getUsername());
        assertNull(loc.getPassword());
    }

    @Test
    public void parseUserPassHost() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://user:pass@host.com");
        assertEquals("host.com", loc.getIpAddr());
        assertNull(loc.getShareName());
        assertNull(loc.getDirname());
        assertNull(loc.getFileName());
        assertEquals("user", loc.getUsername());
        assertEquals("pass", loc.getPassword());
    }

    @Test
    public void parseUserHost() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://user@host.com");
        assertEquals("host.com", loc.getIpAddr());
        assertNull(loc.getShareName());
        assertNull(loc.getDirname());
        assertNull(loc.getFileName());
        assertEquals("user", loc.getUsername());
        assertNull(loc.getPassword());
    }

    @Test
    public void parseHost() throws Exception {
        final SmbLocation loc = new SmbLocationParser().parse("smb://host.com");
        assertEquals("host.com", loc.getIpAddr());
        assertNull(loc.getShareName());
        assertNull(loc.getDirname());
        assertNull(loc.getFileName());
        assertNull(loc.getUsername());
        assertNull(loc.getPassword());
    }

}
package co.uk.sentinelweb.lantv.net.smb.url;

import java.net.MalformedURLException;
import java.net.URL;

import static co.uk.sentinelweb.lantv.util.StringUtils.concat;

/**
 * Created by robert on 18/02/2017.
 */
public class SmbLocationParser {
    public SmbLocation parse(final String urlString) {
        try {
            final URL url = new URL(urlString);
            final String authority = url.getAuthority();
            String username = null;
            String passwd = null;

            if (authority.indexOf("@")>-1) {
                final String[] authSplit = authority.split("@");
                if (authority.indexOf(":")>-1) {
                    final String[] userPass = authSplit[0].split(":");
                    username = userPass[0];
                    passwd = userPass[1];
                } else {
                    username = authSplit[0];
                }
            }
            String share = null;
            String path = null;
            String file = null;
            if (url.getPath()!=null && url.getPath().indexOf("/")>-1) {
                final String[] splitPath = url.getPath().split("/");
                final boolean isDir = url.getPath().endsWith("/");
                if (splitPath.length>0) {
                    share = splitPath[1];
                }
                if (splitPath.length>1) {
                    if (isDir) {
                        path = concat(splitPath, "/", 2, 0);
                    } else {
                        path = concat(splitPath, "/", 2, -1);
                        file = splitPath[splitPath.length - 1];
                    }
                    if (path!=null && path.isEmpty()) {
                        path =null;
                    }
                    if (file!=null && file.isEmpty()) {
                        file =null;
                    }
                }
            } else {
                share = url.getPath();
            }
            if (share!=null && share.isEmpty()) {
                share =null;
            }

            final SmbLocation location = new SmbLocation(
                url.getHost(),
                    share,
                    path,
                    file,
                    username,
                    passwd
            );
            return location;
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

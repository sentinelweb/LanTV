package co.uk.sentinelweb.lantv.net.smb.url;

import static co.uk.sentinelweb.lantv.util.StringUtils.isBlank;

public class SmbUrlBuilder {
    public String build(final SmbLocation location) {

//        try {
//            final URL url = new URL("smb", buildAuthority(location), buildPath(location));
//            return url.toString();
//        } catch (final MalformedURLException e) {
//            e.printStackTrace();
//        }
        final String authority = buildAuthority(location);
        final String path = buildPath(location);
        return "smb://" + authority + ("".equals(authority) ? "" : path);
    }

    private static String buildAuthority(final SmbLocation l) {
        String credentials = "";
        if (l.getUsername() != null) {
            credentials = l.getUsername();
        }
        if (l.getPassword() != null) {
            credentials += ":" + l.getPassword();
        }
        final String ipAddr = l.getIpAddr() != null ? l.getIpAddr() : "";
        return (!credentials.isEmpty() ? (credentials + "@") : "") + ipAddr;
    }

    private static String buildPath(final SmbLocation l) {
        if (isBlank(l.getShareName())) {
            // path will be ignored
            return "/";
        } else if (isBlank(l.getDirname())) {
            return "/" + appendSlash(l.getShareName());
        } else {
            String path = (!l.getShareName().startsWith("/") ? "/" : "") + l.getShareName() + "/";
            if (l.getDirname() != null && !"".equals(l.getDirname())) {
                path += appendSlash(l.getDirname());
            }
            if (l.getFileName() != null && !"".equals(l.getFileName())) {
                path += l.getFileName();
            }
            return path;
        }

    }

    private static String appendSlash(final String dirname) {
        return dirname + (!dirname.endsWith("/") ? "/" : "");
    }
}


package co.uk.sentinelweb.lantv.net.smb.url;

import static co.uk.sentinelweb.lantv.util.StringUtils.isBlank;

public class SmbUrlBuilder {
    public static String build(final SmbLocation location) {

//        try {
//            final URL url = new URL("smb", buildAuthority(location), buildPath(location));
//            return url.toString();
//        } catch (final MalformedURLException e) {
//            e.printStackTrace();
//        }
        return "smb://" + buildAuthority(location) + buildPath(location);
    }

    private static String buildAuthority(final SmbLocation l) {
        String credentials = "";
        if (l.getUsername() != null) {
            credentials = l.getUsername();
        }
        if (l.getPassword() != null) {
            credentials += ":" + l.getPassword();
        }
        return (!credentials.isEmpty() ? (credentials + "@") : "") + l.getIpAddr();
    }

    private static String buildPath(final SmbLocation l) {
        if (isBlank(l.getShareName())) {
            // path will be ignored
            return "/";
        } else if (isBlank(l.getDirname())) {
            return "/"+appendSlash(l.getShareName());
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


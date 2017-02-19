package co.uk.sentinelweb.lantv.net.smb.url;


import java.io.Serializable;

//TODO @AutoValue
public class SmbLocation implements Serializable {


    private static final long serialVersionUID = 9189982489877100001L;

    private String ipAddr;
    private String shareName;
    private String dirname;
    private String fileName;
    private String username;
    private String password;

    public SmbLocation(final String ipAddr, final String shareName, final String dirPath) {
        this(ipAddr, shareName, dirPath, null, null, null);
    }

    public SmbLocation(final String ipAddr, final String shareName, final String dirPath, final String fileName) {
        this(ipAddr, shareName, dirPath, fileName, null, null);
    }

    public SmbLocation(final String ipAddr, final String shareName, final String dirname, final String fileName, final String username, final String password) {
        this.dirname = dirname;
        this.ipAddr = ipAddr;
        this.shareName = shareName;
        this.fileName = fileName;
        this.username = username;
        this.password = password;
    }

    public String getDirname() {
        return dirname;
    }

    public void setDirname(final String dirname) {
        this.dirname = dirname;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(final String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(final String shareName) {
        this.shareName = shareName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public SmbLocation clone () {
        final SmbLocation clone = new SmbLocation(this.ipAddr,this.shareName,this.dirname ,this.fileName,this.username,this.password);
        return clone;
    }

}

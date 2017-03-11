package co.uk.sentinelweb.lantv.net.smb.url;

import java.util.Date;

import co.uk.sentinelweb.lantv.domain.Media;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by robert on 11/03/2017.
 */

public class SmbMediaCreator {
    public Media createMedia(final SmbFile child) throws SmbException {
        final boolean isWorkGroup = child.getType()==SmbFile.TYPE_WORKGROUP;
        final boolean isServer = child.getType()==SmbFile.TYPE_SERVER;
        final boolean isShare = child.getType()==SmbFile.TYPE_SHARE;
        final boolean isPrinter = child.getType()==SmbFile.TYPE_PRINTER;
        final boolean isFileOrDirectory = child.getType()==SmbFile.TYPE_FILESYSTEM;

        return Media.create(
                child.getURL().toExternalForm(),
                child.getName(),
                child.length(),
                new Date(child.lastModified()),
                isWorkGroup,
                isServer,
                isShare,
                isPrinter,
                isFileOrDirectory && child.isDirectory(),
                isFileOrDirectory && child.isFile()
        );
    }
}

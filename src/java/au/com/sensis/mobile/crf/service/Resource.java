package au.com.sensis.mobile.crf.service;

import java.io.File;


/**
 * Represents a resolved resource that is known to exist.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Resource {

    /**
     * @return true if {@link #getNewPath()} ends with the special
     *         extension ".null".
     */
    boolean newPathEndsWithDotNull();

    /**
     * @return the originalPath
     */
    String getOriginalPath();

    /**
     * @return the newPath
     */
    String getNewPath();

    /**
     * @return {@link File} combining {@link #getRootResourceDir()} and
     *         {@link #getNewPath().
     */
    File getNewFile();

    /**
     * @return the rootResourceDir
     */
    File getRootResourceDir();

    /**
     * @return Length of {@link #getNewFile()} as an int.
     */
    int getNewFileLengthAsInt();

}

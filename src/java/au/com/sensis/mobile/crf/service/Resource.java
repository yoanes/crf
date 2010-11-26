package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.Serializable;

import au.com.sensis.mobile.crf.config.Group;


/**
 * Represents a resolved resource that is known to exist.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Resource extends Serializable {

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
     * @return the rootResourceDir which {@link #getNewPath()} is relative to.
     */
    File getRootResourceDir();

    /**
     * @return Length of {@link #getNewFile()} as an int.
     */
    int getNewFileLengthAsInt();

    /**
     * @return {@link Group} that this {@link Resource} was found in.
     */
    Group getGroup();

}

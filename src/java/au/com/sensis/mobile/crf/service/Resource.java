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
     * @see #newPathEndsWithDotNull().
     */
    String DOT_NULL_EXTENSION = ".null";

    /**
     * @return true if {@link #getNewPath()} ends with the special
     *         extension {@link #DOT_NULL_EXTENSION}.
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
    // TODO: it seems that nothing uses this anymore. We can probably remove it.
    Group getGroup();

}

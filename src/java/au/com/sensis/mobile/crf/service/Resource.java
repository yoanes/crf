package au.com.sensis.mobile.crf.service;

import java.io.File;


/**
 * Represents a resolved resource that is known to exist.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Resource {

    /**
     * Reserved name for directories storing bundle result artifacts.
     */
    String BUNDLE_DIR_NAME = "bundle";

    /**
     * @return true if {@link #getNewPath() corresponds to a bundle
     *         result artifact. ie. if the last directory equals
     *         {@link #BUNDLE_DIR_NAME}.
     */
    boolean isBundlePath();

    /**
     * If {@link #isBundlePath()} is true, returns the part of the path before
     * the {@link #BUNDLE_DIR_NAME} directory. Else, throw
     * {@link IllegalStateException}.
     *
     * @return the part of the path before the {@link #BUNDLE_DIR_NAME}
     *         directory.
     * @throws IllegalStateException
     *             Thrown if {@link #isBundlePath()} is false.
     */
    File getBundleParentDirFile() throws IllegalStateException;

    /**
     * @return true if {@link #getNewPath()} ends with the special
     *         extension ".null".
     */
    boolean endsWithDotNull();

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

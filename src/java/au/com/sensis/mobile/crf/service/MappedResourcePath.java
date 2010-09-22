package au.com.sensis.mobile.crf.service;

import java.io.File;


/**
 * Simple pair resulting from a mapping of an original resource path to a new path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// TODO: will be split into two: 1. GroupResourceResolver will handle the current "exist*" methods
// but renamed to "resolve*" 2. Resource will represent a physical resource that is known to exist.
public interface MappedResourcePath {

    /**
     * Reserved name for directories storing bundle result artifacts.
     */
    String BUNDLE_DIR_NAME = "bundle";

    /**
     * @return true if {@link #getNewResourcePath() corresponds to a bundle
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
     * @return true if {@link #getNewResourcePath()} ends with the special
     *         extension ".null".
     */
    boolean endsWithDotNull();

    /**
     * @return the originalResourcePath
     */
    String getOriginalResourcePath();

    /**
     * @return the newResourcePath
     */
    String getNewResourcePath();

    /**
     * @return {@link File} combining {@link #getRootResourceDir()} and
     *         {@link #getNewResourcePath().
     */
    File getNewResourceFile();

    /**
     * @return the rootResourceDir
     */
    File getRootResourceDir();

    /**
     * @return Length of {@link #getNewResourceFile()} as an int.
     */
    int getFileLengthAsInt();

}

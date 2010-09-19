package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.List;


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
     * @return true if {@link #getNewResourcePath()} equals
     *         {@link #getOriginalResourcePath()}.
     */
    boolean isIdentityMapping();

    /**
     * @return true if the mapped path given by {@link #getNewResourcePath()}
     *         exists in {@link #getRootResourceDir()}.
     */
    boolean exists();

    /**
     * Resolves the requested path to a single {@link MappedResourcePath}.
     */
    // TODO: this is the start of decomposing MappedResourcePath into a GroupResourceResolver
    // and a Resource.
    MappedResourcePath resolveToSingle();

    /**
     * Returns a list of {@link MappedResourcePath}s that exist in
     * {@link #getRootResourceDir()} and match the current
     * {@link #getNewResourcePath()} but with any of the given extensions. As
     * such, the last component of {@link #getNewResourcePath()} is assumed to
     * be the stem of a file, not a directory. Note that if
     * {@link #getNewResourcePath()} already has an extension, this is
     * <b>not</b> stripped first.
     *
     * <b>
     * Note that each extension is prefixed with "." before testing occurs.
     * Therefore, in the above example, default/common/unmetered will never
     * be matched itself even if it exists.
     * </b>
     *
     * @param extensions
     *            Array of extensions allowed. A "*" wildcard pattern is
     *            supported and is analogous to the typical Dos/Windows command
     *            line wildcard.
     * @return list of {@link MappedResourcePath}s that exist in
     *         {@link #getRootResourceDir()} and match
     *         {@link #getNewResourcePath()} but with any of the given
     *         extensions. May not be null. Empty indicates no matches exist.
     */
    List<MappedResourcePath> existWithExtensions(
            final String[] extensions);

    /**
     * Returns a list of {@link MappedResourcePath}s that exist in
     * {@link #getRootResourceDir()} based on expanding this
     * {@link MappedResourcePath}'s path.
     *
     * @return list of {@link MappedResourcePath}s that exist in
     *         {@link #getRootResourceDir()}based on expanding this
     *         {@link MappedResourcePath}'s path. May not be null. Empty
     *         indicates no matches exist.
     * @throws IOException
     *             Thrown if any IO error occurs during the expansion.
     */
    List<MappedResourcePath> existByExpansion() throws IOException;

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
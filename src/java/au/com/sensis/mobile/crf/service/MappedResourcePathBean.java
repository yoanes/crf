package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Simple pair resulting from a mapping of an original resource path to a new path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MappedResourcePathBean implements MappedResourcePath {

    /**
     * Separator character expected to be used in {@link #getOriginalResourcePath()}
     * and {@link #getNewResourceFile()}.
     */
    protected static final String SEPARATOR = "/";

    private final String originalResourcePath;
    private final String newResourcePath;
    private final File rootResourceDir;

    /**
     * Default constructor.
     *
     * @param originalResourcePath
     *            Original path that was requested.
     * @param newResourcePath
     *            New path that originalResourcePath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newResourcePath is relative to.
     */
    public MappedResourcePathBean(final String originalResourcePath,
            final String newResourcePath, final File rootResourceDir) {
        if (StringUtils.isBlank(originalResourcePath)) {
            throw new IllegalArgumentException(
                    "originalResourcePath must not be blank: '"
                            + originalResourcePath + "'");
        }
        if (StringUtils.isBlank(newResourcePath)) {
            throw new IllegalArgumentException(
                    "newResourcePath must not be blank: '"
                    + newResourcePath + "'");
        }

        this.originalResourcePath = originalResourcePath;
        this.newResourcePath = newResourcePath;
        this.rootResourceDir = rootResourceDir;
    }

    /**
     * @return true if {@link #getNewResourcePath()} equals
     *         {@link #getOriginalResourcePath()}.
     */
    public boolean isIdentityMapping() {
        return getNewResourcePath().equals(getOriginalResourcePath());
    }

    /**
     * @return true if the mapped path given by {@link #getNewResourcePath()}
     *         exists in {@link #getRootResourceDir()}.
     */
    public boolean exists() {
        // TODO: possibly cache the result since we are accessing the file system?
        return FileIoFacadeFactory.getFileIoFacadeSingleton().fileExists(
                getRootResourceDir(), getNewResourcePath());
    }

    @Override
    public MappedResourcePath resolveToSingle() {
        if (exists()) {
            return this;
        } else {
            return null;
        }
    }

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
    public List<MappedResourcePath> existWithExtensions(
            final String[] extensions) {
        // TODO: possibly cache the result since we are accessing the file
        // system?
        final List<MappedResourcePath> foundMappedResourcePaths =
                new ArrayList<MappedResourcePath>();

        final File[] matchedFiles =
                FileIoFacadeFactory.getFileIoFacadeSingleton().list(
                        getRootResourceDir(), getNewResourcePath(), extensions);
        for (final File matchedFile : matchedFiles) {
            final MappedResourcePath mappedResourcePath =
                    new MappedResourcePathBean(getOriginalResourcePath(),
                            getNewResourcePathPlusFileExtension(matchedFile),
                            getRootResourceDir());
            foundMappedResourcePaths.add(mappedResourcePath);
        }

        return foundMappedResourcePaths;
    }

    /**
     * Returns a list of {@link MappedResourcePath}s that exist in
     * {@link #getRootResourceDir()} based on this {@link MappedResourcePath}s
     * path expasion algorithm.
     *
     * @return a list of {@link MappedResourcePath}s that exist in
     *         {@link #getRootResourceDir()} based on this
     *         {@link MappedResourcePath}s path expasion algorithm. May not be
     *         null. Empty indicates no matches exist.
     * @throws IOException
     *             Thrown if any IO error occurs during the expansion.
     */
    public List<MappedResourcePath> existByExpansion() throws IOException {
        if (exists()) {
            return Arrays.asList((MappedResourcePath) this);
        } else {
            return new ArrayList<MappedResourcePath>();
        }
    }

    /**
     * @return true if {@link #getNewResourcePath() corresponds to a bundle
     *         result artifact. ie. if the last directory equals
     *         {@link #BUNDLE_DIR_NAME}.
     */
    public boolean isBundlePath() {
        return (getNewResourcePath() != null)
                && FilenameUtils.getPathNoEndSeparator(getNewResourcePath())
                        .endsWith(BUNDLE_DIR_NAME);
    }

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
    public File getBundleParentDirFile() throws IllegalStateException {
        if (isBundlePath()) {
            // TODO: Pretty complictade way of building the File. Is there an easier
            // way.
            return new File(getRootResourceDir(),
                    FilenameUtils.getPathNoEndSeparator(getNewResourcePath())).getParentFile();
        } else {
            throw new IllegalStateException(
                    "Illegal to call this method when isBundlePath() is false.");
        }
    }

    private String getNewResourcePathPlusFileExtension(final File matchedFile) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getNewResourcePath());
        stringBuilder.append(".");
        stringBuilder.append(FilenameUtils.getExtension(matchedFile.getName()));
        return stringBuilder.toString();
    }

    /**
     * @return true if {@link #getNewResourcePath()} ends with the special
     *         extension ".null".
     */
    public boolean endsWithDotNull() {
        return getNewResourcePath().endsWith(".null");
    }

    /**
     * @return the originalResourcePath
     */
    public String getOriginalResourcePath() {
        return originalResourcePath;
    }

    /**
     * @return the newResourcePath
     */
    public String getNewResourcePath() {
        return newResourcePath;
    }

    /**
     * @return {@link File} combining {@link #getRootResourceDir()} and
     *         {@link #getNewResourcePath().
     */
    public File getNewResourceFile() {
        return new File(getRootResourceDir(), getNewResourcePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final MappedResourcePathBean rhs = (MappedResourcePathBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getOriginalResourcePath(),
                rhs.getOriginalResourcePath());
        equalsBuilder.append(getNewResourcePath(),
                rhs.getNewResourcePath());
        equalsBuilder.append(getRootResourceDir(),
                rhs.getRootResourceDir());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getOriginalResourcePath());
        hashCodeBuilder.append(getNewResourcePath());
        hashCodeBuilder.append(getRootResourceDir());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("originalResourcePath", getOriginalResourcePath());
        toStringBuilder.append("newResourcePath", getNewResourcePath());
        toStringBuilder.append("rootResourceDir", getRootResourceDir());
        return toStringBuilder.toString();
    }

    /**
     * @return the rootResourceDir
     */
    public File getRootResourceDir() {
        return rootResourceDir;
    }

    /**
     * @return Length of {@link #getNewResourceFile()} as an int.
     */
    public int getFileLengthAsInt() {
        return (int) getNewResourceFile().length();
    }

}

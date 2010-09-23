package au.com.sensis.mobile.crf.service;

import java.io.File;

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
public class ResourceBean implements Resource {

    /**
     * Separator character expected to be used in {@link #getOriginalPath()}
     * and {@link #getNewFile()}.
     */
    protected static final String SEPARATOR = "/";

    private final String originalPath;
    private final String newPath;
    private final File rootResourceDir;

    /**
     * Default constructor.
     *
     * @param originalPath
     *            Original path that was requested.
     * @param newPath
     *            New path that originalPath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newPath is relative to.
     */
    public ResourceBean(final String originalPath,
            final String newPath, final File rootResourceDir) {
        if (StringUtils.isBlank(originalPath)) {
            throw new IllegalArgumentException(
                    "originalPath must not be blank: '"
                            + originalPath + "'");
        }
        if (StringUtils.isBlank(newPath)) {
            throw new IllegalArgumentException(
                    "newPath must not be blank: '"
                    + newPath + "'");
        }

        this.originalPath = originalPath;
        this.newPath = newPath;
        this.rootResourceDir = rootResourceDir;
    }

    /**
     * @return true if {@link #getNewPath()} equals
     *         {@link #getOriginalPath()}.
     */
    public boolean isIdentityMapping() {
        return getNewPath().equals(getOriginalPath());
    }

    /**
     * @return true if {@link #getNewPath() corresponds to a bundle
     *         result artifact. ie. if the last directory equals
     *         {@link #BUNDLE_DIR_NAME}.
     */
    public boolean isBundlePath() {
        return (getNewPath() != null)
                && FilenameUtils.getPathNoEndSeparator(getNewPath())
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
                    FilenameUtils.getPathNoEndSeparator(getNewPath())).getParentFile();
        } else {
            throw new IllegalStateException(
                    "Illegal to call this method when isBundlePath() is false.");
        }
    }

    /**
     * @return true if {@link #getNewPath()} ends with the special
     *         extension ".null".
     */
    public boolean endsWithDotNull() {
        return getNewPath().endsWith(".null");
    }

    /**
     * @return the originalPath
     */
    public String getOriginalPath() {
        return originalPath;
    }

    /**
     * @return the newPath
     */
    public String getNewPath() {
        return newPath;
    }

    /**
     * @return {@link File} combining {@link #getRootResourceDir()} and
     *         {@link #getNewPath().
     */
    public File getNewFile() {
        return new File(getRootResourceDir(), getNewPath());
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

        final ResourceBean rhs = (ResourceBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getOriginalPath(),
                rhs.getOriginalPath());
        equalsBuilder.append(getNewPath(),
                rhs.getNewPath());
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
        hashCodeBuilder.append(getOriginalPath());
        hashCodeBuilder.append(getNewPath());
        hashCodeBuilder.append(getRootResourceDir());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("originalPath", getOriginalPath());
        toStringBuilder.append("newPath", getNewPath());
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
     * @return Length of {@link #getNewFile()} as an int.
     */
    public int getNewFileLengthAsInt() {
        return (int) getNewFile().length();
    }

}

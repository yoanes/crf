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

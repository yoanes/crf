package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.config.Group;


/**
 * Default {@link Resource} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceBean implements Resource {

    private static final long serialVersionUID = 1L;

    /**
     * Separator character expected to be used in {@link #getOriginalPath()}
     * and {@link #getNewFile()}.
     */
    protected static final String SEPARATOR = "/";

    private final String originalPath;
    private final String newPath;
    private final File rootResourceDir;
    private final Group group;

    /**
     * Default constructor.
     *
     * @param originalPath
     *            Original path that was requested.
     * @param newPath
     *            New path that originalPath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newPath is relative to.
     * @param group {@link Group} that this {@link Resource} was found in.
     */
    public ResourceBean(final String originalPath,
            final String newPath, final File rootResourceDir,
            final Group group) {
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
        this.group = group;
    }

    /**
     * @return true if {@link #getNewPath()} ends with the special
     *         extension ".null".
     */
    @Override
    public boolean newPathEndsWithDotNull() {
        return getNewPath().endsWith(".null");
    }

    /**
     * @return the originalPath
     */
    @Override
    public String getOriginalPath() {
        return originalPath;
    }

    /**
     * @return the newPath
     */
    @Override
    public String getNewPath() {
        return newPath;
    }

    /**
     * @return {@link File} combining {@link #getRootResourceDir()} and
     *         {@link #getNewPath().
     */
    @Override
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
        equalsBuilder.append(getGroup(),
                rhs.getGroup());
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
        hashCodeBuilder.append(getGroup());
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
        toStringBuilder.append("group", getGroup());
        return toStringBuilder.toString();
    }

    /**
     * @return the rootResourceDir
     */
    @Override
    public File getRootResourceDir() {
        return rootResourceDir;
    }

    /**
     * @return Length of {@link #getNewFile()} as an int.
     */
    @Override
    public int getNewFileLengthAsInt() {
        return (int) getNewFile().length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Group getGroup() {
        return group;
    }

}

package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Represents the import of a group or multiple groups from another {@link UiConfiguration} that
 * has a given config path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupImport {

    /**
     * Default config path  to use if {@link #getFromConfigPath()} is null.
     */
    public static final String DEFAULT_CONFIG_PATH = "global/devices";

    /**
     * Optional name of the group to import. If null, all groups from the {@link UiConfiguration}
     * with a config path of {@link #getFromConfigPath()} will be imported. At least one of this
     * field and {@link #getFromConfigPath()} must be non-blank.
     */
    private String groupName;

    /**
     * Optional name of the {@link UiConfiguration#getConfigPath()} to import a group(s) from.
     * At least one of this field and {@link #getGroupName()} must be non-blank. If
     * {@link #getGroupName()} is non-blank and this {@link #fromConfigPath} is blank, then
     * {@link #fromConfigPath} defaults to {@link #DEFAULT_CONFIG_PATH}.
     */
    private String fromConfigPath = DEFAULT_CONFIG_PATH;

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the fromConfigPath
     */
    public String getFromConfigPath() {
        return fromConfigPath;
    }

    /**
     * @param fromConfigPath the fromConfigPath to set.
     */
    public void setFromConfigPath(final String fromConfigPath) {
        this.fromConfigPath = fromConfigPath;
    }

    /**
     * @return true if {@link #getFromConfigPath()} is the default of
     *         {@link #DEFAULT_CONFIG_PATH}.
     */
    public boolean hasDefaultConfigPath() {
        return DEFAULT_CONFIG_PATH.equals(getFromConfigPath());
    }

    /**
     * @return true if {@link #getFromConfigPath()} is a global config path. ie. if
     *         it has a prefix of {@link UiConfiguration#GLOBAL_CONFIG_PATH_PREFIX}.
     */
    public boolean hasGlobalConfigPath() {
        return getFromConfigPath().startsWith(UiConfiguration.GLOBAL_CONFIG_PATH_PREFIX);
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

        final GroupImport rhs = (GroupImport) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroupName(), rhs.getGroupName());
        equalsBuilder.append(getFromConfigPath(), rhs.getFromConfigPath());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getGroupName());
        hashCodeBuilder.append(getFromConfigPath());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("groupName", getGroupName())
            .append("fromConfigPath", getFromConfigPath())
            .toString();
    }

}

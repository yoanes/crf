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
     * Optional name that the (single) imported group will be given after it is imported. If only
     * one of {@link #getGroupName()} and {@link #getFromGroupName()} is given, the other will
     * automatically use the same value. If both {@link #getGroupName()} and
     * {@link #getFromGroupName()} are null, all groups from the {@link UiConfiguration} with a
     * config path of {@link #getFromConfigPath()} will be imported.
     */
    private String groupName;

    /**
     * Optional config path (ie. namespace) of the file from which to import a group(s). If neither
     * {@link #getGroupName()} or {@link #getFromGroupName()} are specified, all groups will be
     * imported. If config path is not specified, it defaults to {@link #DEFAULT_CONFIG_PATH} but
     * note that at least one of {@link #getGroupName()}, {@link #getFromGroupName()} or
     * {@link #getFromConfigPath()} must be explicitly set.
     */
    private String fromConfigPath = DEFAULT_CONFIG_PATH;

    /**
     * Optional name of the (single) group to import. If only one of {@link #getGroupName()} and
     * {@link #getFromGroupName()} is given, the other will automatically use the same value. If
     * both {@link #getGroupName()} and {@link #getFromGroupName()} are null, all groups from the
     * {@link UiConfiguration} with a config path of {@link #getFromConfigPath()} will be imported.
     */
    private String fromGroupName;

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
     * @return the fromGroupName
     */
    public String getFromGroupName() {
        return fromGroupName;
    }

    /**
     * @param fromGroupName the fromGroupName to set
     */
    public void setFromGroupName(final String fromGroupName) {
        this.fromGroupName = fromGroupName;
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

        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final GroupImport rhs = (GroupImport) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroupName(), rhs.getGroupName());
        equalsBuilder.append(getFromConfigPath(), rhs.getFromConfigPath());
        equalsBuilder.append(getFromGroupName(), rhs.getFromGroupName());
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
        hashCodeBuilder.append(getFromGroupName());
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
            .append("fromGroupName", getFromGroupName())
            .toString();
    }

    /**
     * @return the effective name of the group to import, depending on whether
     *         {@link #getFromGroupName()} or {@link #getGroupName()} are null.
     */
    public String getEffectiveFromGroupName() {

        if (getFromGroupName() != null) {
            return getFromGroupName();

        } else if (getGroupName() != null) {
            return getGroupName();

        } else {
            return null;
        }
    }

    /**
     * @return the effective name of the group after it is imported, depending on whether
     *         {@link #getFromGroupName()} or {@link #getGroupName()} are null.
     */
    public String getEffectiveGroupName() {

        if (getGroupName() != null) {
            return getGroupName();

        } else if (getFromGroupName() != null) {
            return getFromGroupName();

        } else {
            return null;
        }
    }

}

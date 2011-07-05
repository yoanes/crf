package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Intermediate representation of {@link Groups} prior to all imports being
 * resolved to real groups.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsAndImports {

    private GroupOrImport[] groupOrImport = new GroupOrImport [] {};

    private DefaultGroup defaultGroup;

    /**
     * @return the groupOrImport
     */
    public GroupOrImport[] getGroupOrImport() {
        return groupOrImport;
    }

    /**
     * @param groupOrImport the groupOrImport to set
     */
    public void setGroupOrImport(final GroupOrImport[] groupOrImport) {
        Validate.notNull(groupOrImport, "groupOrImport must not be null.");
        this.groupOrImport = groupOrImport;
    }

    /**
     * @return the defaultGroup
     */
    public DefaultGroup getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * @param defaultGroup the defaultGroup to set
     */
    public void setDefaultGroup(final DefaultGroup defaultGroup) {
        Validate.notNull(defaultGroup, "defaultGroup must not be null.");
        this.defaultGroup = defaultGroup;
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

        final GroupsAndImports rhs = (GroupsAndImports) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroupOrImport(), rhs.getGroupOrImport());
        equalsBuilder.append(getDefaultGroup(), rhs.getDefaultGroup());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getGroupOrImport());
        hashCodeBuilder.append(getDefaultGroup());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("groupOrImport", getGroupOrImport())
            .append("defaultGroup", getDefaultGroup())
            .toString();
    }

    public Group getGroupByName(final String groupName) {
        // TODO: maybe create a map for the lookup. Performance probably isn't an issue
        // since this only occurs at app start up time but for some reason, it still makes me
        // queasy.
        for (final GroupOrImport groupOrImport : getGroupOrImport()) {
            if (groupOrImport.isGroup() && groupName.equals(groupOrImport.getGroup().getName())) {
                return groupOrImport.getGroup();
            }
        }
        return null;
    }
}

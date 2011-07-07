package au.com.sensis.mobile.crf.config;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Get the group with the given name. Will ignore imports.
     *
     * @param groupName Name of the group to get.
     * @return the group with the given name. Null if not found.
     */
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

    /**
     * A friendly summary of all group names. Will ignore imports.
     *
     * @return a friendly summary of all group names. Will ignore imports.
     */
    public String groupNameSummary() {
        final StringBuilder summaryBuilder = new StringBuilder("[");

        boolean haveOutputAGroupName = false;
        for (int i = 0; i < getGroupOrImport().length; i++) {
            if (getGroupOrImport()[i].isGroup()) {
                if (haveOutputAGroupName) {
                    summaryBuilder.append(", ");
                }
                summaryBuilder.append(getGroupOrImport()[i].getGroup().getName());
                haveOutputAGroupName = true;
            }
        }
        summaryBuilder.append("]");

        return summaryBuilder.toString();
    }

    /**
     * @return all contained {@link Group}s. Will ignore imports.
     */
    public Group[] getGroups() {
        final List<Group> groups = new ArrayList<Group>();
        for (final GroupOrImport groupOrImport : getGroupOrImport()) {
            if (groupOrImport.isGroup()) {
                groups.add(groupOrImport.getGroup());
            }
        }
        return groups.toArray(new Group [] {});

    }

    /**
     * @return true if this {@link GroupsAndImports} contains any imports.
     */
    public boolean containsImports() {
        for (final GroupOrImport groupOrImport : getGroupOrImport()) {
            if (groupOrImport.isGroupImport()) {
                return true;
            }
        }
        return false;
    }
}

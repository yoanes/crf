package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Default {@link GroupOrImport}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupOrImportBean implements GroupOrImport {

    private Group group;
    private GroupImport groupImport;

    /**
     * Construct this to hold a {@link Group}.
     *
     * @param group {@link Group} for this bean to hold.
     */
    public GroupOrImportBean(final Group group) {
        Validate.notNull(group, "group must not be null.");
        this.group = group;
    }

    /**
     * Construct this to hold a {@link GroupImport}.
     *
     * @param groupImport {@link GroupImport} for this bean to hold.
     */
    public GroupOrImportBean(final GroupImport groupImport) {
        Validate.notNull(groupImport, "groupImport must not be null.");
        this.groupImport = groupImport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Group getGroup() {
        return group;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupImport getGroupImport() {
        return groupImport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroup() {
        return getGroup() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupImport() {
        return getGroupImport() != null;
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

        final GroupOrImportBean rhs = (GroupOrImportBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroup(), rhs.getGroup());
        equalsBuilder.append(getGroupImport(), rhs.getGroupImport());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getGroup());
        hashCodeBuilder.append(getGroupImport());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("group", getGroup())
            .append("groupImport", getGroupImport())
            .toString();
    }
}

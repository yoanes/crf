/**
 *
 */
package au.com.sensis.mobile.crf.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Encapsulates the group configuration.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class Groups {

    private Group[] groups = new Group[] {};

    private DefaultGroup defaultGroup;

    /**
     * @return {@link Iterator} for iterating through all groups, including the
     *         default group, which will always be the last group.
     */
    public Iterator<Group> groupIterator() {
        final List<Group> groups =
                new ArrayList<Group>(Arrays.asList(getGroups()));
        if (getDefaultGroup() != null) {
            groups.add(getDefaultGroup());
        }
        return groups.iterator();
    }

    /**
     * @param device
     *            {@link Device} to match against each group.
     * @return {@link Iterator} for iterating through all {@link Group}s that
     *         match the given {@link Device}.
     */
    public Iterator<Group> matchingGroupIterator(final Device device) {
        return matchingGroups(device).iterator();
    }

    /**
     * @param device
     *            {@link Device} to match against each group.
     * @return {@link List} containing {@link Group}s that match the given
     *         {@link Device}.
     */
    public List<Group> matchingGroups(final Device device) {
        final List<Group> matchingGroups = new ArrayList<Group>();
        for (final Group group : getGroups()) {
            if (group.match(device)) {
                matchingGroups.add(group);
            }
        }

        // Strictly speaking, we probably don't need to bother calling match for
        // the default group. However, there is no harm in doing so and it makes
        // the code a bit more robust to any future changes to the DefaultGroup
        // semantics.
        if (getDefaultGroup().match(device)) {
            matchingGroups.add(getDefaultGroup());
        }

        return matchingGroups;
    }

    /**
     * Only public due to XML->Java requirements (using Castor at the time of
     * writing). Clients are strongly encouraged to use the
     * {@link #groupIterator()} and {@link #matchingGroupIterator(Device)}
     * methods to manipulate the {@link Groups}.
     *
     * @return encapsulated sequence of {@link Group}s.
     */
    public Group[] getGroups() {
        return groups;
    }

    /**
     * @param groups encapsulated sequence of {@link Group}s.
     */
    public void setGroups(final Group[] groups) {
        Validate.notNull(groups, "groups must not be null");
        this.groups = groups;
    }

    /**
     * Only public due to XML->Java requirements (using Castor at the time of
     * writing). Clients are strongly encouraged to use the
     * {@link #groupIterator()} and {@link #matchingGroupIterator(Device)}
     * methods to manipulate the {@link Groups}.
     *
     * @return the defaultGroup
     */
    public DefaultGroup getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * @param defaultGroup
     *            the defaultGroup to set
     */
    public void setDefaultGroup(final DefaultGroup defaultGroup) {
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

        if ((obj == null) || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final Groups rhs = (Groups) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroups(), rhs.getGroups());
        equalsBuilder.append(getDefaultGroup(), rhs.getDefaultGroup());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getGroups());
        hashCodeBuilder.append(getDefaultGroup());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("groups", getGroups());
        toStringBuilder.append("defaultGroup", getDefaultGroup());
        return toStringBuilder.toString();
    }
}

package au.com.sensis.mobile.crf.config;

import java.util.Iterator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfiguration {

    private Groups groups;

    /**
     * Only public due to XML->Java requirements (using Castor at the time of
     * writing). Clients are strongly encouraged to use the
     * {@link #groupIterator()} and {@link #matchingGroupIterator(Device)}
     * methods to manipulate the {@link Groups}.
     *
     * @return The {@link Groups}.
     */
    public Groups getGroups() {
        return groups;
    }

    /**
     * @param groups The {@link Groups}.
     */
    public void setGroups(final Groups groups) {
        this.groups = groups;
    }

    /**
     * @return {@link Iterator} for iterating through all groups, including the
     *         default group, which will always be the last group.
     */
    public Iterator<Group> groupIterator() {
        return getGroups().groupIterator();
    }

    /**
    * @param device
    *            {@link Device} to match against each group.
    * @return {@link Iterator} for iterating through all {@link Group}s that
    *         match the given {@link Device}.
    */
    public Iterator<Group> matchingGroupIterator(final Device device) {
        return getGroups().matchingGroupIterator(device);
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

        final UiConfiguration rhs = (UiConfiguration) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getGroups(), rhs.getGroups());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getGroups());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("groups", getGroups());
        return toStringBuilder.toString();
    }
}

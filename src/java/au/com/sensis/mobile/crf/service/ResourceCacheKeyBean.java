package au.com.sensis.mobile.crf.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.config.Group;

/**
 * Default {@link Key} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceCacheKeyBean implements ResourceCacheKey {

    private static final long serialVersionUID = 1L;

    private final String requestedResourcePath;
    private final Group group;

    /**
     * Constructor.
     *
     * @param requestedResourcePath Path of requested resource.
     * @param group Group to look for the requested resource in.
     */
    public ResourceCacheKeyBean(final String requestedResourcePath, final Group group) {
        this.requestedResourcePath = requestedResourcePath;
        this.group = group;
    }

    /**
     * @return the requestedResourcePath
     */
    public String getRequestedResourcePath() {
        return requestedResourcePath;
    }

    /**
     * @return the group
     */
    public Group getGroup() {
        return group;
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

        final ResourceCacheKeyBean rhs = (ResourceCacheKeyBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(requestedResourcePath, rhs.requestedResourcePath);
        equalsBuilder.append(group, rhs.group);
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(requestedResourcePath);
        hashCodeBuilder.append(group);
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("requestedResourcePath", requestedResourcePath);
        toStringBuilder.append("group", group);
        return toStringBuilder.toString();
    }
}

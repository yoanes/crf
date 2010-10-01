package au.com.sensis.mobile.crf.config;

import java.net.URL;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfiguration {

    /**
     * URL of the source that this {@link UiConfiguration} was loaded from.
     */
    private URL sourceUrl;

    /**
     * Timestamp of the {@link #getSourceUrl()} that was loaded.
     */
    private long sourceTimestamp;

    /**
     * Path prefix that this configuration applies to. eg. component/map
     */
    private String configPath;

    private Groups groups;

    /**
     * @return URL of the source that this {@link UiConfiguration} was loaded from.
     */
    public URL getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @param sourceUrl
     *            URL of the source that this {@link UiConfiguration} was loaded
     *            from.
     */
    public void setSourceUrl(final URL sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * @return Timestamp of the {@link #getSourceUrl()} that this
     *         {@link UiConfiguration} was loaded from.
     */
    public long getSourceTimestamp() {
        return sourceTimestamp;
    }

    /**
     * @param sourceTimestamp
     *            Timestamp of the {@link #getSourceUrl()} that this
     *            {@link UiConfiguration} was loaded from.
     */
    public void setSourceTimestamp(final long sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }

    /**
     * @return Path prefix that this configuration applies to. eg. component/map
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * @param configPath
     *            Path prefix that this configuration applies to. eg.
     *            component/map
     */
    public void setConfigPath(final String configPath) {
        this.configPath = configPath;
    }

    /**
     * @param requestedResourcePath Path of the requested resource.
     * @return true if this {@link UiConfiguration} applies to the given requested resource path.
     */
    public boolean appliesToPath(final String requestedResourcePath) {
        return (requestedResourcePath != null) && requestedResourcePath.startsWith(getConfigPath());
    }

    /**
     * @return true if this {@link UiConfiguration} has a default config path
     *         (ie. does not apply to any specific config path).
     */
    public boolean hasDefaultConfigPath() {
        return (getConfigPath() == null) || StringUtils.isBlank(getConfigPath());
    }

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

        equalsBuilder.append(getSourceUrl(), rhs.getSourceUrl());
        equalsBuilder.append(getSourceTimestamp(), rhs.getSourceTimestamp());
        equalsBuilder.append(getConfigPath(), rhs.getConfigPath());
        equalsBuilder.append(getGroups(), rhs.getGroups());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getSourceUrl());
        hashCodeBuilder.append(getSourceTimestamp());
        hashCodeBuilder.append(getConfigPath());
        hashCodeBuilder.append(getGroups());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("sourceUrl", getSourceUrl());
        toStringBuilder.append("sourceTimestamp", getSourceTimestamp());
        toStringBuilder.append("configPath", getConfigPath());
        toStringBuilder.append("groups", getGroups());
        return toStringBuilder.toString();
    }
}

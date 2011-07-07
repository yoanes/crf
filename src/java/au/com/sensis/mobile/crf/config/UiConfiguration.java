package au.com.sensis.mobile.crf.config;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfiguration {

    private static final Logger LOGGER = Logger.getLogger(UiConfiguration.class);

    /**
     * Prefix that all global namespaces should have.
     */
    public static final String GLOBAL_CONFIG_PATH_PREFIX = "global/";

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

    private Groups groups = new Groups();

    private GroupsAndImports groupsAndImports;

    private GroupsCache matchingGroupsCache;

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
        return requestedResourcePath != null && requestedResourcePath.contains(getConfigPath());
    }

    /**
     * @return true if this {@link UiConfiguration} has a default config path
     *         (ie. does not apply to any specific config path).
     */
    public boolean hasDefaultConfigPath() {
        return getConfigPath() == null || StringUtils.isBlank(getConfigPath());
    }

    /**
     * @return true if {@link #getConfigPath()} is a global path. ie. starts
     *         with {@link #GLOBAL_CONFIG_PATH_PREFIX}.
     */
    public boolean hasGlobalConfigPath() {
        return getConfigPath().startsWith(GLOBAL_CONFIG_PATH_PREFIX);
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
     * @return the groupsAndImports
     */
    public GroupsAndImports getGroupsAndImports() {
        return groupsAndImports;
    }

    /**
     * @param groupsAndImports the groupsAndImports to set
     */
    public void setGroupsAndImports(final GroupsAndImports groupsAndImports) {
        this.groupsAndImports = groupsAndImports;
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
        return Arrays.asList(matchingGroups(device)).iterator();
    }

    /**
     * @param device
     *            {@link Device} to match against each group.
     * @return {@link Group}s that match the given {@link Device}.
     */
    public Group[] matchingGroups(final Device device) {
        final GroupsCacheKey groupsCacheKey = createGroupsCacheKey(device);
        if (getMatchingGroupsCache().contains(groupsCacheKey)) {
            debugLogGroupsFoundInCache();

            return getCachedGroups(groupsCacheKey);
        } else {
            debugLogGroupsNotFoundInCache();

            final List<Group> matchingGroups = getGroups().matchingGroups(device);
            final Group[] matchingGroupsArray = matchingGroups.toArray(new Group[] {});
            getMatchingGroupsCache().put(groupsCacheKey, matchingGroupsArray);
            return matchingGroupsArray;
        }

    }

    private GroupsCacheKey createGroupsCacheKey(final Device device) {
        return new GroupsCacheKeyBean(device.getUserAgent(), getConfigPath());
    }

    private void debugLogGroupsFoundInCache() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning groups from cache.");
        }
    }

    private void debugLogGroupsNotFoundInCache() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Groups not found in cache. Will fetch them.");
        }
    }

    private Group[] getCachedGroups(final GroupsCacheKey groupsCacheKey) {
        return getMatchingGroupsCache().get(groupsCacheKey);
    }

    /**
     * @return the matchingGroupsCache
     */
    public GroupsCache getMatchingGroupsCache() {
        return matchingGroupsCache;
    }

    /**
     * @param matchingGroupsCache the matchingGroupsCache to set
     */
    public void setMatchingGroupsCache(final GroupsCache matchingGroupsCache) {
        this.matchingGroupsCache = matchingGroupsCache;
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

        final UiConfiguration rhs = (UiConfiguration) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getSourceUrl(), rhs.getSourceUrl());
        equalsBuilder.append(getSourceTimestamp(), rhs.getSourceTimestamp());
        equalsBuilder.append(getConfigPath(), rhs.getConfigPath());
        equalsBuilder.append(getGroups(), rhs.getGroups());
        equalsBuilder.append(getGroupsAndImports(), rhs.getGroupsAndImports());

        // Ignore getMatchingGroupsCache();

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
        hashCodeBuilder.append(getGroupsAndImports());

        // Ignore getMatchingGroupsCache();

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
        toStringBuilder.append("groupsAndImports", getGroupsAndImports());

        // Ignore getMatchingGroupsCache();

        return toStringBuilder.toString();
    }

    /**
     * A friendly summary of all group names from {@link #getGroupsAndImports()}, excluding imports.
     *
     * @return A friendly summary of all group names from {@link #getGroupsAndImports()}, excluding
     *         imports.
     */
    public String groupsAndImportsGroupNameSummary() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("sourceUrl", getSourceUrl());
        toStringBuilder.append("configPath", getConfigPath());
        toStringBuilder.append("groups", getGroupsAndImports().groupNameSummary());

        return toStringBuilder.toString();
    }
}

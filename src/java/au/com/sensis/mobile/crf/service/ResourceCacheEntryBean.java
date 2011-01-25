package au.com.sensis.mobile.crf.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.util.TimeGeneratorFactory;

/**
 * Default {@link ResourceCacheEntry}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceCacheEntryBean implements ResourceCacheEntry {

    private static final long serialVersionUID = 1L;

    private int maxEmptyResourcesRefreshCount;
    private int minEmptyResourcesRefreshCountUpdateMilliseconds;

    private Resource [] resources;

    /**
     * Number of times that this cache entry's payload has been refreshed.
     */
    private int refreshCount;

    /**
     * Timestamp of the last time that {@link #getRefreshCount()} was updated.
     */
    private Date refreshCountLastUpdated;

    /**
     * Create this entry to hold the given resources.
     *
     * @param resources
     *            Resources that this entry should hold.
     * @param maxEmptyResourcesRefreshCount
     *            See {@link #getMaxEmptyResourcesRefreshCount()}.
     * @param minEmptyResourcesRefreshCountUpdateMilliseconds
     *            See
     *            {@link #getMinEmptyResourcesRefreshCountUpdateMilliseconds()}.
     */
    public ResourceCacheEntryBean(final Resource[] resources,
            final int maxEmptyResourcesRefreshCount,
            final int minEmptyResourcesRefreshCountUpdateMilliseconds) {
        setResources(resources);
        setMaxEmptyResourcesRefreshCount(maxEmptyResourcesRefreshCount);
        setMinEmptyResourcesRefreshCountUpdateMilliseconds(
                minEmptyResourcesRefreshCountUpdateMilliseconds);

        setRefreshCount(1);
    }

    private Date getCurrentTime() {
        return new Date(TimeGeneratorFactory.getTimeGeneratorSingleton().getTimeInMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource[] getResources() {
        return resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResourcesAsList() {
        return Arrays.asList(getResources());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmptyResources() {
        return (getResources() == null) || (getResources().length == 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean maxRefreshCountReached() {
        return getRefreshCount() > getMaxEmptyResourcesRefreshCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementRefreshCountRateLimited() {
        if (getCurrentTime().getTime() - getRefreshCountLastUpdated().getTime()
                >= getMinEmptyResourcesRefreshCountUpdateMilliseconds()) {
            setRefreshCount(getRefreshCount() + 1);
        }
    }

    /**
     * @return the refreshCount
     */
    @Override
    public int getRefreshCount() {
        return refreshCount;
    }

    /**
     * @return the refreshCountLastUpdated
     */
    private Date getRefreshCountLastUpdated() {
        return refreshCountLastUpdated;
    }

    /**
     * @param resources the resources to set
     */
    @Override
    public void setResources(final Resource[] resources) {
        this.resources = resources;
    }

    /**
     * @param refreshCount
     *            the refreshCount to set
     */
    private void setRefreshCount(final int accessCount) {
        setRefreshCountLastUpdated(getCurrentTime());
        refreshCount = accessCount;
    }

    /**
     * @param refreshCountLastUpdated the refreshCountLastUpdated to set
     */
    private void setRefreshCountLastUpdated(final Date accessCountLastUpdated) {
        refreshCountLastUpdated = accessCountLastUpdated;
    }

    /**
     * @return the maxEmptyResourcesRefreshCount
     */
    private int getMaxEmptyResourcesRefreshCount() {
        return maxEmptyResourcesRefreshCount;
    }

    /**
     * @return the minEmptyResourcesRefreshCountUpdateMilliseconds
     */
    private int getMinEmptyResourcesRefreshCountUpdateMilliseconds() {
        return minEmptyResourcesRefreshCountUpdateMilliseconds;
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

        final ResourceCacheEntryBean rhs = (ResourceCacheEntryBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(resources, rhs.resources);
        equalsBuilder.append(refreshCount, rhs.refreshCount);
        equalsBuilder.append(refreshCountLastUpdated, rhs.refreshCountLastUpdated);
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(resources);
        hashCodeBuilder.append(refreshCount);
        hashCodeBuilder.append(refreshCountLastUpdated);
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(resources)
            .append(refreshCount)
            .append(refreshCountLastUpdated)
            .toString();
    }

    /**
     * @param maxEmptyResourcesRefreshCount the maxEmptyResourcesRefreshCount to set
     */
    private void setMaxEmptyResourcesRefreshCount(final int maxEmptyResourcesRefreshCount) {
        this.maxEmptyResourcesRefreshCount = maxEmptyResourcesRefreshCount;
    }

    /**
     * @param minEmptyResourcesRefreshCountUpdateMilliseconds
     *            the minEmptyResourcesRefreshCountUpdateMilliseconds to set
     */
    private void setMinEmptyResourcesRefreshCountUpdateMilliseconds(
            final int minEmptyResourcesRefreshCountUpdateMilliseconds) {
        this.minEmptyResourcesRefreshCountUpdateMilliseconds =
                minEmptyResourcesRefreshCountUpdateMilliseconds;
    }
}

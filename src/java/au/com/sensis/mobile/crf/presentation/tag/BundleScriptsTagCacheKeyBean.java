package au.com.sensis.mobile.crf.presentation.tag;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Default {@link BundleScriptsTagCacheKey} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class BundleScriptsTagCacheKeyBean implements BundleScriptsTagCacheKey {

    private static final long serialVersionUID = 1L;

    private final String bundleId;
    private final Resource [] resources;

    /**
     * Constructor.
     *
     * @param bundleId Id associated with the bundle.
     * @param resources {@link Resource}s to be included in the bundle.
     */
    public BundleScriptsTagCacheKeyBean(final String bundleId, final Resource [] resources) {
        this.bundleId = bundleId;
        this.resources = resources;
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

        final BundleScriptsTagCacheKeyBean rhs = (BundleScriptsTagCacheKeyBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(bundleId, rhs.bundleId);
        equalsBuilder.append(resources, rhs.resources);
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(bundleId);
        hashCodeBuilder.append(resources);
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("bundleId", bundleId);
        toStringBuilder.append("resources", resources);
        return toStringBuilder.toString();
    }
}

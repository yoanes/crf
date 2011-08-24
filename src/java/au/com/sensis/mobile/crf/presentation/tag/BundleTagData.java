package au.com.sensis.mobile.crf.presentation.tag;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Contains data that child tags have registered with the owning {@link AbstractBundleTag}.
 *
 * @author Brendan Doyle
 * @author Adrian Koh
 */
public class BundleTagData {

    /**
     * List of resources that child tags have registered with this {@link AbstractBundleTag}
     * to be bundled into a single script.
     */
    private List<Resource> resourcesToBundle = new ArrayList<Resource>();

    /**
     * List of absoulte hrefs that child tags have registered with this {@link AbstractBundleTag}
     * to be be written out before the bundle. (Note, not actually included in the bundle).
     */
    private List<String> absoluteHrefsToRemember = new ArrayList<String>();

    /**
     * @return  the resourcesToBundle.
     */
    public List<Resource> getResourcesToBundle() {


        return resourcesToBundle;
    }

    /**
     * @param resourcesToBundle the resourcesToBundle to set.
     */
    public void setResourcesToBundle(final List<Resource> resourcesToBundle) {


        this.resourcesToBundle = resourcesToBundle;
    }

    /**
     * @return  the absoluteHrefsToRemember.
     */
    public List<String> getAbsoluteHrefsToRemember() {


        return absoluteHrefsToRemember;
    }

    /**
     * @param absoluteHrefsToRemember the absoluteHrefsToRemember to set.
     */
    public void setAbsoluteHrefsToRemember(final List<String> absoluteHrefsToRemember) {


        this.absoluteHrefsToRemember = absoluteHrefsToRemember;
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

        final BundleTagData rhs = (BundleTagData) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getResourcesToBundle(), rhs.getResourcesToBundle());
        equalsBuilder.append(getAbsoluteHrefsToRemember(), rhs.getAbsoluteHrefsToRemember());

        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

        hashCodeBuilder.append(getResourcesToBundle());
        hashCodeBuilder.append(getAbsoluteHrefsToRemember());

        return hashCodeBuilder.toHashCode();
    }
}

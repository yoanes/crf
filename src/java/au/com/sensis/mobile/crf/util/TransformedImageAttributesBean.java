package au.com.sensis.mobile.crf.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Default {@link TransformedImageAttributes} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class TransformedImageAttributesBean implements TransformedImageAttributes {

    private ImageAttributes sourceImageAttributes;
    private ImageAttributes outputImageAttributes;

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageAttributes getOutputImageAttributes() {
        return outputImageAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageAttributes getSourceImageAttributes() {
        return sourceImageAttributes;
    }

    /**
     * @param sourceImageAttributes the sourceImageAttributes to set
     */
    public void setSourceImageAttributes(final ImageAttributes sourceImageAttributes) {
        this.sourceImageAttributes = sourceImageAttributes;
    }

    /**
     * @param outputImageAttributes the outputImageAttributes to set
     */
    public void setOutputImageAttributes(final ImageAttributes outputImageAttributes) {
        this.outputImageAttributes = outputImageAttributes;
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

        final TransformedImageAttributesBean rhs = (TransformedImageAttributesBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getSourceImageAttributes(), rhs.getSourceImageAttributes());
        equalsBuilder.append(getOutputImageAttributes(), rhs.getOutputImageAttributes());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getSourceImageAttributes());
        hashCodeBuilder.append(getOutputImageAttributes());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        toStringBuilder.append("sourceImageAttributes", getSourceImageAttributes());
        toStringBuilder.append("outputImageAttributes", getOutputImageAttributes());

        return toStringBuilder.toString();
    }


}

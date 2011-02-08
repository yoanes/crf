package au.com.sensis.mobile.crf.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.util.ScaledImageFactory.ImageFormat;
import au.com.sensis.mobile.crf.util.ScaledImageFactory.ImageScalingParameters;

/**
 * Default {@link ImageScalingParameters}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageScalingParametersBean implements ImageScalingParameters {

    private int deviceImagePercentWidth;
    private int devicePixelWidth;
    private ImageFormat outputImageFormat;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDeviceImagePercentWidth() {
        return deviceImagePercentWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDevicePixelWidth() {
        return devicePixelWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageFormat getOutputImageFormat() {
        return outputImageFormat;
    }

    /**
     * @param deviceImagePercentWidth the deviceImagePercentWidth to set
     */
    public void setDeviceImagePercentWidth(final int deviceImagePercentWidth) {
        this.deviceImagePercentWidth = deviceImagePercentWidth;
    }

    /**
     * @param devicePixelWidth the devicePixelWidth to set
     */
    public void setDevicePixelWidth(final int devicePixelWidth) {
        this.devicePixelWidth = devicePixelWidth;
    }

    /**
     * @param outputImageFormat the outputImageFormat to set
     */
    public void setOutputImageFormat(final ImageFormat outputImageFormat) {
        this.outputImageFormat = outputImageFormat;
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

        final ImageScalingParametersBean rhs = (ImageScalingParametersBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getDevicePixelWidth(), rhs.getDevicePixelWidth());
        equalsBuilder.append(getDeviceImagePercentWidth(), rhs.getDeviceImagePercentWidth());
        equalsBuilder.append(getOutputImageFormat(), rhs.getOutputImageFormat());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getDevicePixelWidth());
        hashCodeBuilder.append(getDeviceImagePercentWidth());
        hashCodeBuilder.append(getOutputImageFormat());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        toStringBuilder.append("devicePixelWidth", getDevicePixelWidth());
        toStringBuilder.append("deviceImagePercentWidth", getDeviceImagePercentWidth());
        toStringBuilder.append("outputImageFormat", getOutputImageFormat());

        return toStringBuilder.toString();
    }


}

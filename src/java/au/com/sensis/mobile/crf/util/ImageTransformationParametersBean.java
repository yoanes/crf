package au.com.sensis.mobile.crf.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.util.ImageTransformationFactory.ImageFormat;
import au.com.sensis.mobile.crf.util.ImageTransformationFactory.ImageTransformationParameters;

/**
 * Default {@link ImageTransformationParameters}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTransformationParametersBean implements ImageTransformationParameters {

    private Integer deviceImagePercentWidth;
    private Integer absolutePixelWidth;
    private int devicePixelWidth;
    private ImageFormat outputImageFormat = ImageFormat.GIF;
    private String backgroundColor;

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getDeviceImagePercentWidth() {
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
    public void setDeviceImagePercentWidth(final Integer deviceImagePercentWidth) {
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
    public Integer getAbsolutePixelWidth() {
        return absolutePixelWidth;
    }

    /**
     * @param absolutePixelWidth the absolutePixelWidth to set
     */
    public void setAbsolutePixelWidth(final Integer absolutePixelWidth) {
        this.absolutePixelWidth = absolutePixelWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preserveOriginalDimensions() {
        return !scaleToAbsolutePixelWidth() && !scaleToPercentDeviceWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scaleToAbsolutePixelWidth() {
        return getAbsolutePixelWidth() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scaleToPercentDeviceWidth() {
        return !scaleToAbsolutePixelWidth() && (getDeviceImagePercentWidth() != null);
    }


    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
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

        final ImageTransformationParametersBean rhs = (ImageTransformationParametersBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getDevicePixelWidth(), rhs.getDevicePixelWidth());
        equalsBuilder.append(getDeviceImagePercentWidth(), rhs.getDeviceImagePercentWidth());
        equalsBuilder.append(getAbsolutePixelWidth(), rhs.getAbsolutePixelWidth());
        equalsBuilder.append(getOutputImageFormat(), rhs.getOutputImageFormat());
        equalsBuilder.append(getBackgroundColor(), rhs.getBackgroundColor());
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
        hashCodeBuilder.append(getAbsolutePixelWidth());
        hashCodeBuilder.append(getOutputImageFormat());
        hashCodeBuilder.append(getBackgroundColor());
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
        toStringBuilder.append("absolutePixelWidth", getAbsolutePixelWidth());
        toStringBuilder.append("outputImageFormat", getOutputImageFormat());
        toStringBuilder.append("backgroundColor", getBackgroundColor());

        return toStringBuilder.toString();
    }

}

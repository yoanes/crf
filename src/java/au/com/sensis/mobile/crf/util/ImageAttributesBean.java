package au.com.sensis.mobile.crf.util;

import java.io.File;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Default {@link ImageAttributes}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageAttributesBean implements ImageAttributes {

    private File imageFile;
    private int pixelWidth;
    private int pixelHeight;

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
        return imageFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPixelHeight() {
        return pixelHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPixelWidth() {
        return pixelWidth;
    }

    /**
     * @param imageFile the imageFile to set
     */
    public void setImageFile(final File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * @param pixelWidth the pixelWidth to set
     */
    public void setPixelWidth(final int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    /**
     * @param pixelHeight the pixelHeight to set
     */
    public void setPixelHeight(final int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAspectRatio() {
        return (double) getPixelWidth() / (double) getPixelHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getInverseAspectRatio() {
        return (double) getPixelHeight() / (double) getPixelWidth();
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

        final ImageAttributesBean rhs = (ImageAttributesBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getFile(), rhs.getFile());
        equalsBuilder.append(getPixelWidth(), rhs.getPixelWidth());
        equalsBuilder.append(getPixelHeight(), rhs.getPixelHeight());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getFile());
        hashCodeBuilder.append(getPixelWidth());
        hashCodeBuilder.append(getPixelHeight());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        toStringBuilder.append("imageFile", getFile());
        toStringBuilder.append("pixelWidth", getPixelWidth());
        toStringBuilder.append("pixelHeight", getPixelHeight());

        return toStringBuilder.toString();
    }

}

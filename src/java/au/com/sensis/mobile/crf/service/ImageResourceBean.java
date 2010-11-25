package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.config.Group;


/**
 * Represents an image {@link Resource}, having the additional image-specific
 * width and height properties.
 *
 * @author Tony Filipe
 */
public class ImageResourceBean extends ResourceBean {

    private int imageWidth;
    private int imageHeight;


    /**
     * Default constructor.
     *
     * @param originalPath
     *            Original path that was requested.
     * @param newPath
     *            New path that originalPath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newPath is relative to.
     * @param group {@link Group} that this {@link Resource} was found in.
     */
    public ImageResourceBean(final String originalPath, final String newPath,
            final File rootResourceDir, final Group group) {

        super(originalPath, newPath, rootResourceDir, group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.appendSuper(super.toString());
        toStringBuilder.append("width", getImageWidth());
        toStringBuilder.append("height", getImageHeight());
        return toStringBuilder.toString();
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {

        return imageWidth;
    }


    /**
     * @param imageWidth  the imageWidth to set
     */
    public void setImageWidth(final int imageWidth) {

        this.imageWidth = imageWidth;
    }


    /**
     * @return the imageHeight
     */
    public int getImageHeight() {

        return imageHeight;
    }


    /**
     * @param imageHeight  the imageHeight to set
     */
    public void setImageHeight(final int imageHeight) {

        this.imageHeight = imageHeight;
    }


}

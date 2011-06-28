package au.com.sensis.mobile.crf.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Default {@link ImageReader}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageReaderBean implements ImageReader {

    private static final Logger LOGGER = Logger.getLogger(ImageReaderBean.class);

    /**
     * {@inheritDoc}
     *
     * If the dimensions of the image cannot be determined, they are set to 0. We don't throw
     * an exception to cater to the case that there is an image format that the Java
     * platform cannot understand and yet a browser might. For example, SVG. Furthermore, with SVG,
     * it probably doesn't make sense to ask what its dimensions are.
     */
    @Override
    public ImageAttributes readImageAttributes(final File imageFile) {
        Validate.notNull(imageFile, "imageFile must not be null.");
        Validate.isTrue(imageFile.canRead(), "imageFile must be readable: '" + imageFile + "'");

        final ImageAttributesBean imageAttributesBean = new ImageAttributesBean();
        imageAttributesBean.setImageFile(imageFile);

        final Dimension dimension = getImageDimensions(imageFile);
        if (dimension != null) {
            imageAttributesBean.setPixelWidth((int) dimension.getWidth());
            imageAttributesBean.setPixelHeight((int) dimension.getHeight());
        }

        return imageAttributesBean;
    }

    private Dimension getImageDimensions(final File imageFile) {

        Dimension dimensions = null;

        ImageInputStream stream = null;
        javax.imageio.ImageReader reader = null;
        try {
            stream = new FileImageInputStream(imageFile);

            final Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(stream);

            if (readers.hasNext()) {
                reader = readers.next();
                dimensions = readImageDimensions(stream, reader);
            }

        } catch (final IOException e) {
            warnUnableToRetrieveImageDimensions(imageFile, e);

        } finally {
            cleanUp(imageFile, stream, reader);
        }

        return dimensions;
    }

    private void warnUnableToRetrieveImageDimensions(final File imageFile, final IOException e) {
        if (LOGGER.isEnabledFor(Level.WARN)) {
            // It will work OK without width and height, but this error shouldn't happen.
            // We don't output the full stack trace because this is only a mild warning.
            // We don't want to fill the logs unnecessarily.
            LOGGER.warn("Unable to retrieve image dimensions for " + imageFile.getName() + "\n "
                    + e);
        }
    }

    private void cleanUp(final File imageFile, final ImageInputStream stream,
            final javax.imageio.ImageReader reader) {

        if (reader != null) {
            reader.dispose();
        }

        if (stream != null) {
            closeStreamSafely(stream, imageFile);
        }
    }

    private Dimension readImageDimensions(final ImageInputStream stream,
            final javax.imageio.ImageReader reader) throws IOException {

        reader.setInput(stream);

        return new Dimension(reader.getWidth(reader.getMinIndex()), reader.getHeight(reader
                .getMinIndex()));
    }

    private void closeStreamSafely(final ImageInputStream stream, final File imageFile) {
        try {
            stream.close();
        } catch (final IOException e) {
            if (LOGGER.isEnabledFor(Level.WARN)) {
                LOGGER.warn("Unable to close inputstream for image: " + imageFile);
            }
        }
    }

}

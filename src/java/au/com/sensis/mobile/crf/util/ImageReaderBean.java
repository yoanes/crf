package au.com.sensis.mobile.crf.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
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
     */
    @Override
    public ImageAttributes readImageAttributes(final File imageFile) {
        Validate.notNull(imageFile, "imageFile must not be null.");
        Validate.isTrue(imageFile.canRead(), "imageFile must be readable: '" + imageFile + "'");

        final Dimension dimension  = getImageDimensions(imageFile);

        final ImageAttributesBean imageAttributesBean = new ImageAttributesBean();
        imageAttributesBean.setImageFile(imageFile);
        imageAttributesBean.setPixelWidth((int) dimension.getWidth());
        imageAttributesBean.setPixelHeight((int) dimension.getHeight());

        return imageAttributesBean;
    }

    private Dimension getImageDimensions(final File imageFile) {

        Dimension dimensions = null;

        final Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReadersBySuffix(
                FilenameUtils.getExtension(imageFile.getName()));

        if (readers.hasNext()) {
            final javax.imageio.ImageReader reader = readers.next();

            try {
                final ImageInputStream stream = new FileImageInputStream(imageFile);
                reader.setInput(stream);

                dimensions = new Dimension(reader.getWidth(reader.getMinIndex()),
                        reader.getHeight(reader.getMinIndex()));

            } catch (final IOException e) {
                // It will work OK without width and height, but this error shouldn't happen.
                LOGGER.warn("Unable to retrieve image dimensions for " + imageFile.getName()
                        + "\n " + e);
            } finally {
                reader.dispose();
            }
        }

        return dimensions;
    }

}

package au.com.sensis.mobile.crf.util;

import java.io.File;

/**
 * Encapsulates attributes of an image file.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ImageAttributes {

    /**
     * @return File of the image.
     */
    File getFile();

    /**
     * @return pixel width of the image.
     */
    int getPixelWidth();

    /**
     * @return pixel height of the image.
     */
    int getPixelHeight();

    /**
     * @return the aspect ratio of the image.
     */
    double getAspectRatio();

    /**
     * @return the inverse of {@link #getAspectRatio()}.
     */
    double getInverseAspectRatio();
}

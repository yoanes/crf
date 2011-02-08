package au.com.sensis.mobile.crf.util;

import java.io.File;

/**
 * Encapsulates an image file and its details.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Image {

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
}

package au.com.sensis.mobile.crf.util;

import java.io.File;

/**
 * Interface for reading images and their attributes.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ImageReader {

    /**
     * Read attributes of the given image.
     *
     * @param image Image to read.
     * @return attributes of the given image.
     */
    ImageAttributes readImageAttributes(File image);
}

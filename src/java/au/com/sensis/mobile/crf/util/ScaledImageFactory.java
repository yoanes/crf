package au.com.sensis.mobile.crf.util;

import java.io.File;

/**
 * Factory for generating scaled images.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ScaledImageFactory {

    /**
     * Output image formats supported.
     *
     * @author Adrian.Koh2@sensis.com.au
     */
    enum ImageFormat {
        /**
         * png image format.
         */
        PNG,

        /**
         * gif image format.
         */
        GIF,

        /**
         * jpeg image format.
         */
        JPEG;
    }

    /**
     * Parameters to control the scaling that is applied.
     */
    interface ImageScalingParameters {
        /**
         * @return pixel width of the device that the image is to be generated for.
         */
        int getDevicePixelWidth();

        /**
         * @return percentage of the device's screen width that the scaled image should occupy.
         * The aspect ratio of the source image will be preserved.
         */
        int getDeviceImagePercentWidth();

        /**
         * @return Output image format.
         */
        ImageFormat getOutputImageFormat();
    }

    /**
     * Scale an image.
     *
     * @param sourceImageFile The source image to be scaled.
     * @param outputImageBaseDir Base directory to output the scaled image to.
     * @param imageScalingParameters Parameters to control the scaling that is applied.
     * @return Details of the generated image.
     */
    Image scaleImage(File sourceImageFile, File outputImageBaseDir,
            ImageScalingParameters imageScalingParameters);
}

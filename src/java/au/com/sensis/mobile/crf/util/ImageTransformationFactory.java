package au.com.sensis.mobile.crf.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * Factory for generating scaled images.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ImageTransformationFactory {

    /**
     * Output image formats supported.
     *
     * @author Adrian.Koh2@sensis.com.au
     */
    enum ImageFormat {

        /**
         * png image format.
         */
        PNG("png"),

        /**
         * gif image format.
         */
        GIF("gif"),

        /**
         * jpeg image format.
         */
        JPEG("jpg");

        private ImageFormat(final String fileExtension) {
            this.fileExtension = fileExtension;
        }

        private final String fileExtension;

        /**
         * @return the fileExtension
         */
        public String getFileExtension() {
            return fileExtension;
        }

        /**
         * @param file File to be tested.
         * @return true if the file has an extension that matches this {@link ImageFormat}.
         */
        public boolean hasExtension(final File file) {
            return getFileExtension().equals(FilenameUtils.getExtension(file.getPath()));
        }

        /**
         * @param format ImageFormat as a string.
         * @return {@link ImageFormat} if conversion is possible. Null otherwise.
         */
        public static ImageFormat fromString(final String format) {
            for (final ImageFormat imageFormat : values()) {
                if (imageFormat.getFileExtension().equalsIgnoreCase(format)) {
                    return imageFormat;
                }
            }

            return null;
        }
    }

    /**
     * Parameters to control the transformation that is applied.
     */
    interface ImageTransformationParameters {

        /**
         * @return pixel width of the device that the image is to be generated for.
         */
        int getDevicePixelWidth();

        /**
         * @return percentage of the device's screen width that the scaled image
         *         should occupy. The aspect ratio of the source image will be
         *         preserved. Will only be non-null if
         *         {@link #scaleToAbsolutePixelWidth()} is false and
         *         {@link #scaleToPercentDeviceWidth()} is true. ie.
         *         {@link #getAbsolutePixelWidth()} takes precedence.
         */
        Integer getDeviceImagePercentWidth();

        /**
         * @return absolute pixel width of the scaled image. The aspect ratio of
         *         the source image will be preserved. Will only be non-null if
         *         {@link #scaleToAbsolutePixelWidth()} is true.
         */
        Integer getAbsolutePixelWidth();

        /**
         * @return true if the image should be scaled according to
         *         {@link #getDeviceImagePercentWidth()}.
         */
        boolean scaleToPercentDeviceWidth();

        /**
         * @return true if the image should be scaled according to
         *         {@link #getAbsolutePixelWidth()}. This takes precedence over
         *         {@link #scaleToPercentDeviceWidth()}.
         */
        boolean scaleToAbsolutePixelWidth();

        /**
         * @return true if no scaling is to be performed.
         */
        boolean preserveOriginalDimensions();

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
     * @param imageTransformationParameters Parameters to control the scaling that is applied.
     * @return Details of the generated image.
     */
    TransformedImageAttributes transformImage(File sourceImageFile, File outputImageBaseDir,
            ImageTransformationParameters imageTransformationParameters);
}

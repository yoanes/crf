package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * {@link ImageTransformationFactory} that lookups the transformed image from the pre-generated
 * images on disk.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class PregeneratedFileLookupImageTransformationFactoryBean
    extends AbstractImageTransformationFactoryBean {

    /**
     * @param imageReader
     *            {@link ImageReader} to use to read images.
     */
    public PregeneratedFileLookupImageTransformationFactoryBean(final ImageReader imageReader) {
        super(imageReader);
    }

    @Override
    protected TransformedImageAttributes doTransformImage(final File sourceImageFile,
            final ImageAttributes sourceImageAttributes, final File baseTargetImageDir,
            final ImageTransformationParameters imageTransformationParameters) throws IOException {

        final FileAndWidthPair pregeneratedImageAndWidth =
                findNearestPregeneratedImage(baseTargetImageDir, sourceImageAttributes,
                        imageTransformationParameters);

        validateImageFound(pregeneratedImageAndWidth, baseTargetImageDir, sourceImageFile);

        final int outputImageHeight = getPregeneratedImageHeight(
                pregeneratedImageAndWidth.getFile());

        return createTransformedImageAttributes(sourceImageAttributes,
                pregeneratedImageAndWidth.getFile(), pregeneratedImageAndWidth.getWidth(),
                outputImageHeight);

    }

    private void validateImageFound(final FileAndWidthPair foundImage, final File baseOutputDir,
            final File sourceImageFile) {
        if (foundImage == null) {
            throw new ImageCreationException(sourceImageFile.getName()
                    + " could not be found under any width directory below " + baseOutputDir);
        }
    }

    private FileAndWidthPair findNearestPregeneratedImage(final File baseTargetImageDir,
            final ImageAttributes sourceImageAttributes,
            final ImageTransformationParameters imageTransformationParameters) {

        final int requestedOutputImageWidth = calculateRequestedOutputImageWidth(
                imageTransformationParameters, sourceImageAttributes);

        final TreeSet<FileAndWidthPair> imagesSortedByWidth =
            findPregeneratedImagesSortedByWidth(sourceImageAttributes.getFile(),
                    baseTargetImageDir, imageTransformationParameters.getOutputImageFormat());

        return getSmallestImageClosestToRequestedWidth(requestedOutputImageWidth,
                imagesSortedByWidth);

    }

    private FileAndWidthPair getSmallestImageClosestToRequestedWidth(
            final int requestedOutputImageWidth,
            final TreeSet<FileAndWidthPair> imagesSortedByWidth) {

        final FileAndWidthPair imageWithGreatestLowerBoundWidth = imagesSortedByWidth
                .floor(new FileAndWidthPair(requestedOutputImageWidth));

        if (imageWithGreatestLowerBoundWidth != null) {
            return imageWithGreatestLowerBoundWidth;
        } else {
            // The requested image width is smaller than the smallest output image found so
            // just return the smallest we found.
            return firstOrNull(imagesSortedByWidth);
        }

    }

    private FileAndWidthPair firstOrNull(final TreeSet<FileAndWidthPair> fileAndWidthPairs) {
        if (!fileAndWidthPairs.isEmpty()) {
            return fileAndWidthPairs.first();
        } else {
            return null;
        }
    }

    private TreeSet<FileAndWidthPair> findPregeneratedImagesSortedByWidth(
            final File sourceImage, final File baseTargetImageDir,
            final ImageFormat requestedImageFormat) {

        final String nameOfImageToFind = computePregeneratedImageName(
                sourceImage, requestedImageFormat);
        final File[] foundPregeneratedImages =
            FileIoFacadeFactory.getFileIoFacadeSingleton().listByFilenameAndDirnameWildcardPatterns(
                    baseTargetImageDir,
                    new String [] { nameOfImageToFind },
                    new String [] { "w*", "h*" });

        final TreeSet<FileAndWidthPair> imagesSortedByWidth = new TreeSet<FileAndWidthPair>();
        for (final File foundImage : foundPregeneratedImages) {

            if (sourceImage.equals(foundImage)) {
                // The directory listing may also contain the source image so skip it.
                continue;
            }

            // foundImage.getParentFile().getParentFile().getName() is guaranteed not to throw null
            // pointer exceptions along the way due to the way we obtained the
            // foundPregeneratedImages above.
            final String imageWidth = StringUtils.stripStart(
                    foundImage.getParentFile().getParentFile().getName(), "w");
            try {
                imagesSortedByWidth.add(
                        new FileAndWidthPair(Integer.valueOf(imageWidth), foundImage));
            } catch (final NumberFormatException e) {
                throw new ImageCreationException("The found image '"
                        + foundImage + "' has an invalid width specified in its path.");
            }
        }
        return imagesSortedByWidth;
    }

    private String computePregeneratedImageName(final File sourceImage,
            final ImageFormat requestedImageFormat) {
        return FilenameUtils.getBaseName(sourceImage.getName())
            + "." + requestedImageFormat.getFileExtension();
    }

    private int getPregeneratedImageHeight(final File outputImage) {
        final File parentDir = outputImage.getParentFile();
        final String heightAsString = StringUtils.stripStart(parentDir.getName(), "h");
        try {
            return Integer.parseInt(heightAsString);
        } catch (final NumberFormatException e) {
            throw new ImageCreationException("The found image '" + outputImage
                    + "' has an invalid height specified in its path.", e);
        }
    }

    private class FileAndWidthPair implements Comparable<FileAndWidthPair>{
        private final Integer width;
        private File file;

        private FileAndWidthPair(final Integer width, final File file) {
            this.width = width;
            this.file = file;
        }

        private FileAndWidthPair(final int width) {
            this.width = width;
        }

        private Integer getWidth() {
            return width;
        }

        private File getFile() {
            return file;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(final FileAndWidthPair other) {
            // Only consider the width because this is what we wish to order by.
            return getWidth().compareTo(other.getWidth());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || !this.getClass().equals(obj.getClass())) {
                return false;
            }

            final FileAndWidthPair rhs = (FileAndWidthPair) obj;
            final EqualsBuilder equalsBuilder = new EqualsBuilder();

            // Only consider the width in order to be consistent with the compareTo method.
            equalsBuilder.append(this.width, rhs.width);

            return equalsBuilder.isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

            // Only consider the width in order to be consistent with the compareTo method.
            hashCodeBuilder.append(this.width);

            return hashCodeBuilder.toHashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("file", this.file)
                .append("width", this.width)
                .toString();
        }


    }
}

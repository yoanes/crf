package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

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

        final File[] foundPregeneratedImages =
            FileIoFacadeFactory.getFileIoFacadeSingleton().listByFilenameAndDirnameWildcardPatterns(
                    baseTargetImageDir,
                    new String [] { sourceImageAttributes.getFile().getName() },
                    new String [] { "w*", "h*" });


        final TreeSet<FileAndWidthPair> availableImagesAndWidths =
            buildTreeSetFromFoundPregeneratedImages(baseTargetImageDir, foundPregeneratedImages);

        final FileAndWidthPair imageWithGreatestLowerBoundWidth = availableImagesAndWidths
                .floor(new FileAndWidthPair(requestedOutputImageWidth));

        if (imageWithGreatestLowerBoundWidth != null) {
            return imageWithGreatestLowerBoundWidth;
        } else {
            // The requested image width is smaller than the smallest output image found so
            // just return the smallest we found.
            return firstOrNull(availableImagesAndWidths);
        }

    }

    private FileAndWidthPair firstOrNull(final TreeSet<FileAndWidthPair> fileAndWidthPairs) {
        if (!fileAndWidthPairs.isEmpty()) {
            return fileAndWidthPairs.first();
        } else {
            return null;
        }
    }

    private TreeSet<FileAndWidthPair> buildTreeSetFromFoundPregeneratedImages(
            final File baseTargetImageDir, final File[] foundPregeneratedImages) {
        final TreeSet<FileAndWidthPair> availableImagesAndWidths = new TreeSet<FileAndWidthPair>();
        for (final File foundImage : foundPregeneratedImages) {
            // foundImage.getParentFile().getParentFile().getName() is guaranteed not to throw null
            // pointer exceptions along the way due to the method that generated the
            // foundPregeneratedImages.
            final String availableWidth = StringUtils.stripStart(
                    foundImage.getParentFile().getParentFile().getName(), "w");
            try {
                availableImagesAndWidths.add(
                        new FileAndWidthPair(Integer.valueOf(availableWidth), foundImage));
            } catch (final NumberFormatException e) {
                throw new ImageCreationException("The found image '"
                        + foundImage + "' has an invalid width specified in its path.");
            }
        }
        return availableImagesAndWidths;
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

        @Override
        public int compareTo(final FileAndWidthPair other) {
            return getWidth().compareTo(other.getWidth());
        }
    }
}

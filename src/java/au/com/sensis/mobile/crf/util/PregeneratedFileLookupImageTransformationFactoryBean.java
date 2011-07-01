package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * {@link ImageTransformationFactory} that lookups the transformed image from the pre-generated
 * images on disk.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class PregeneratedFileLookupImageTransformationFactoryBean
    extends AbstractImageTransformationFactoryBean {

    // Not final so that we can inject a mock during testing.
    private static Logger logger =
            Logger.getLogger(PregeneratedFileLookupImageTransformationFactoryBean.class);

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

        final int outputImageWidth =
                findNearestPregeneratedImageWidth(baseTargetImageDir, sourceImageAttributes,
                        imageTransformationParameters);

        final File outputImageWidthDir = new File(baseTargetImageDir, "w" + outputImageWidth);

        final String outputImageFilename =
                createOutputImageFilename(imageTransformationParameters, sourceImageFile);
        final File[] foundPregeneratedImages =
                FileIoFacadeFactory.getFileIoFacadeSingleton().list(outputImageWidthDir,
                        outputImageFilename, "h*");

        validateImageFound(foundPregeneratedImages, outputImageWidthDir, sourceImageFile);

        logWarnIfMultipleImagesFound(foundPregeneratedImages);

        final File outputImage = foundPregeneratedImages[0];
        final int outputImageHeight = getPregeneratedImageHeight(outputImage);

        return createTransformedImageAttributes(sourceImageAttributes, foundPregeneratedImages[0],
                outputImageWidth, outputImageHeight);

    }

    private void logWarnIfMultipleImagesFound(final File[] foundPregeneratedImages) {
        if (foundPregeneratedImages.length > 1 && logger.isEnabledFor(Level.WARN)) {
            logger.warn("Multiple images found with the same width: "
                    + ArrayUtils.toString(foundPregeneratedImages) + ". Returning the first one. ");
        }
    }

    private void validateImageFound(final File[] foundPregeneratedImages, final File baseOutputDir,
            final File sourceImageFile) {
        if (foundPregeneratedImages.length == 0) {
            throw new ImageCreationException(sourceImageFile.getName()
                    + " could not be found under " + baseOutputDir);
        }
    }

    private int findNearestPregeneratedImageWidth(final File baseTargetImageDir,
            final ImageAttributes sourceImageAttributes,
            final ImageTransformationParameters imageTransformationParameters) {

        final int outputImageWidthRequested = calculateRequestedOutputImageWidth(
                imageTransformationParameters, sourceImageAttributes);

        final File[] availableOutputImageWidthDirs = getAllAvailableImageWidthDirs(
                baseTargetImageDir);

        final TreeSet<Integer> availableOutputImageWidths =
            buildTreeSetFromFoundWidths(availableOutputImageWidthDirs);

        final NavigableSet<Integer> widthsLessThanOrEqualToRequested = availableOutputImageWidths
                .headSet(new Integer(outputImageWidthRequested), true);
        final Integer greatestLowerBoundWidth = widthsLessThanOrEqualToRequested.pollLast();

        if (greatestLowerBoundWidth != null) {
            return greatestLowerBoundWidth;
        } else {
            // The requested image width is smallest than the smallest output image found so
            // just return the smallest we found.
            return availableOutputImageWidths.pollFirst();
        }

    }

    private TreeSet<Integer> buildTreeSetFromFoundWidths(
            final File[] availableOutputImageWidthDirs) {

        final TreeSet<Integer> availableOutputImageWidths = new TreeSet<Integer>();
        for (final File imageWidthDir : availableOutputImageWidthDirs) {
            final String availableWidth = StringUtils.stripStart(imageWidthDir.getName(), "w");
            try {
                availableOutputImageWidths.add(Integer.parseInt(availableWidth));
            } catch (final NumberFormatException e) {
                throw new ImageCreationException("The found image width directory '"
                        + imageWidthDir + "' has an invalid width specified in its path.");
            }
        }
        return availableOutputImageWidths;
    }

    private File[] getAllAvailableImageWidthDirs(final File baseTargetImageDir) {
        final File[] availableOutputImageWidthDirs = FileIoFacadeFactory.getFileIoFacadeSingleton()
                .list(baseTargetImageDir, new String[] { "w*" });

        if (availableOutputImageWidthDirs.length == 0) {
            throw new ImageCreationException("No pregenerated image directories found under "
                    + baseTargetImageDir);
        }
        return availableOutputImageWidthDirs;
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
}

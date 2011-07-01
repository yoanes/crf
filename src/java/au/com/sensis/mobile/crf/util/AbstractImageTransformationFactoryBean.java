package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * Base class for {@link ImageTransformationFactory} implementations.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractImageTransformationFactoryBean
    implements ImageTransformationFactory {

    private static final Logger LOGGER =
            Logger.getLogger(AbstractImageTransformationFactoryBean.class);

    private static final double PERCENTAGE_DIVISOR = 100.0d;

    private final ImageReader imageReader;

    /**
     * Constructor.
     *
     * @param imageReader
     *            {@link ImageReader} to use to read images.
     */
    public AbstractImageTransformationFactoryBean(
            final ImageReader imageReader) {

        this.imageReader = imageReader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformedImageAttributes transformImage(final File sourceImageFile,
            final File baseOutputImageDir,
            final ImageTransformationParameters imageTransformationParameters) {

        validateSourceImageFile(sourceImageFile);
        validateBaseOutputImageDir(baseOutputImageDir);

        try {
            return transformImageIfRequired(sourceImageFile, baseOutputImageDir,
                    imageTransformationParameters);

        } catch (final ImageCreationException e) {
            throw e;

        } catch (final Exception e) {
            throw new ImageCreationException("Error scaling source image '" + sourceImageFile
                    + "' to base target directory of '" + baseOutputImageDir
                    + "' using parameters: '" + imageTransformationParameters + "'", e);
        }

    }

    private void validateBaseOutputImageDir(final File baseOutputImageDir) {
        if ((baseOutputImageDir == null) || !baseOutputImageDir.isDirectory()) {
            throw new ImageCreationException("Base output image dir is not a directory: '"
                    + baseOutputImageDir + "'");
        }
    }

    private void validateSourceImageFile(final File sourceImageFile) {
        if ((sourceImageFile == null) || !sourceImageFile.canRead()) {
            throw new ImageCreationException("Cannot read source image file: '" + sourceImageFile
                    + "'");
        }

    }

    private TransformedImageAttributes transformImageIfRequired(final File sourceImageFile,
            final File baseTargetImageDir,
            final ImageTransformationParameters imageTransformationParameters) throws IOException {

        final ImageAttributes sourceImageAttributes =
                getImageReader().readImageAttributes(sourceImageFile);

        if (transformationRequired(sourceImageFile, imageTransformationParameters)) {

            return doTransformImage(sourceImageFile, sourceImageAttributes,
                    baseTargetImageDir, imageTransformationParameters);

        } else {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No transformation required for source: " + sourceImageFile
                        + " and parameters: " + imageTransformationParameters);
            }

            return createIdentityTransformedImageAttributes(sourceImageAttributes);
        }
    }


    private boolean transformationRequired(final File sourceImageFile,
            final ImageTransformationParameters imageTransformationParameters) {

        return !imageTransformationParameters.getOutputImageFormat().hasExtension(sourceImageFile)
                || !imageTransformationParameters.preserveOriginalDimensions();
    }

    /**
     * Perform the actual transformation.
     *
     * @param sourceImageFile Source image to transform.
     * @param sourceImageAttributes Attributes of the source image.
     * @param baseTargetImageDir Base directory to store the transformed image in.
     * @param imageTransformationParameters Parameters to control the transformation.
     * @return the transformed source image.
     * @throws IOException Thrown if there was an error in transforming the image.
     */
    protected abstract TransformedImageAttributes doTransformImage(File sourceImageFile,
            ImageAttributes sourceImageAttributes, File baseTargetImageDir,
            ImageTransformationParameters imageTransformationParameters) throws IOException;

    private TransformedImageAttributes createIdentityTransformedImageAttributes(
            final ImageAttributes sourceImageAttributes) {

        final TransformedImageAttributesBean transformedImageAttributesBean =
                new TransformedImageAttributesBean();

        transformedImageAttributesBean.setSourceImageAttributes(sourceImageAttributes);
        transformedImageAttributesBean.setOutputImageAttributes(sourceImageAttributes);
        return transformedImageAttributesBean;
    }

    /**
     * @return the imageReader
     */
    private ImageReader getImageReader() {
        return imageReader;
    }

    /**
     * Calculate how wide the output image should be.
     *
     * @param imageTransformationParameters Parameters to control the scaling that is applied.
     * @param sourceImageAttributes Attributes of the source image.
     * @return how wide the output image should be.
     */
    protected int calculateRequestedOutputImageWidth(
            final ImageTransformationParameters imageTransformationParameters,
            final ImageAttributes sourceImageAttributes) {

        if (imageTransformationParameters.scaleToAbsolutePixelWidth()) {
            return imageTransformationParameters.getAbsolutePixelWidth();

        } else if (imageTransformationParameters.scaleToPercentDeviceWidth()) {

            return (int) ((getPercentAsDecimal(imageTransformationParameters
                    .getDeviceImagePercentWidth())) * imageTransformationParameters
                    .getDevicePixelWidth());
        } else {
            // Preserve original width.
            return sourceImageAttributes.getPixelWidth();
        }
    }

    private double getPercentAsDecimal(final int percent) {
        return percent / PERCENTAGE_DIVISOR;
    }

    /**
     * @param sourceImageAttributes Attributes of the source image.
     * @param outputImageFile The output image file.
     * @param scaledImageWidth Width of the scaled output image.
     * @param scaledImageHeight Height of the scaled output image.
     * @return new {@link TransformedImageAttributes} containing the passed in data.
     */
    protected final TransformedImageAttributes createTransformedImageAttributes(
            final ImageAttributes sourceImageAttributes, final File outputImageFile,
            final int scaledImageWidth, final int scaledImageHeight) {

        final TransformedImageAttributesBean transformedImageAttributesBean =
                new TransformedImageAttributesBean();

        transformedImageAttributesBean.setSourceImageAttributes(sourceImageAttributes);
        transformedImageAttributesBean.setOutputImageAttributes(createImageAttributes(
                outputImageFile, scaledImageWidth, scaledImageHeight));

        return transformedImageAttributesBean;
    }

    private ImageAttributes createImageAttributes(final File outputImageFile,
            final int scaledImageWidth, final int scaledImageHeight) {
        final ImageAttributesBean attributesBean = new ImageAttributesBean();
        attributesBean.setImageFile(outputImageFile);
        attributesBean.setPixelWidth(scaledImageWidth);
        attributesBean.setPixelHeight(scaledImageHeight);
        return attributesBean;
    }

    /**
     * @param imageTransformationParameters Parameters being used to create the output image.
     * @param sourceImage The source image file.
     * @return Output image filename, computede from the passed in data.
     */
    protected String createOutputImageFilename(
            final ImageTransformationParameters imageTransformationParameters,
            final File sourceImage) {
        return FilenameUtils.getBaseName(sourceImage.getPath()) + "."
                + imageTransformationParameters.getOutputImageFormat().getFileExtension();
    }

}

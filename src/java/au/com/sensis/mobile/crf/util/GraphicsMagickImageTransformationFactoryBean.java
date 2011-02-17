package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * {@link ImageTransformationFactory} that invokes a command line to perform the
 * scaling.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GraphicsMagickImageTransformationFactoryBean implements ImageTransformationFactory {

    private static final Logger LOGGER =
            Logger.getLogger(GraphicsMagickImageTransformationFactoryBean.class);

    private static final double PERCENTAGE_DIVISOR = 100.0d;

    /**
     * Millisecond interval to poll whether the launched command line process has finished.
     */
    public static final int PROCESS_POLLING_INTERVAL_MILLISECONDS = 100;

    private final ImageReader imageReader;
    private final ProcessStarter processStarter;
    private final String graphicsMagickExecutable;

    /**
     * Constructor.
     *
     * @param processStarter
     *            {@link ProcessStarter} to start a process.
     * @param imageReader
     *            {@link ImageReader} to use to read images.
     * @param graphicsMagickExecutable
     *            path of the GraphicsMagick executable.
     */
    public GraphicsMagickImageTransformationFactoryBean(final ProcessStarter processStarter,
            final ImageReader imageReader, final String graphicsMagickExecutable) {

        this.processStarter = processStarter;
        this.imageReader = imageReader;
        this.graphicsMagickExecutable = graphicsMagickExecutable;
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
            return doTransformImage(sourceImageFile, baseOutputImageDir,
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

    private TransformedImageAttributes doTransformImage(final File sourceImageFile,
            final File baseTargetImageDir,
            final ImageTransformationParameters imageTransformationParameters) throws IOException {

        final ImageAttributes sourceImageAttributes =
                getImageReader().readImageAttributes(sourceImageFile);

        if (transformationRequired(sourceImageFile, imageTransformationParameters)) {

            return transformImageByCommandLine(sourceImageFile, baseTargetImageDir,
                    imageTransformationParameters, sourceImageAttributes);

        } else {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No transformation required for source: " + sourceImageFile
                        + " and parameters: " + imageTransformationParameters);
            }

            return createTransformedImageAttributes(sourceImageAttributes);
        }
    }

    private TransformedImageAttributes transformImageByCommandLine(final File sourceImageFile,
            final File baseTargetImageDir,
            final ImageTransformationParameters imageTransformationParameters,
            final ImageAttributes sourceImageAttributes) throws IOException {

        final int outputImageWidth =
                calculateOutputImageWidth(imageTransformationParameters, sourceImageAttributes);
        final int outputImageHeight =
                calculateOutputImageHeight(outputImageWidth, sourceImageAttributes);

        final File outputImageDir =
                createOutputImageDir(outputImageWidth, outputImageHeight, baseTargetImageDir);
        final File outputImageFile =
                createOutputImageFile(imageTransformationParameters, outputImageDir,
                        sourceImageFile);

        final List<String> commandLine =
                createCommandLine(sourceImageFile, imageTransformationParameters,
                        outputImageFile, outputImageWidth);
        final Process process = getProcessStarter().start(commandLine);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Transforming image using command: " + commandLine);
        }

        final int processExitValue = waitForProcess(process);
        validateProcessExitedSuccessfully(processExitValue, commandLine);

        return createTransformedImageAttributes(sourceImageAttributes, outputImageFile,
                outputImageWidth, outputImageHeight);
    }

    private boolean transformationRequired(final File sourceImageFile,
            final ImageTransformationParameters imageTransformationParameters) {

        return !imageTransformationParameters.getOutputImageFormat().hasExtension(sourceImageFile)
                || !imageTransformationParameters.preserveOriginalDimensions();
    }

    private void validateProcessExitedSuccessfully(final int processExitValue,
            final List<String> commandLine) {
        if (processExitValue != 0) {
            throw new ImageCreationException("Error when creating scaled image using command: "
                    + commandLine + ". Process exit code: " + processExitValue);
        }
    }

    private int waitForProcess(final Process process) {
        while (!hasProcessExited(process)) {
            try {
                Thread.sleep(PROCESS_POLLING_INTERVAL_MILLISECONDS);
            } catch (final InterruptedException e) {
                throw new IllegalStateException(
                        "We were interrupted when waiting for the image scaling process to exit. "
                                + "This really, really shouldn't happen.", e);
            }
        }
        return process.exitValue();

    }

    private boolean hasProcessExited(final Process process) {
        try {
            process.exitValue();
            return true;
        } catch (final IllegalThreadStateException e) {
            // This just means that the process has not yet finished.
            return false;
        }
    }

    private int calculateOutputImageHeight(final int outputImageWidth,
            final ImageAttributes sourceImageAttributes) {

        return (int) (sourceImageAttributes.getInverseAspectRatio() * outputImageWidth);
    }

    private int calculateOutputImageWidth(
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

    private TransformedImageAttributes createTransformedImageAttributes(
            final ImageAttributes sourceImageAttributes, final File outputImageFile,
            final int scaledImageWidth, final int scaledImageHeight) {

        final TransformedImageAttributesBean transformedImageAttributesBean =
                new TransformedImageAttributesBean();

        transformedImageAttributesBean.setSourceImageAttributes(sourceImageAttributes);
        transformedImageAttributesBean.setOutputImageAttributes(createImageAttributes(
                outputImageFile, scaledImageWidth, scaledImageHeight));
        return transformedImageAttributesBean;
    }

    private TransformedImageAttributes createTransformedImageAttributes(
            final ImageAttributes sourceImageAttributes) {

        final TransformedImageAttributesBean transformedImageAttributesBean =
            new TransformedImageAttributesBean();

        transformedImageAttributesBean.setSourceImageAttributes(sourceImageAttributes);
        transformedImageAttributesBean.setOutputImageAttributes(sourceImageAttributes);
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

    private File createOutputImageDir(final int scaledImageWidth, final int scaledImageHeight,
            final File baseTargetImageDir) throws IOException {

        final File outputImageDir =
                new File(baseTargetImageDir, "w" + scaledImageWidth + "/h" + scaledImageHeight);
        FileIoFacadeFactory.getFileIoFacadeSingleton().mkdirs(outputImageDir);

        return outputImageDir;
    }

    private File createOutputImageFile(
            final ImageTransformationParameters imageTransformationParameters,
            final File outputImageDir, final File sourceImage) {

        final String outputImageFilename =
                FilenameUtils.getBaseName(sourceImage.getPath()) + "."
                        + imageTransformationParameters.getOutputImageFormat().getFileExtension();

        return new File(outputImageDir, outputImageFilename);
    }

    private List<String> createCommandLine(final File sourceImageFile,
            final ImageTransformationParameters imageTransformationParameters,
            final File outputImageFile, final int scaledImageWidth) {

        final List<String> commandLine = new ArrayList<String>();

        commandLine.add(getGraphicsMagickExecutable());
        commandLine.add("convert");

        if (imageTransformationParameters.scaleToPercentDeviceWidth()
                || imageTransformationParameters.scaleToAbsolutePixelWidth()) {
            commandLine.add("-resize");
            commandLine.add(scaledImageWidth + "x");
            commandLine.add("-unsharp");
            commandLine.add("0x1");
        }

        commandLine.add(sourceImageFile.getPath());
        commandLine.add(outputImageFile.getPath());
        return commandLine;
    }

    private double getPercentAsDecimal(final int percent) {
        return percent / PERCENTAGE_DIVISOR;
    }

    /**
     * @return the imageReader
     */
    private ImageReader getImageReader() {
        return imageReader;
    }

    /**
     * @return the processStarter
     */
    private ProcessStarter getProcessStarter() {
        return processStarter;
    }

    /**
     * @return the graphicsMagickExecutable
     */
    private String getGraphicsMagickExecutable() {
        return graphicsMagickExecutable;
    }
}

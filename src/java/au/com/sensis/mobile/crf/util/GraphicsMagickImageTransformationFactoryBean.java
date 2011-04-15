package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * {@link ImageTransformationFactory} that invokes a command line to perform the
 * scaling.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// TODO: class was not completely finished, since we decided to switch to pre-generating images
// at build time and only looking them up on disk at runtime
// (PregeneratedFileLookupImageTransformationFactoryBean). If you decide to resurrect this
// GraphicsMagickImageTransformationFactoryBean, you need to revisit performance of runtime scaling.
// eg. 1) make sure images aren't regenerated if they already exist
//     2) (ideally) prevent concurrent, identical invocations of graphics magick.
// NOTE, however, that the above two measures may not actually be required, since there are a
// maximum number of server threads available and, hence, a cap on the maximum number of image
// transformations that can be occurring simultaneously.
public class GraphicsMagickImageTransformationFactoryBean
    extends AbstractImageTransformationFactoryBean {

    private static final Logger LOGGER =
            Logger.getLogger(GraphicsMagickImageTransformationFactoryBean.class);

    /**
     * Millisecond interval to poll whether the launched command line process has finished.
     */
    public static final int PROCESS_POLLING_INTERVAL_MILLISECONDS = 100;

    private final ProcessStarter processStarter;
    private final String graphicsMagickExecutable;

    /**
     * Default value for {@link #getProcessTimeoutMilliseconds()}.
     */
    public static final int DEFAULT_PROCESS_TIMEOUT_MILLISECONDS = 60000;

    /**
     * Milliseconds to wait for the launched process to terminate. Defaults to
     * {@link #DEFAULT_PROCESS_TIMEOUT_MILLISECONDS}.
     */
    private int processTimeoutMilliseconds = DEFAULT_PROCESS_TIMEOUT_MILLISECONDS;

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

        super(imageReader);
        this.processStarter = processStarter;
        this.graphicsMagickExecutable = graphicsMagickExecutable;
    }

    /**
     * @return Milliseconds to wait for the launched process to terminate.
     *         Defaults to {@link #DEFAULT_PROCESS_TIMEOUT_MILLISECONDS}.
     */
    public int getProcessTimeoutMilliseconds() {
        return processTimeoutMilliseconds;
    }

    /**
     * @param processTimeoutMilliseconds
     *            Milliseconds to wait for the launched process to terminate.
     *            Defaults to {@link #DEFAULT_PROCESS_TIMEOUT_MILLISECONDS}.
     */
    public void setProcessTimeoutMilliseconds(final int processTimeoutMilliseconds) {
        this.processTimeoutMilliseconds = processTimeoutMilliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TransformedImageAttributes doTransformImage(final File sourceImageFile,
            final ImageAttributes sourceImageAttributes,
            final File baseTargetImageDir,
            final ImageTransformationParameters imageTransformationParameters) throws IOException {

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

        final int processExitValue = waitForProcess(process, commandLine);
        validateProcessExitedSuccessfully(processExitValue, commandLine);

        return createTransformedImageAttributes(sourceImageAttributes, outputImageFile,
                outputImageWidth, outputImageHeight);
    }

    private void validateProcessExitedSuccessfully(final int processExitValue,
            final List<String> commandLine) {
        if (processExitValue != 0) {
            throw new ImageCreationException("Error when creating scaled image using command: "
                    + commandLine + ". Process exit code: " + processExitValue);
        }
    }

    private int waitForProcess(final Process process, final List<String> commandLine) {
        final long startTime = System.currentTimeMillis();
        while (!hasProcessExited(process)) {

            if (processHasExceededTimeout(startTime)) {
                throw new ImageCreationException("Error when creating scaled image using command: "
                        + commandLine + ". Process took too long to exit.");
            }

            sleepUntilNextPollingTime();
        }
        return process.exitValue();

    }

    private boolean processHasExceededTimeout(final long startTime) {
        return System.currentTimeMillis() - startTime > getProcessTimeoutMilliseconds();
    }

    private void sleepUntilNextPollingTime() {
        try {
            Thread.sleep(PROCESS_POLLING_INTERVAL_MILLISECONDS);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(
                    "We were interrupted when waiting for the image scaling process to exit. "
                            + "This really, really shouldn't happen.", e);
        }
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
                createOutputImageFilename(imageTransformationParameters, sourceImage);

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

            addResizeCommandLineOptions(scaledImageWidth, commandLine);
        }

        if (ImageFormat.GIF.equals(imageTransformationParameters.getOutputImageFormat())) {
            addGifConversionCommandLineOptions(imageTransformationParameters, commandLine);
        }

        commandLine.add(sourceImageFile.getPath());
        commandLine.add(outputImageFile.getPath());
        return commandLine;
    }

    private void addGifConversionCommandLineOptions(
            final ImageTransformationParameters imageTransformationParameters,
            final List<String> commandLine) {
        if (imageTransformationParameters.getBackgroundColor() != null) {
            commandLine.add("-background");
            commandLine.add(imageTransformationParameters.getBackgroundColor());
        }
        commandLine.add("-extent");
        commandLine.add("0x0");

        // Probably don't need this option but use it just in case.
        commandLine.add("+matte");
    }

    private void addResizeCommandLineOptions(final int scaledImageWidth,
            final List<String> commandLine) {
        commandLine.add("-resize");
        commandLine.add(scaledImageWidth + "x");
        commandLine.add("-unsharp");
        commandLine.add("0x1");
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

package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import au.com.sensis.mobile.crf.util.ImageTransformationFactory.ImageFormat;
import au.com.sensis.mobile.crf.util.ImageTransformationFactory.ImageTransformationParameters;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link GraphicsMagickImageTransformationFactoryBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GraphicsMagickImageTransformationFactoryBeanTestCase extends AbstractJUnit4TestCase {

    private static final int DEVICE_PIXEL_WIDTH = 500;
    private static final int DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH = 100;
    private static final int DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT = 75;

    private static final int ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH = 200;
    private static final int ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT = 150;

    private static final int SOURCE_IMAGE_PIXEL_WIDTH = 800;
    private static final int SOURCE_IMAGE_PIXEL_HEIGHT = 600;

    private GraphicsMagickImageTransformationFactoryBean objectUnderTest;

    private File sourceImage;
    private File outputImageBaseDir;

    private List<String> devicePercentWidthScalingCommandLine;
    private String devicePercentWidthScalingOutputImageSubDir;
    private File devicePercentWidthScalingOutputImage;

    private List<String> absolutePixelWidthScalingCommandLine;
    private String absolutePixelWidthScalingOutputImageSubDir;
    private File absolutePixelWidthScalingOutputImage;

    private List<String> noScalingCommandLine;
    private String noScalingOutputImageSubDir;
    private File noScalingOutputImage;

    private FileIoFacade mockFileIoFacade;
    private ImageReader mockImageReader;
    private ProcessStarter mockProcessStarter;
    private Process mockProcess;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new GraphicsMagickImageTransformationFactoryBean(
                getMockProcessStarter(), getMockImageReader(), "gm"));

        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        final ClassPathResource myPackageDirClassPathResource =
                new ClassPathResource("/"
                        + getClass().getPackage().getName().replaceAll("\\.", "/"));
        final File myPackageDir = new File(myPackageDirClassPathResource.getURI());

        setupSourceImage(myPackageDir);

        setOutputImageBaseDir(myPackageDir);

        setDevicePercentWidthScalingOutputImageSubDir("w"
                + DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH + "/h"
                + DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT);

        setDevicePercentWidthScalingOutputImage(new File(getOutputImageBaseDir(),
                getDevicePercentWidthScalingOutputImageSubDir() + "/myInputImage.gif"));

        setDevicePercentWidthScalingCommandLine(Arrays.asList(new String[] { "gm", "convert",
                "-resize", "100x", "-unsharp", "0x1", getSourceImage().getPath(),
                getDevicePercentWidthScalingOutputImage().getPath() }));

        setAbsolutePixelWidthScalingOutputImageSubDir("w"
                + ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH + "/h"
                + ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT);

        setAbsolutePixelWidthScalingOutputImage(new File(getOutputImageBaseDir(),
                getAbsolutePixelWidthScalingOutputImageSubDir() + "/myInputImage.gif"));

        setAbsolutePixelWidthScalingCommandLine(Arrays.asList(new String[] { "gm", "convert",
                "-resize", "200x", "-unsharp", "0x1", getSourceImage().getPath(),
                getAbsolutePixelWidthScalingOutputImage().getPath() }));

        setNoScalingOutputImageSubDir("w" + SOURCE_IMAGE_PIXEL_WIDTH + "/h"
                + SOURCE_IMAGE_PIXEL_HEIGHT);

        setNoScalingOutputImage(new File(getOutputImageBaseDir(), getNoScalingOutputImageSubDir()
                + "/myInputImage.gif"));

        setNoScalingCommandLine(Arrays.asList(new String[] { "gm", "convert",
                getSourceImage().getPath(), getNoScalingOutputImage().getPath() }));
    }

    private void setupSourceImage(final File myPackageDir) throws IOException {
        setSourceImage(new File(myPackageDir, "myInputImage.png"));
        final boolean sourceImageCreated = getSourceImage().createNewFile();
        if (!sourceImageCreated) {
            throw new RuntimeException("Could not create file to setup test: " + getSourceImage());
        }
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();

        getSourceImage().delete();
    }

    @Test
    public void testTransformImageWhenDevicePercentWidthScalingRequested() throws Throwable {

        recordReadSourceImageAttributes();

        recordMakeOutputImageDirs(getDevicePercentWidthScalingOutputImageSubDir());

        recordStartProcess(getDevicePercentWidthScalingCommandLine());

        EasyMock.expect(getMockProcess().exitValue()).andThrow(
                new IllegalThreadStateException("test"));

        recordProcessExit(0);

        replay();

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        createPercentWidthImageTransformationParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getDevicePercentWidthScalingOutputImage(),
                DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH,
                DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT), actualImage);
    }

    @Test
    public void testTransformImageWhenAbsolutePixelWidthScalingRequested() throws Throwable {

        recordReadSourceImageAttributes();

        recordMakeOutputImageDirs(getAbsolutePixelWidthScalingOutputImageSubDir());

        recordStartProcess(getAbsolutePixelWidthScalingCommandLine());

        EasyMock.expect(getMockProcess().exitValue()).andThrow(
                new IllegalThreadStateException("test"));

        recordProcessExit(0);

        replay();

        final ImageTransformationParameters imageTransformationParameters =
                createAbsolutePixelWidthImageTransformationParameters(
                        ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH);

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        imageTransformationParameters);

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getAbsolutePixelWidthScalingOutputImage(),
                ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH,
                ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT), actualImage);
    }

    @Test
    public void testTransformImageWhenDImensionsPreservingTransformRequested() throws Throwable {

        recordReadSourceImageAttributes();

        recordMakeOutputImageDirs(getNoScalingOutputImageSubDir());

        recordStartProcess(getNoScalingCommandLine());

        EasyMock.expect(getMockProcess().exitValue()).andThrow(
                new IllegalThreadStateException("test"));

        recordProcessExit(0);

        replay();

        final TransformedImageAttributes actualImage =
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createNoScalingImageTransformationParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getNoScalingOutputImage(), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT),
                actualImage);
    }

    @Test
    public void testTransformImageWhenNoTransformRequired() throws Throwable {

        recordReadSourceImageAttributes();

        replay();

        final TransformedImageAttributes actualImage =
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createNoImageTransformationRequiredParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getSourceImage(), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT),
                actualImage);
    }

    private void recordProcessExit(final int exitCode) {
        EasyMock.expect(getMockProcess().exitValue()).andReturn(new Integer(exitCode))
                .atLeastOnce();
    }

    private void recordStartProcess(final List<String> commandLine) throws IOException {
        EasyMock.expect(getMockProcessStarter().start(commandLine))
                .andReturn(getMockProcess());
    }

    private void recordMakeOutputImageDirs(final String subdir) throws IOException {
        final File outputImageDir = new File(getOutputImageBaseDir(), subdir);
        getMockFileIoFacade().mkdirs(outputImageDir);
    }

    private void recordReadSourceImageAttributes() {
        EasyMock.expect(getMockImageReader().readImageAttributes(getSourceImage())).andReturn(
                createSourceImageAttributes());
    }

    private ImageTransformationParameters createPercentWidthImageTransformationParameters() {
        final ImageTransformationParametersBean parametersBean =
                new ImageTransformationParametersBean();

        parametersBean.setDeviceImagePercentWidth(20);
        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.GIF);
        return parametersBean;
    }

    private ImageTransformationParameters createAbsolutePixelWidthImageTransformationParameters(
            final int pixelWidth) {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setAbsolutePixelWidth(pixelWidth);
        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.GIF);
        return parametersBean;
    }

    private ImageTransformationParameters createNoScalingImageTransformationParameters() {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.GIF);
        return parametersBean;
    }

    private ImageTransformationParameters createNoImageTransformationRequiredParameters() {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.PNG);
        return parametersBean;
    }

    private ImageAttributes createSourceImageAttributes() {
        final ImageAttributesBean imageAttributesBean = new ImageAttributesBean();

        imageAttributesBean.setImageFile(getSourceImage());
        imageAttributesBean.setPixelWidth(SOURCE_IMAGE_PIXEL_WIDTH);
        imageAttributesBean.setPixelHeight(SOURCE_IMAGE_PIXEL_HEIGHT);

        return imageAttributesBean;
    }

    private TransformedImageAttributes createExpectedTransformedImageAttributes(
            final File outputImage, final int pixelWidth, final int pixelHeight) {
        final TransformedImageAttributesBean imageBean = new TransformedImageAttributesBean();

        final ImageAttributesBean imageAttributesBean = new ImageAttributesBean();
        imageAttributesBean.setImageFile(outputImage);
        imageAttributesBean.setPixelWidth(pixelWidth);
        imageAttributesBean.setPixelHeight(pixelHeight);

        imageBean.setSourceImageAttributes(createSourceImageAttributes());
        imageBean.setOutputImageAttributes(imageAttributesBean);

        return imageBean;
    }

    @Test
    public void testScaleImageWhenProcessExitCodeUnsuccessful() throws Throwable {

        recordReadSourceImageAttributes();

        recordMakeOutputImageDirs(getDevicePercentWidthScalingOutputImageSubDir());

        recordStartProcess(getDevicePercentWidthScalingCommandLine());

        EasyMock.expect(getMockProcess().exitValue()).andThrow(
                new IllegalThreadStateException("test"));

        recordProcessExit(1);

        replay();

        try {
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createPercentWidthImageTransformationParameters());

            Assert.fail("ImageCreationException expected");
        } catch (final ImageCreationException e) {

            Assert.assertEquals("ImageCreationException has wrong message",
                    "Error when creating scaled image using command: "
                            + getDevicePercentWidthScalingCommandLine() + ". Process exit code: 1",
                    e.getMessage());
        }
    }

    @Test
    public void testSourceImageFileNotReadable() throws Throwable {
        final File[] unreadableSourceImageFiles = new File[] { null, new File("i cannot be read") };
        for (final File unreadableFile : unreadableSourceImageFiles) {
            try {
                getObjectUnderTest().transformImage(unreadableFile, getOutputImageBaseDir(),
                        createPercentWidthImageTransformationParameters());

                Assert.fail("ImageCreationException expected for file: '" + unreadableFile + "'");

            } catch (final ImageCreationException e) {

                Assert.assertEquals("ImageCreationException has wrong message",
                        "Cannot read source image file: '" + unreadableFile + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testBaseOutputImageDirInvalid() throws Throwable {
        final File[] invalidDirs = new File[] { null, new File("i am not a dir") };
        for (final File invalidDir : invalidDirs) {
            try {
                getObjectUnderTest().transformImage(getSourceImage(), invalidDir,
                        createPercentWidthImageTransformationParameters());

                Assert.fail("ImageCreationException expected for file: '" + invalidDir + "'");

            } catch (final ImageCreationException e) {

                Assert.assertEquals("ImageCreationException has wrong message",
                        "Base output image dir is not a directory: '" + invalidDir + "'", e
                                .getMessage());
            }
        }

    }

    /**
     * @return the objectUnderTest
     */
    private GraphicsMagickImageTransformationFactoryBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest
     *            the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final GraphicsMagickImageTransformationFactoryBean objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the commandLine
     */
    private List<String> getDevicePercentWidthScalingCommandLine() {
        return devicePercentWidthScalingCommandLine;
    }

    /**
     * @param commandLine the commandLine to set
     */
    private void setDevicePercentWidthScalingCommandLine(final List<String> commandLine) {
        devicePercentWidthScalingCommandLine = commandLine;
    }

    /**
     * @return the outputImageBaseDir
     */
    private File getOutputImageBaseDir() {
        return outputImageBaseDir;
    }

    /**
     * @param outputImageBaseDir the outputImageBaseDir to set
     */
    private void setOutputImageBaseDir(final File outputImageBaseDir) {
        this.outputImageBaseDir = outputImageBaseDir;
    }

    /**
     * @return the sourceImage
     */
    private File getSourceImage() {
        return sourceImage;
    }

    /**
     * @param sourceImage the sourceImage to set
     */
    private void setSourceImage(final File sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * @return the mockFileIoFacade
     */
    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    /**
     * @param mockFileIoFacade the mockFileIoFacade to set
     */
    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }

    /**
     * @return the mockImageReader
     */
    public ImageReader getMockImageReader() {
        return mockImageReader;
    }

    /**
     * @param mockImageReader the mockImageReader to set
     */
    public void setMockImageReader(final ImageReader mockImageReader) {
        this.mockImageReader = mockImageReader;
    }

    /**
     * @return the mockProcessStarter
     */
    public ProcessStarter getMockProcessStarter() {
        return mockProcessStarter;
    }

    /**
     * @param mockProcessStarter the mockProcessStarter to set
     */
    public void setMockProcessStarter(final ProcessStarter mockProcessStarter) {
        this.mockProcessStarter = mockProcessStarter;
    }

    /**
     * @return the mockProcess
     */
    public Process getMockProcess() {
        return mockProcess;
    }

    /**
     * @param mockProcess the mockProcess to set
     */
    public void setMockProcess(final Process mockProcess) {
        this.mockProcess = mockProcess;
    }

    /**
     * @return the absolutePixelWidthScalingCommandLine
     */
    private List<String> getAbsolutePixelWidthScalingCommandLine() {
        return absolutePixelWidthScalingCommandLine;
    }

    /**
     * @param absolutePixelWidthScalingCommandLine the absolutePixelWidthScalingCommandLine to set
     */
    private void setAbsolutePixelWidthScalingCommandLine(
            final List<String> absolutePixelWidthScalingCommandLine) {
        this.absolutePixelWidthScalingCommandLine = absolutePixelWidthScalingCommandLine;
    }

    /**
     * @return the noScalingCommandLine
     */
    private List<String> getNoScalingCommandLine() {
        return noScalingCommandLine;
    }

    /**
     * @param noScalingCommandLine the noScalingCommandLine to set
     */
    private void setNoScalingCommandLine(final List<String> noScalingCommandLine) {
        this.noScalingCommandLine = noScalingCommandLine;
    }


    /**
     * @return the devicePercentWidthScalingOutputImageSubDir
     */
    private String getDevicePercentWidthScalingOutputImageSubDir() {
        return devicePercentWidthScalingOutputImageSubDir;
    }

    /**
     * @param devicePercentWidthScalingOutputImageSubDir
     *            the devicePercentWidthScalingOutputImageSubDir to set
     */
    private void setDevicePercentWidthScalingOutputImageSubDir(
            final String devicePercentWidthScalingOutputImageSubDir) {
        this.devicePercentWidthScalingOutputImageSubDir =
                devicePercentWidthScalingOutputImageSubDir;
    }

    /**
     * @return the devicePercentWidthScalingOutputImage
     */
    private File getDevicePercentWidthScalingOutputImage() {
        return devicePercentWidthScalingOutputImage;
    }

    /**
     * @param devicePercentWidthScalingOutputImage the devicePercentWidthScalingOutputImage to set
     */
    private void setDevicePercentWidthScalingOutputImage(
            final File devicePercentWidthScalingOutputImage) {
        this.devicePercentWidthScalingOutputImage = devicePercentWidthScalingOutputImage;
    }

    /**
     * @return the absolutePixelWidthScalingOutputImageSubDir
     */
    private String getAbsolutePixelWidthScalingOutputImageSubDir() {
        return absolutePixelWidthScalingOutputImageSubDir;
    }

    /**
     * @param absolutePixelWidthScalingOutputImageSubDir
     *            the absolutePixelWidthScalingOutputImageSubDir to set
     */
    private void setAbsolutePixelWidthScalingOutputImageSubDir(
            final String absolutePixelWidthScalingOutputImageSubDir) {
        this.absolutePixelWidthScalingOutputImageSubDir =
                absolutePixelWidthScalingOutputImageSubDir;
    }

    /**
     * @return the absolutePixelWidthScalingOutputImage
     */
    private File getAbsolutePixelWidthScalingOutputImage() {
        return absolutePixelWidthScalingOutputImage;
    }

    /**
     * @param absolutePixelWidthScalingOutputImage the absolutePixelWidthScalingOutputImage to set
     */
    private void setAbsolutePixelWidthScalingOutputImage(
            final File absolutePixelWidthScalingOutputImage) {
        this.absolutePixelWidthScalingOutputImage = absolutePixelWidthScalingOutputImage;
    }

    /**
     * @return the noScalingOutputImageSubDir
     */
    private String getNoScalingOutputImageSubDir() {
        return noScalingOutputImageSubDir;
    }

    /**
     * @param noScalingOutputImageSubDir the noScalingOutputImageSubDir to set
     */
    private void setNoScalingOutputImageSubDir(final String noScalingOutputImageSubDir) {
        this.noScalingOutputImageSubDir = noScalingOutputImageSubDir;
    }

    /**
     * @return the noScalingOutputImage
     */
    private File getNoScalingOutputImage() {
        return noScalingOutputImage;
    }

    /**
     * @param noScalingOutputImage the noScalingOutputImage to set
     */
    private void setNoScalingOutputImage(final File noScalingOutputImage) {
        this.noScalingOutputImage = noScalingOutputImage;
    }
}

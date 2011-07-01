package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Level;
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
 * Unit test {@link PregeneratedFileLookupImageTransformationFactoryBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PregeneratedFileLookupImageTransformationFactoryBeanTestCase extends AbstractJUnit4TestCase {

    private static final int DEVICE_PIXEL_WIDTH = 468;
    private static final int SMALL_DEVICE_PIXEL_WIDTH = 210;
    private static final int VERY_LARGE_DEVICE_PIXEL_WIDTH = 4200;

    private static final int REQUESTED_DEVICE_PERCENT_SCALED_WIDTH = 20;
    private static final int DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH = 92;
    private static final int DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT = 52;

    private static final int REQUESTED_ABSOLUTE_SCALED_WIDTH = 100;
    private static final int ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH = 98;
    private static final int ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT = 55;

    private static final int SOURCE_IMAGE_PIXEL_WIDTH = 103;
    private static final int SOURCE_IMAGE_PIXEL_HEIGHT = 77;

    private static final int SMALLEST_SCALED_IMAGE_WIDTH = 82;
    private static final int SMALLEST_SCALED_IMAGE_HEIGHT = 46;

    private PregeneratedFileLookupImageTransformationFactoryBean objectUnderTest;

    private File sourceImage;
    private File outputImageBaseDir;

    private File devicePercentWidthScalingOutputImageDir;
    private File devicePercentWidthScalingOutputImage;

    private File absolutePixelWidthScalingOutputImageDir;
    private File absolutePixelWidthScalingOutputImage;

    private File noScalingOutputImageWidthDir;
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
        setObjectUnderTest(new PregeneratedFileLookupImageTransformationFactoryBean(
                getMockImageReader()));

        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setupSourceImage(getPackageDirOfCurrentClass());

        setOutputImageBaseDir(getPackageDirOfCurrentClass());

        setDevicePercentWidthScalingOutputImageDir(
                new File(getOutputImageBaseDir(),
                        "w" + DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH));

        setDevicePercentWidthScalingOutputImage(
                new File(getDevicePercentWidthScalingOutputImageDir(),
                    "/h" + DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT + "/myInputImage.png"));

        setAbsolutePixelWidthScalingOutputImageDir(new File(getOutputImageBaseDir(),
                "w"+ ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH));
        setAbsolutePixelWidthScalingOutputImage(
                new File(getAbsolutePixelWidthScalingOutputImageDir(),
                        "/h" + ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT + "/myInputImage.png"));


        setNoScalingOutputImageWidthDir(new File(getOutputImageBaseDir(),
                "w" + SOURCE_IMAGE_PIXEL_WIDTH));

        setNoScalingOutputImage(new File(getNoScalingOutputImageWidthDir(),
                "/h" + SOURCE_IMAGE_PIXEL_HEIGHT + "/myInputImage.gif"));

    }

    private File getPackageDirOfCurrentClass() throws IOException {
        final ClassPathResource myPackageDirClassPathResource =
                new ClassPathResource("/"
                        + getClass().getPackage().getName().replaceAll("\\.", "/"));
        final File myPackageDir = new File(myPackageDirClassPathResource.getURI());
        return myPackageDir;
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

        recordListScaledImageWidthDirs();

        final File[] foundFiles = { getDevicePercentWidthScalingOutputImage() };
        recordListScaledImagesUnderWidthDir(getDevicePercentWidthScalingOutputImageDir(),
                getSourceImage().getName(), foundFiles);

        replay();

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        createPercentWidthImageTransformationParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getDevicePercentWidthScalingOutputImage(),
                DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_WIDTH,
                DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT), actualImage);
    }

    private File[] createScaledImagesWidthDirList() {
        return new File [] {
                new File("/uiresources/images/default/furniture/w103"),
                new File("/uiresources/images/default/furniture/w98"),
                new File("/uiresources/images/default/furniture/w92"),
                new File("/uiresources/images/default/furniture/w87"),
                new File("/uiresources/images/default/furniture/w"
                        + SMALLEST_SCALED_IMAGE_WIDTH) };
    }

    @Test
    public void testTransformImageWhenHeightDirectoryValueInvalid() throws Throwable {
        setDevicePercentWidthScalingOutputImage(new File(getDevicePercentWidthScalingOutputImageDir(),
                "/h100trailing text/myInputImage.png"));

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File[] foundFiles = { getDevicePercentWidthScalingOutputImage() };
        recordListScaledImagesUnderWidthDir(getDevicePercentWidthScalingOutputImageDir(),
                        getSourceImage().getName(), foundFiles);

        replay();

        try {
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createPercentWidthImageTransformationParameters());

            Assert.fail("ImageCreationException expected");
        } catch (final ImageCreationException e) {

            Assert.assertEquals("ImageCreationException has wrong message", "The found image '"
                    + getDevicePercentWidthScalingOutputImage()
                    + "' has an invalid height specified in its path.", e.getMessage());
        }

    }

    @Test
    public void testTransformImageWhenAbsolutePixelWidthScalingRequested() throws Throwable {

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File[] foundFiles = { getAbsolutePixelWidthScalingOutputImage() };
        recordListScaledImagesUnderWidthDir(getAbsolutePixelWidthScalingOutputImageDir(),
                getSourceImage().getName(), foundFiles);

        replay();

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        createAbsolutePixelWidthImageTransformationParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getAbsolutePixelWidthScalingOutputImage(),
                ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_WIDTH,
                ABSOLUTE_WIDTH_SCALING_OUTPUT_PIXEL_HEIGHT), actualImage);
    }

    @Test
    public void testTransformImageWhenDimensionsPreservingTransformRequested() throws Throwable {

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File[] foundFiles = { getNoScalingOutputImage() };
        recordListScaledImagesUnderWidthDir(getNoScalingOutputImageWidthDir(),
                getNoScalingOutputImage().getName(), foundFiles);

        replay();

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        createNoScalingImageTransformationParameters());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getNoScalingOutputImage(), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT),
                actualImage);
    }

    private void recordListScaledImagesUnderWidthDir(final File imageWidthDir, final String imageName,
            final File [] foundFiles) {

        EasyMock.expect(
                getMockFileIoFacade().list(imageWidthDir, imageName,"h*"))
                    .andReturn(foundFiles);

    }

    private void recordListScaledImageWidthDirs() {
        recordListScaledImageWidthDirs(createScaledImagesWidthDirList());
    }

    private void recordListScaledImageWidthDirs(final File [] dirs) {
        final String[] widthDirWildcardPattern = new String [] { "w*" };

        EasyMock.expect(getMockFileIoFacade().list(
                EasyMock.eq(getOutputImageBaseDir()),
                EasyMock.aryEq(widthDirWildcardPattern))
        ).andReturn(dirs);
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

    @Test
    public void testTransformImageWhenScalingUpLimitedToOriginalSize() throws Throwable {

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File outputImageDir = getNoScalingOutputImageWidthDir();

        final File[] foundFiles = { getNoScalingOutputImage() };
        recordListScaledImagesUnderWidthDir(outputImageDir, getSourceImage().getName(), foundFiles);

        replay();

        final TransformedImageAttributes actualImage =
                getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                        createImageTransformationParametersWhenCalculatedWidthExceedsSource());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                getNoScalingOutputImage(),
                SOURCE_IMAGE_PIXEL_WIDTH,
                SOURCE_IMAGE_PIXEL_HEIGHT), actualImage);
    }

    @Test
    public void testTransformImageWhenRequestedImageWidthLessThanSmallestScaledImage() throws Throwable {

        final File expectedScaledImageWidthDir =
            new File(getOutputImageBaseDir(), "w" + SMALLEST_SCALED_IMAGE_WIDTH);

        final File expectedScaledImage = new File(expectedScaledImageWidthDir,
                "/h" + SMALLEST_SCALED_IMAGE_HEIGHT + "/myInputImage.gif");

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File[] foundFiles = { expectedScaledImage };

        recordListScaledImagesUnderWidthDir(expectedScaledImageWidthDir, getSourceImage().getName(),
                foundFiles);

        replay();

        final TransformedImageAttributes actualImage =
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createImageTransformationParametersWhenCalculatedWidthLessThanSmallestScaledImage());

        Assert.assertEquals("actualImage is wrong", createExpectedTransformedImageAttributes(
                expectedScaledImage, SMALLEST_SCALED_IMAGE_WIDTH, SMALLEST_SCALED_IMAGE_HEIGHT),
                    actualImage);
    }

    @Test
    public void testTransformImageWhenMultiplePregeneratedImagesFound() throws Throwable {

        swapOutRealLoggerForMock(PregeneratedFileLookupImageTransformationFactoryBean.class);

        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final List<File> foundFilesList = new ArrayList<File>();
        foundFilesList.add(getDevicePercentWidthScalingOutputImage());
        foundFilesList.add(new File(getDevicePercentWidthScalingOutputImageDir(),
                "/h"+ (DEVICE_PERCENT_SCALING_OUTPUT_IMAGE_PIXEL_HEIGHT + 1) + "/myInputImage.png"));
        final File[] foundFiles = foundFilesList.toArray(new File [] {});

        recordListScaledImagesUnderWidthDir(getDevicePercentWidthScalingOutputImageDir(),
                getSourceImage().getName(), foundFiles);

        EasyMock.expect(
                getMockLogger(PregeneratedFileLookupImageTransformationFactoryBean.class)
                        .isEnabledFor(Level.WARN)).andReturn(Boolean.TRUE);

        getMockLogger(PregeneratedFileLookupImageTransformationFactoryBean.class).warn(
                "Multiple images found with the same width: " + ArrayUtils.toString(foundFiles)
                        + ". Returning the first one. ");

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
    public void testTransformImageWhenPregeneratedImageWidthDirectoriesNotFound() throws Throwable {
        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs(new File [] {});

        replay();

        try {
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createPercentWidthImageTransformationParameters());

            Assert.fail("ImageCreationException expected");
        } catch (final ImageCreationException e) {

            Assert.assertEquals("ImageCreationException has wrong message",
                    "No pregenerated image directories found under " + getOutputImageBaseDir(),
                        e.getMessage());
        }
    }

    @Test
    public void testTransformImageWhenPregeneratedImageNotFound() throws Throwable {
        recordReadSourceImageAttributes();

        recordListScaledImageWidthDirs();

        final File[] foundFiles = new File[] {};
        recordListScaledImagesUnderWidthDir(getDevicePercentWidthScalingOutputImageDir(),
                getSourceImage().getName(), foundFiles);

        replay();

        try {
            getObjectUnderTest().transformImage(getSourceImage(), getOutputImageBaseDir(),
                    createPercentWidthImageTransformationParameters());

            Assert.fail("ImageCreationException expected");
        } catch (final ImageCreationException e) {

            Assert.assertEquals("ImageCreationException has wrong message",
                    getSourceImage().getName() + " could not be found under "
                        + getDevicePercentWidthScalingOutputImageDir(), e.getMessage());
        }
    }

    private void recordReadSourceImageAttributes() {
        EasyMock.expect(getMockImageReader().readImageAttributes(getSourceImage())).andReturn(
                createSourceImageAttributes());
    }

    private ImageTransformationParameters createPercentWidthImageTransformationParameters() {
        final ImageTransformationParametersBean parametersBean =
                new ImageTransformationParametersBean();

        parametersBean.setDeviceImagePercentWidth(REQUESTED_DEVICE_PERCENT_SCALED_WIDTH);
        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.PNG);
        return parametersBean;
    }

    private ImageTransformationParameters
        createImageTransformationParametersWhenCalculatedWidthExceedsSource() {

        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDeviceImagePercentWidth(REQUESTED_DEVICE_PERCENT_SCALED_WIDTH);
        parametersBean.setDevicePixelWidth(VERY_LARGE_DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.PNG);
        return parametersBean;
    }

    private ImageTransformationParameters
        createImageTransformationParametersWhenCalculatedWidthLessThanSmallestScaledImage() {

        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDeviceImagePercentWidth(REQUESTED_DEVICE_PERCENT_SCALED_WIDTH);
        parametersBean.setDevicePixelWidth(SMALL_DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.PNG);
        return parametersBean;
    }

    private ImageTransformationParameters createAbsolutePixelWidthImageTransformationParameters() {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setAbsolutePixelWidth(REQUESTED_ABSOLUTE_SCALED_WIDTH);
        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.PNG);
        return parametersBean;
    }

    private ImageTransformationParameters createNoScalingImageTransformationParameters() {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDevicePixelWidth(DEVICE_PIXEL_WIDTH);
        parametersBean.setOutputImageFormat(ImageFormat.GIF);
        parametersBean.setBackgroundColor("#FFF");
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
    private PregeneratedFileLookupImageTransformationFactoryBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest
     *            the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final PregeneratedFileLookupImageTransformationFactoryBean objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
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

    private File getNoScalingOutputImageWidthDir() {
        return noScalingOutputImageWidthDir;
    }

    private void setNoScalingOutputImageWidthDir(final File noScalingOutputImageWidthDir) {
        this.noScalingOutputImageWidthDir = noScalingOutputImageWidthDir;
    }

    private File getDevicePercentWidthScalingOutputImageDir() {
        return devicePercentWidthScalingOutputImageDir;
    }

    private void setDevicePercentWidthScalingOutputImageDir(final File devicePercentWidthScalingOutputImageDir) {
        this.devicePercentWidthScalingOutputImageDir = devicePercentWidthScalingOutputImageDir;
    }

    private File getAbsolutePixelWidthScalingOutputImageDir() {
        return absolutePixelWidthScalingOutputImageDir;
    }

    private void setAbsolutePixelWidthScalingOutputImageDir(final File absolutePixelWidthScalingOutputImageDir) {
        this.absolutePixelWidthScalingOutputImageDir = absolutePixelWidthScalingOutputImageDir;
    }

}

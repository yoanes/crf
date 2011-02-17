package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.ImageAttributesBean;
import au.com.sensis.mobile.crf.util.ImageTransformationFactory;
import au.com.sensis.mobile.crf.util.ImageTransformationParametersBean;
import au.com.sensis.mobile.crf.util.TransformedImageAttributes;
import au.com.sensis.mobile.crf.util.TransformedImageAttributesBean;
import au.com.sensis.mobile.crf.util.ImageTransformationFactory.ImageTransformationParameters;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Unit test {@link TransformedImageResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class TransformedImageResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private static final int OUTPUT_IMAGE_PIXEL_HEIGHT = 60;
    private static final int OUTPUT_IMAGE_PIXEL_WIDTH = 90;
    private static final String[] MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS = new String[] { "*" };
    private static final String[] EXCLUDED_IMAGE_FILE_EXTENSION_WILDCARDS
        = new String[] { "properties" };

    private static final int SOURCE_IMAGE_PIXEL_WIDTH = 800;
    private static final int SOURCE_IMAGE_PIXEL_HEIGHT = 600;

    private Device mockDevice;
    private TransformedImageResourceResolverBean objectUnderTest;
    private au.com.sensis.mobile.crf.util.PropertiesLoader mockPropertiesLoader;
    private ImageTransformationFactory mockImageTransformationFactory;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new TransformedImageResourceResolverBean(
                getResourceResolverCommonParamHolder(), getResourcePathTestData()
                        .getAbstractImageExtensionWithLeadingDot(), getResourcesRootDir(),
                MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS));

        getObjectUnderTest().setPropertiesLoader(getMockPropertiesLoader());
        getObjectUnderTest().setImageTransformationFactory(getMockImageTransformationFactory());

    }

    @Override
    protected TransformedImageResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        final TransformedImageResourceResolverBean resolverBean =
                new TransformedImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                        abstractResourceExtension, getResourcesRootDir(),
                        MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS);

        resolverBean.setPropertiesLoader(getMockPropertiesLoader());
        resolverBean.setImageTransformationFactory(getMockImageTransformationFactory());

        return resolverBean;
    }

    @Override
    protected TransformedImageResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new TransformedImageResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected TransformedImageResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new TransformedImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected TransformedImageResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new TransformedImageResourceResolverBean(commonParams,
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS);
    }

    @Test
    public void testConstructorWhenFileExtensionWildcardsIsInvalid()
    throws Throwable {

        final List<String []> testVaues = Arrays.asList(
                null,
                new String [] {},
                new String [] { null },
                new String [] { StringUtils.EMPTY },
                new String [] { " " },
                new String [] { "  " }
        );

        for (final String [] testValue : testVaues) {
            try {
                new TransformedImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                        getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        getResourcesRootDir(), testValue);

                Assert.fail("IllegalArgumentException expected for testValue: '"
                        + ArrayUtils.toString(testValue) + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals("IllegalArgumentException has wrong message",
                        "fileExtensionWildcards must be an array of non-blank Strings but was: '"
                        + ArrayUtils.toString(testValue) + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndSingleResourceFound() throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                        getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadIphoneGroupImageProperties(createIphoneGroupPercentWidthImageProperties());
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordGetDeviceAttributesForImageScaling();

            recordGetImageRatioDeviceProperty(null);
            recordTransformImage(createPercentWidthImageTransformationParameters(
                    1), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    OUTPUT_IMAGE_PIXEL_WIDTH, OUTPUT_IMAGE_PIXEL_HEIGHT);

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedImageResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                    Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndSingleResourceFoundAndScaledUp()
        throws Throwable {

        final String[] testValues =
        { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadIphoneGroupImageProperties(createIphoneGroupPercentWidthImageProperties());
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordGetDeviceAttributesForImageScaling();

            recordGetImageRatioDeviceProperty(null);
            recordTransformImage(createPercentWidthImageTransformationParameters(
                    1), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    SOURCE_IMAGE_PIXEL_WIDTH * 2, SOURCE_IMAGE_PIXEL_HEIGHT * 2);

            recordWarnScaledImageUp(SOURCE_IMAGE_PIXEL_WIDTH * 2, SOURCE_IMAGE_PIXEL_HEIGHT * 2);

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordWarnScaledImageUp(final int outputPixelWidthGreaterThanSourceWidth,
            final int outputPixelWidthGreaterThanSourceHeight) {

        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);

        final TransformedImageAttributes transformedImageAttributes =
                createTransformedImageAttributes(SOURCE_IMAGE_PIXEL_WIDTH,
                        SOURCE_IMAGE_PIXEL_HEIGHT, outputPixelWidthGreaterThanSourceWidth,
                        outputPixelWidthGreaterThanSourceHeight);

        getMockResourceResolutionWarnLogger().warn(
                "Scaled image up for device " + getMockDevice() + ". This may produce "
                        + "unacceptable image quality. Source image attributes: '"
                        + transformedImageAttributes.getSourceImageAttributes()
                        + "'. Output image attributes: '"
                        + transformedImageAttributes.getOutputImageAttributes() + "'");
    }

    @Test
    public void testResolveWhenMappingPerformedAndSingleResourceFoundAndAbsolutePixelWidth()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                        getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            final int scaledImagePixelWidth = 180;
            recordLoadIphoneGroupImageProperties(createIphoneGroupAbsoluteWidthImageProperties(
                    scaledImagePixelWidth));
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordGetDeviceAttributesForImageScaling();

            recordGetImageRatioDeviceProperty(null);

            recordTransformImage(createAbsoluteWidthImageTransformationParameters(
                    scaledImagePixelWidth, 1),
                    SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    scaledImagePixelWidth, OUTPUT_IMAGE_PIXEL_HEIGHT);


            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedImageResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                    Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndSingleResourceFoundAndImageRatioNotNull()
        throws Throwable {

        final String[] testValues =
        { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadIphoneGroupImageProperties(createIphoneGroupPercentWidthImageProperties());
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordGetDeviceAttributesForImageScaling();

            recordGetImageRatioDeviceProperty(2);
            recordTransformImage(createPercentWidthImageTransformationParameters(
                    2), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    OUTPUT_IMAGE_PIXEL_WIDTH, OUTPUT_IMAGE_PIXEL_HEIGHT);


            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }
    }

    @Test
    public void testResolveWhenMappingPerformedAndImagePropertiesCannotBeParsed() throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                        getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadIphoneGroupImageProperties(createIphoneGroupInvalidImageProperties());
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordLogWarningExceptionEncountered();

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            recordLogWarningIfEmptyResolvedResources(
                    getResourcePathTestData().getRequestedImageResourcePath());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedImageResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources = new ArrayList<Resource>();
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordGetImageRatioDeviceProperty(final Integer ratio) {

        EasyMock.expect(
                getMockDevice().getPropertyAsInteger(
                        TransformedImageResourceResolverBean.IMAGE_RATIO_DEVICE_PROPERTY_NAME))
                .andReturn(ratio);
    }

    @Test
    public void testResolveWhenMappingPerformedAndDotNullResourceFound() throws Throwable {

        final String[] testValues =
        { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(1);

            recordListIphoneFilesByExtension(getSingleMatchedDotNullImageArray());

            recordPutResourceCache(resourceCacheKey,
                    getMappedIPhoneDotNullImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                Arrays.asList(getMappedIPhoneDotNullImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndImageTransformationPropertiesEmpty()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                        getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadIphoneGroupImageProperties(new Properties());
            recordLoadAppleGroupImageProperties(new Properties());

            recordGetDeviceAttributesForImageScaling();

            recordTransformImage(createImageTransformationParametersPreserveDimensions(),
                    SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT);

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedImageResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources =
                    Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong", expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordTransformImage(
            final ImageTransformationParameters imageTransformationParameters,
            final int sourcePixelWidth, final int sourcePixelHeight,
            final int outputPixelWidth, final int outputPixelHeight) {

        EasyMock.expect(
                getMockImageTransformationFactory().transformImage(
                        getMappedIPhonePngImageResourcePath().getNewFile(),
                        getMappedIPhonePngImageResourcePath().getNewFile().getParentFile(),
                        imageTransformationParameters)).andReturn(
                createTransformedImageAttributes(sourcePixelWidth, sourcePixelHeight,
                        outputPixelWidth, outputPixelHeight));
    }

    private void recordGetDeviceAttributesForImageScaling() {
        EasyMock.expect(getMockDevice().getPixelsX()).andReturn(new Integer(450));
    }

    private void recordLoadIphoneGroupImageProperties(final Properties properties)
            throws IOException {
        final File propertiesFile =
                new File(getMappedIPhonePngImageResourcePath().getNewFile().getParentFile(),
                        "unmetered.properties");

        EasyMock.expect(getMockPropertiesLoader().loadPropertiesNotNull(propertiesFile)).andReturn(
                properties);
    }

    private void recordLoadAppleGroupImageProperties(final Properties properties)
            throws IOException {
        final File propertiesFile =
                new File(getMappedAppleGroupImageResourcePath().getNewFile().getParentFile(),
                        "unmetered.properties");

        EasyMock.expect(getMockPropertiesLoader().loadPropertiesNotNull(propertiesFile)).andReturn(
                properties);
    }

    private Resource getMappedScaledIphoneGroupGifImageResourcePath() {
        return getResourcePathTestData().getMappedScaledIphoneGroupGifImageResourcePath();
    }

    private TransformedImageAttributes createTransformedImageAttributes(final int sourcePixelWidth,
            final int sourcePixelHeight, final int outputPixelWidth,
            final int outputPixelHeight) {

        final TransformedImageAttributesBean transformedImageAttributesBean =
                new TransformedImageAttributesBean();

        final ImageAttributesBean sourceImageAttributesBean = new ImageAttributesBean();
        sourceImageAttributesBean.setImageFile(getMappedIphoneGroupPngImageResourcePath()
                .getNewFile());
        sourceImageAttributesBean.setPixelWidth(sourcePixelWidth);
        sourceImageAttributesBean.setPixelHeight(sourcePixelHeight);

        transformedImageAttributesBean.setSourceImageAttributes(sourceImageAttributesBean);

        final ImageAttributesBean outputImageAttributesBean = new ImageAttributesBean();
        outputImageAttributesBean.setImageFile(getMappedScaledIphoneGroupGifImageResourcePath()
                .getNewFile());
        outputImageAttributesBean.setPixelWidth(outputPixelWidth);
        outputImageAttributesBean.setPixelHeight(outputPixelHeight);

        transformedImageAttributesBean.setOutputImageAttributes(outputImageAttributesBean);

        return transformedImageAttributesBean;
    }

    private ImageTransformationParameters createPercentWidthImageTransformationParameters(
            final int imageRatio) {

        final ImageTransformationParametersBean parametersBean =
                new ImageTransformationParametersBean();
        parametersBean.setDeviceImagePercentWidth(20 * imageRatio);
        parametersBean.setDevicePixelWidth(450);

        parametersBean.setOutputImageFormat(ImageTransformationFactory.ImageFormat.PNG);
        return parametersBean;

    }

    private ImageTransformationParameters createAbsoluteWidthImageTransformationParameters(
            final int pixelWidth, final int imageRatio) {

        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();
        parametersBean.setAbsolutePixelWidth(pixelWidth * imageRatio);
        parametersBean.setDevicePixelWidth(450);

        parametersBean.setOutputImageFormat(ImageTransformationFactory.ImageFormat.PNG);
        return parametersBean;

    }

    private ImageTransformationParameters createImageTransformationParametersPreserveDimensions() {
        final ImageTransformationParametersBean parametersBean =
            new ImageTransformationParametersBean();

        parametersBean.setDevicePixelWidth(450);
        parametersBean.setOutputImageFormat(ImageTransformationFactory.ImageFormat.PNG);

        return parametersBean;

    }

    private Properties createIphoneGroupPercentWidthImageProperties() {
        final Properties properties = new Properties();
        properties.setProperty("width", "20%");
        return properties;
    }

    private Properties createIphoneGroupAbsoluteWidthImageProperties(final int pixelWidth) {
        final Properties properties = new Properties();
        properties.setProperty("width", pixelWidth + "px");
        return properties;
    }

    private Properties createIphoneGroupInvalidImageProperties() {
        final Properties properties = new Properties();
        properties.setProperty("width", "I'm not a number%");
        return properties;
    }

    private Properties createAppleGroupPercentWidthImageProperties() {
        final Properties properties = new Properties();
        properties.setProperty("width", "40%");
        return properties;
    }

    private void recordGetMatchingGroups() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedImageResourcePath()))
                .andReturn(getMockUiConfiguration()).atLeastOnce();

        final Group[] matchingGroups =
                new Group[] { getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData().getRequestedImageResourcePath(),
                        new Group[] { getGroupTestData().createIPhoneGroup(),
                                getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    private File[] getSingleMatchedPngImageArray() {
        return new File[] {
                getMappedIPhonePngImageResourcePath().getNewFile() };
    }

    private File[] getSingleMatchedDotNullImageArray() {
        return new File[] {
                getMappedIPhoneDotNullImageResourcePath().getNewFile() };
    }

    private File[] getMultipleMatchedPngImageArray() {
        return new File[] {
                getMappedIPhonePngImageResourcePath().getNewFile(),
                getMappedIPhoneGroupGifImageResourcePath().getNewFile() };
    }

    private Resource getMappedIPhonePngImageResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupPngImageResourcePath();
    }

    private Resource getMappedIPhoneDotNullImageResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupDotNullImageResourcePath();
    }

    private Resource getMappedIPhoneGroupGifImageResourcePath() {
        return getResourcePathTestData()
        .getMappedIphoneGroupGifImageResourcePath();
    }

    @Test
    public void testResolveWhenMappingPerformedAndMultipleResourcesFound() throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(2);

            recordListIphoneFilesByExtension(getMultipleMatchedPngImageArray());

            recordLogWarningResolveToSingleFoundMultipleResources();

            recordLoadIphoneGroupImageProperties(createIphoneGroupPercentWidthImageProperties());
            recordLoadAppleGroupImageProperties(createAppleGroupPercentWidthImageProperties());

            recordGetDeviceAttributesForImageScaling();

            recordGetImageRatioDeviceProperty(null);

            recordTransformImage(createPercentWidthImageTransformationParameters(
                    1), SOURCE_IMAGE_PIXEL_WIDTH, SOURCE_IMAGE_PIXEL_HEIGHT,
                    OUTPUT_IMAGE_PIXEL_WIDTH, OUTPUT_IMAGE_PIXEL_HEIGHT);

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupGifImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources
                = Arrays.asList(getMappedScaledIphoneGroupGifImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong",
                    expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordGetMatchingGroupIterator(final int times) {

        final List<Group> matchingGroups =
                Arrays.asList(getGroupTestData().createIPhoneGroup(), getGroupTestData()
                        .createAppleGroup());

        for (int i = 0; i < times; i++) {
            // Explicitly record twice so that a new iterator instance is
            // returned each time.
            EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
                    .andReturn(matchingGroups.iterator());
        }

    }

    private void recordLogWarningResolveToSingleFoundMultipleResources() {

        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);

        getMockResourceResolutionWarnLogger().warn(
                "Requested resource '" + getResourcePathTestData().getRequestedImageResourcePath()
                        + "' resolved to multiple real resources with extensions matching "
                        + ArrayUtils.toString(MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS)
                        + ". Will only use the first resource. Total found: ["
                        + getMappedIPhonePngImageResourcePath().getNewFile() + ", "
                        + getMappedIPhoneGroupGifImageResourcePath().getNewFile() + "].");
    }

    private void recordLogWarningExceptionEncountered() {

        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);

        getMockResourceResolutionWarnLogger().warn(
                EasyMock.eq("Error resolving requested resource: '"
                        + getResourcePathTestData().getRequestedImageResourcePath() + "'"),
                EasyMock.isA(ResourceResolutionRuntimeException.class));
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
    throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator(1);

            recordListIphoneFilesByExtension(new File[] {});
            recordListAppleFilesByExtension(new File[] {});

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
            recordLogWarningIfEmptyResolvedResources(
                    getResourcePathTestData().getRequestedImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertNotNull("actualResources should not be null",
                    actualResources);
            Assert.assertTrue("actualResources should be empty",
                    actualResources.isEmpty());


            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourcesFromCache() throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            recordGetFromResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources
                = Arrays.asList(getMappedIphoneGroupPngImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong",
                    expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey) {
        final ResourceCacheEntryBean resourceCacheEntryBean =
            new ResourceCacheEntryBean(
                new Resource[] { getMappedIphoneGroupPngImageResourcePath() },
                    ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                    ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                resourceCacheEntryBean);
    }

    private void recordListIphoneFilesByExtension(final File[] files) {

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesDir()),
                        EasyMock.eq(getMappedIphoneGroupImageResourcePath().getNewPath()),
                        EasyMock.aryEq(MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS),
                        EasyMock.aryEq(EXCLUDED_IMAGE_FILE_EXTENSION_WILDCARDS))).andReturn(files);

    }

    private void recordListAppleFilesByExtension(final File[] files) {

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesDir()),
                        EasyMock.eq(getMappedAppleGroupImageResourcePath().getNewPath()),
                        EasyMock.aryEq(MATCHED_IMAGE_FILE_EXTENSION_WILDCARDS),
                        EasyMock.aryEq(EXCLUDED_IMAGE_FILE_EXTENSION_WILDCARDS))).andReturn(files);

    }

    private Resource getMappedIphoneGroupImageResourcePath() {

        return getResourcePathTestData().getMappedIphoneGroupImageResourcePath();
    }

    private Resource getMappedAppleGroupImageResourcePath() {

        return getResourcePathTestData().getMappedAppleGroupImageResourcePath();
    }

    private Resource getMappedIphoneGroupPngImageResourcePath() {

        return getResourcePathTestData().getMappedIphoneGroupPngImageResourcePath();
    }

    private File getRootResourcesDir() {
        return getResourcePathTestData().getRootResourcesPath();
    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedCssResourcePath(),
                    getMockDevice());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

    }

    @Test
    public void testSupportsWhenTrue() throws Throwable {

        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedImageResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {

        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
    }

    /**
     * @return the objectUnderTest
     */
    private TransformedImageResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final TransformedImageResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockDevice
     */
    @Override
    public Device getMockDevice() {

        return mockDevice;
    }

    /**
     * @param mockDevice  the mockDevice to set
     */
    @Override
    public void setMockDevice(final Device mockDevice) {

        this.mockDevice = mockDevice;
    }

    /**
     * @return the mockPropertiesLoader
     */
    public au.com.sensis.mobile.crf.util.PropertiesLoader getMockPropertiesLoader() {
        return mockPropertiesLoader;
    }

    /**
     * @param mockPropertiesLoader the mockPropertiesLoader to set
     */
    public void setMockPropertiesLoader(
            final au.com.sensis.mobile.crf.util.PropertiesLoader mockPropertiesLoader) {
        this.mockPropertiesLoader = mockPropertiesLoader;
    }

    /**
     * @return the mockScaledImageFactory
     */
    public ImageTransformationFactory getMockImageTransformationFactory() {
        return mockImageTransformationFactory;
    }

    /**
     * @param mockImageTransformationFactory
     *            the mockScaledImageFactory to set
     */
    public void setMockImageTransformationFactory(
            final ImageTransformationFactory mockImageTransformationFactory) {
        this.mockImageTransformationFactory = mockImageTransformationFactory;
    }
}

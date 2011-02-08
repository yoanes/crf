package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
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
import au.com.sensis.mobile.crf.util.Image;
import au.com.sensis.mobile.crf.util.ImageBean;
import au.com.sensis.mobile.crf.util.ImageScalingParametersBean;
import au.com.sensis.mobile.crf.util.ScaledImageFactory;
import au.com.sensis.mobile.crf.util.ScaledImageFactory.ImageScalingParameters;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Unit test {@link ScalingImageResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScalingImageResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private static final String[] FILE_EXTENSION_WILDCARDS = new String[] { "*" };
    private Device mockDevice;
    private ScalingImageResourceResolverBean objectUnderTest;
    private PropertiesLoader mockPropertiesLoader;
    private ScaledImageFactory mockScaledImageFactory;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new ScalingImageResourceResolverBean(
                getResourceResolverCommonParamHolder(), getResourcePathTestData()
                        .getAbstractImageExtensionWithLeadingDot(), getResourcesRootDir(),
                FILE_EXTENSION_WILDCARDS));

        getObjectUnderTest().setPropertiesLoader(getMockPropertiesLoader());
        getObjectUnderTest().setScaledImageFactory(getMockScaledImageFactory());

    }

    @Override
    protected ScalingImageResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        final ScalingImageResourceResolverBean resolverBean =
                new ScalingImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                        abstractResourceExtension, getResourcesRootDir(), FILE_EXTENSION_WILDCARDS);

        resolverBean.setPropertiesLoader(getMockPropertiesLoader());
        resolverBean.setScaledImageFactory(getMockScaledImageFactory());

        return resolverBean;
    }

    @Override
    protected ScalingImageResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new ScalingImageResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ScalingImageResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new ScalingImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ScalingImageResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new ScalingImageResourceResolverBean(commonParams,
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), FILE_EXTENSION_WILDCARDS);
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
                new ScalingImageResourceResolverBean(getResourceResolverCommonParamHolder(),
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

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator();

            recordListIphoneFilesByExtension(getSingleMatchedPngImageArray());

            recordLoadScaledImageProperties();

            recordGetDeviceAttributesForImageScaling();

            recordScaleImage();

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupPngImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources
                = Arrays.asList(getMappedScaledIphoneGroupPngImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong",
                    expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordScaleImage() {
        EasyMock.expect(getMockScaledImageFactory().scaleImage(
                getMappedIPhonePngImageResourcePath().getNewFile(),
                getMappedIPhonePngImageResourcePath().getNewFile().getParentFile(),
                createImageScalingParameters())).andReturn(createScaledImage());
    }

    private void recordGetDeviceAttributesForImageScaling() {
        EasyMock.expect(getMockDevice().getPixelsX()).andReturn(new Integer(450));
        EasyMock.expect(getMockDevice().getPropertyAsString("preferredimagetype"))
            .andReturn("image/png");
    }

    private void recordLoadScaledImageProperties() {
        EasyMock.expect(getMockPropertiesLoader().loadProperties(getMockDevice(),
                getResourcePathTestData().getRequestedScaledImagePropertiesResourcePath()))
                    .andReturn(createScaledImageProperties());
    }


    private Resource getMappedScaledIphoneGroupPngImageResourcePath() {
        return getResourcePathTestData().getMappedScaledIphoneGroupPngImageResourcePath();
    }
    private Image createScaledImage() {
        final ImageBean imageBean = new ImageBean();
        imageBean.setImageFile(getMappedScaledIphoneGroupPngImageResourcePath().getNewFile());
        imageBean.setPixelWidth(90);
        imageBean.setPixelHeight(60);
        return imageBean;
    }

    private ImageScalingParameters createImageScalingParameters() {
        // TODO
        final ImageScalingParametersBean parametersBean = new ImageScalingParametersBean();
        parametersBean.setDeviceImagePercentWidth(20);
        parametersBean.setDevicePixelWidth(450);
        parametersBean.setOutputImageFormat(ScaledImageFactory.ImageFormat.PNG);
        return parametersBean;

    }

    private Properties createScaledImageProperties() {
        final Properties properties = new Properties();
        properties.setProperty("width", "20%");
        return properties;
    }

    private void recordGetMatchingGroups() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedImageResourcePath()))
                .andReturn(getMockUiConfiguration());

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

    private File[] getMultipleMatchedPngImageArray() {
        return new File[] {
                getMappedIPhonePngImageResourcePath().getNewFile(),
                getMappedIPhoneGroupGifImageResourcePath().getNewFile() };
    }

    private Resource getMappedIPhonePngImageResourcePath() {
        return getResourcePathTestData()
        .getMappedIphoneGroupPngImageResourcePath();
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

            recordGetMatchingGroupIterator();

            recordListIphoneFilesByExtension(getMultipleMatchedPngImageArray());

            recordLogWarningResolveToSingleFoundMultipleResources();

            recordLoadScaledImageProperties();

            recordGetDeviceAttributesForImageScaling();

            recordScaleImage();

            recordPutResourceCache(resourceCacheKey,
                    getMappedScaledIphoneGroupPngImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            final List<Resource> expectedResources
                = Arrays.asList(getMappedScaledIphoneGroupPngImageResourcePath());
            assertComplexObjectsEqual("actualResources is wrong",
                    expectedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(expectedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedImageResourcePath())).andReturn(
                getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
                Arrays.asList(getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice())).andReturn(
                matchingGroupsIterator);

    }

    private void recordLogWarningResolveToSingleFoundMultipleResources() {

        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled())
        .andReturn(Boolean.TRUE);

        getMockResourceResolutionWarnLogger()
        .warn("Requested resource '"
                + getResourcePathTestData().getRequestedImageResourcePath()
                + "' resolved to multiple real resources with extensions matching "
                + ArrayUtils.toString(FILE_EXTENSION_WILDCARDS)
                + ". Will only return the first resource. Total found: ["
                + getMappedIPhonePngImageResourcePath().getNewFile()
                + ", " + getMappedIPhoneGroupGifImageResourcePath().getNewFile()
                + "].");
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

            recordGetMatchingGroupIterator();

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
                        EasyMock.aryEq(FILE_EXTENSION_WILDCARDS))).andReturn(files);

    }

    private void recordListAppleFilesByExtension(final File[] files) {

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesDir()),
                        EasyMock.eq(getMappedAppleGroupImageResourcePath().getNewPath()),
                        EasyMock.aryEq(FILE_EXTENSION_WILDCARDS))).andReturn(files);

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
    private ScalingImageResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ScalingImageResourceResolverBean objectUnderTest) {
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
    public PropertiesLoader getMockPropertiesLoader() {
        return mockPropertiesLoader;
    }

    /**
     * @param mockPropertiesLoader the mockPropertiesLoader to set
     */
    public void setMockPropertiesLoader(final PropertiesLoader mockPropertiesLoader) {
        this.mockPropertiesLoader = mockPropertiesLoader;
    }

    /**
     * @return the mockScaledImageFactory
     */
    public ScaledImageFactory getMockScaledImageFactory() {
        return mockScaledImageFactory;
    }

    /**
     * @param mockScaledImageFactory the mockScaledImageFactory to set
     */
    public void setMockScaledImageFactory(final ScaledImageFactory mockScaledImageFactory) {
        this.mockScaledImageFactory = mockScaledImageFactory;
    }
}

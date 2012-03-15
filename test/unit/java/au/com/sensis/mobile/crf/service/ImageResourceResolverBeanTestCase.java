package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;

/**
 * Unit test {@link ImageResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private static final String[] FILE_EXTENSION_WILDCARDS = new String[] { "*" };
    private Device mockDevice;
    private ImageResourceResolverBean objectUnderTest;
    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), FILE_EXTENSION_WILDCARDS));

    }

    @Override
    protected ImageResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir(), FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
                new ResourceResolverCommonParamHolder(
                        resourceResolutionWarnLogger, getDeploymentMetadata(),
                        getMockConfigurationFactory(), getMockResourceCache());

        return new ImageResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
                new ResourceResolverCommonParamHolder(
                        getMockResourceResolutionWarnLogger(), deploymentMetadata,
                        getMockConfigurationFactory(), getMockResourceCache());

        return new ImageResourceResolverBean(commonParams,
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
                new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
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

            recordPutResourceCache(resourceCacheKey, getMappedIphoneGroupPngImageResourcePath());

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

            recordPutResourceCache(resourceCacheKey, getMappedIphoneGroupPngImageResourcePath());

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
    private ImageResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageResourceResolverBean objectUnderTest) {
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
}

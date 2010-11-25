package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;
import au.com.sensis.mobile.crf.debug.ResourceTreeNode;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

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
                getResourcesRootDir(), getMockResourceCache(), FILE_EXTENSION_WILDCARDS));

        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree(true));
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree());
    }

    @Override
    protected ImageResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir(), getMockResourceCache(), FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new ImageResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceCache(), FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new ImageResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceCache(), FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new ImageResourceResolverBean(commonParams,
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), getMockResourceCache(), FILE_EXTENSION_WILDCARDS);
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
                        getResourcesRootDir(), getMockResourceCache(), testValue);

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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordListFilesByExtension(getSingleMatchedPngImageArray());

            recordPutResourceCache(resourceCacheKey, getMappedIphoneGroupPngImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                    actualResources);

            assertResourceResolutionTreeUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }


    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey = new ResourceCacheKeyBean(
                getResourcePathTestData().getRequestedImageResourcePath(),
                getGroupTestData().createIPhoneGroup());
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

    private void assertResourceResolutionTreeUpdated() {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        Assert.assertTrue("ResourceResolutionTree treePreOrderIterator should have a next item",
                treePreOrderIterator.hasNext());
        final ResourceTreeNode resourceTreeNode = treePreOrderIterator.next();
        Assert.assertNotNull("first item from preOrderIterator should not be null",
                resourceTreeNode);
        Assert.assertEquals("first item from preOrderIterator has wrong resource",
                getMappedIphoneGroupPngImageResourcePath(), resourceTreeNode.getResource());

    }

    @Test
    public void testResolveWhenMappingPerformedAndMultipleResourcesFound() throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordListFilesByExtension(getMultipleMatchedPngImageArray());

            recordLogWarningResolveToSingleFoundMultipleResources();

            recordPutResourceCache(resourceCacheKey, getMappedIphoneGroupPngImageResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                    actualResources);

            assertResourceResolutionTreeUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(getMockConfigurationFactory().getUiConfiguration(
                getResourcePathTestData().getRequestedImageResourcePath())).andReturn(
                        getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
        .andReturn(matchingGroupsIterator);

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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordListFilesByExtension(new File[] {});

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            Assert.assertNotNull("actualResources should not be null",
                    actualResources);
            Assert.assertTrue("actualResources should be empty",
                    actualResources.isEmpty());

            // Explicit verify and reset since we are in a loop.
            verify();
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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            recordGetFromResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getMockDevice());

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                    actualResources);

            assertResourceResolutionTreeUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getMappedIphoneGroupPngImageResourcePath()});
    }

    private void recordListFilesByExtension(final File[] files) {

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesDir()),
                        EasyMock.eq(getMappedIphoneGroupImageResourcePath().getNewPath()),
                        EasyMock.aryEq(FILE_EXTENSION_WILDCARDS))).andReturn(files);

    }

    private Resource getMappedIphoneGroupImageResourcePath() {

        return getResourcePathTestData()
        .getMappedIphoneGroupImageResourcePath();
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

package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;

/**
 * Unit test {@link CssResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CssResourceResolverBeanTestCase extends AbstractMultipleResourceResolverTestCase {

    private CssResourceResolverBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new CssResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceAccumulatorFactory()));
    }

    // Override Abstract test methods //

    @Override
    protected CssResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new CssResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir(), getMockResourceAccumulatorFactory());
    }

    @Override
    protected CssResourceResolverBean createWithRootResourcesDir(final File rootResourcesDir) {

        return new CssResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceAccumulatorFactory());
    }

    @Override
    protected CssResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new CssResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceAccumulatorFactory());
    }

    @Override
    protected CssResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new CssResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceAccumulatorFactory());
    }

    // Tests //

    @Test
    public void testResolveWhenMappingPerformedAndResourceExistsAndBundlingDisabled()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetResourceAccumulator();
            recordIsBundlingEnabled(Boolean.FALSE);

            recordGetMatchingGroupIterator();
            recordAccumulateIphoneGroupResourceBundlingDisabled(Boolean.FALSE, Boolean.TRUE);
            recordAccumulateAppleGroupResourceBundlingDisabled(Boolean.FALSE, Boolean.TRUE);

            final List<Resource> expectedResources = Arrays.asList(
                    getMappedAppleGroupCssResourcePath(),
                    getMappedIphoneGroupCssResourcePath());
            recordGetResourcesFromAccumulator(expectedResources);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    expectedResources, actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExistsAndBundlingEnabled()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetResourceAccumulator();
            recordIsBundlingEnabled(Boolean.TRUE);

            // First group iterator used to determine first group so that we can build a
            // ResourceCacheKey to check if the bundle exists in the cache.
            recordGetMatchingGroupIterator();

            recordCheckResourceCache(createIphoneGroupResourceCacheKey(), Boolean.FALSE);

            // Second group iterator used to accumulate resources from all groups.
            recordGetMatchingGroupIterator();
            recordAccumulateIphoneGroupResourceBundlingEnabled(Boolean.TRUE);
            recordAccumulateAppleGroupResourceBundlingEnabled(Boolean.TRUE);

            final List<Resource> expectedResources = Arrays.asList(getMappedBundleResourcePath());
            recordGetResourcesFromAccumulator(expectedResources);

            recordPutBundleIntoResourceCache(createIphoneGroupResourceCacheKey());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedCssResourcePath(),
                            getMockDevice());

            Assert.assertEquals("actualResources is wrong", expectedResources, actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private Resource getMappedBundleResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupCssBundleResourcePath();
    }

    private void recordAccumulateAppleGroupResourceBundlingEnabled(final Boolean resourceExists) {
        recordCheckIfNewAppleGroupPathExists(resourceExists);
        if (resourceExists) {
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedAppleGroupCssResourcePath()));
        } else {
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());
        }

    }

    private void recordAccumulateAppleGroupResourceBundlingDisabled(final Boolean foundInCache,
            final Boolean resourceExists) {
        final ResourceCacheKey resourceCacheKey = createAppleGroupResourceCacheKey();

        recordCheckResourceCache(resourceCacheKey, foundInCache);
        if (foundInCache) {
            recordGetAppleGroupResourceFromCache(resourceCacheKey);

            final List<Resource> expectedResources =
                    Arrays.asList(getMappedAppleGroupCssResourcePath());
            getMockResourceAccumulator().accumulate(expectedResources);

            return;
        }

        recordCheckIfNewAppleGroupPathExists(resourceExists);
        if (resourceExists) {
            recordPutResourceCache(resourceCacheKey, getMappedAppleGroupCssResourcePath());
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedAppleGroupCssResourcePath()));
        } else {
            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());
        }
    }

    protected void recordPutBundleIntoResourceCache(final ResourceCacheKey resourceCacheKey) {
        getMockResourceCache().put(EasyMock.eq(resourceCacheKey),
                EasyMock.aryEq(new Resource[] { getMappedBundleResourcePath() }));
    }

    private void recordAccumulateIphoneGroupResourceBundlingEnabled(final Boolean resourceExists) {
        recordCheckIfNewIphoneGroupPathExists(resourceExists);
        if (resourceExists) {
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedIphoneGroupCssResourcePath()));
        } else {
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

        }
    }

    private void recordAccumulateIphoneGroupResourceBundlingDisabled(final Boolean foundInCache,
            final Boolean resourceExists) {
        final ResourceCacheKey resourceCacheKey = createIphoneGroupResourceCacheKey();
        recordCheckResourceCache(resourceCacheKey, foundInCache);

        if (foundInCache) {
            recordGetIphoneGroupResourceFromCache(resourceCacheKey);

            final List<Resource> expectedResources =
                    Arrays.asList(getMappedIphoneGroupCssResourcePath());
            getMockResourceAccumulator().accumulate(expectedResources);

            return;
        }

        recordCheckIfNewIphoneGroupPathExists(resourceExists);
        if (resourceExists) {
            recordPutResourceCache(resourceCacheKey, getMappedIphoneGroupCssResourcePath());
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedIphoneGroupCssResourcePath()));
        } else {
            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

        }

    }

    private Resource getMappedIphoneGroupCssResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupCssResourcePath();
    }

    private Resource getMappedAppleGroupCssResourcePath() {
        return getResourcePathTestData().getMappedAppleGroupCssResourcePath();
    }

    private void recordGetResourceAccumulator() {
        EasyMock.expect(getMockResourceAccumulatorFactory().getCSSResourceAccumulator())
            .andReturn(getMockResourceAccumulator());
    }

    private ResourceCacheKey createIphoneGroupResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey = new ResourceCacheKeyBean(
                getResourcePathTestData().getRequestedCssResourcePath(),
                getGroupTestData().createIPhoneGroup());
        return resourceCacheKey;
    }

    private ResourceCacheKey createAppleGroupResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey = new ResourceCacheKeyBean(
                getResourcePathTestData().getRequestedCssResourcePath(),
                getGroupTestData().createAppleGroup());
        return resourceCacheKey;
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
    throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupIterator();

            recordGetResourceAccumulator();

            recordIsBundlingEnabled(Boolean.FALSE);

//            final ResourceCacheKey resourceCacheKey = createIphoneGroupResourceCacheKey();
//            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);
//
//            recordCheckIfNewIphoneGroupPathExists(Boolean.FALSE);
//
//            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
//
//            final ArrayList<Resource> expectedResources = new ArrayList<Resource>();
//            getMockResourceAccumulator().accumulate(expectedResources);

            recordAccumulateIphoneGroupResourceBundlingDisabled(Boolean.FALSE, Boolean.FALSE);

            recordAccumulateAppleGroupResourceBundlingDisabled(Boolean.FALSE, Boolean.FALSE);


            recordGetResourcesFromAccumulator(new ArrayList<Resource>());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
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
    public void testResolveWhenNoMappingPerformed() throws Throwable {

        recordGetMatchingGroupIterator();

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedJspResourcePath(),
                    getMockDevice());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceFromCacheAndBundlingDisabled()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetResourceAccumulator();
            recordIsBundlingEnabled(Boolean.FALSE);

            recordGetMatchingGroupIterator();
            recordAccumulateIphoneGroupResourceBundlingDisabled(Boolean.TRUE, Boolean.TRUE);
            recordAccumulateAppleGroupResourceBundlingDisabled(Boolean.TRUE, Boolean.TRUE);

            final List<Resource> expectedResources = Arrays.asList(
                    getMappedAppleGroupCssResourcePath(),
                    getMappedIphoneGroupCssResourcePath());
            recordGetResourcesFromAccumulator(expectedResources);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    expectedResources, actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceFromCacheAndBundlingEnabled()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetResourceAccumulator();
            recordIsBundlingEnabled(Boolean.TRUE);

            recordGetMatchingGroupIterator();
            recordCheckResourceCache(createIphoneGroupResourceCacheKey(), Boolean.TRUE);

            recordGetBundleResourceFromCache(createIphoneGroupResourceCacheKey());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedCssResourcePath(),
                            getMockDevice());

            Assert.assertEquals("actualResources is wrong", Arrays
                    .asList(getMappedBundleResourcePath()), actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordGetIphoneGroupResourceFromCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getMappedIphoneGroupCssResourcePath()});
    }

    private void recordGetAppleGroupResourceFromCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getMappedAppleGroupCssResourcePath()});
    }

    private void recordGetBundleResourceFromCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getMappedBundleResourcePath()});
    }

    @Test
    public void testSupportsWhenTrue() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedJspResourcePath()));
    }

    @Test
    public void testGetResourceAccumulator() throws Throwable {

        recordGetResourceAccumulator();

        replay();

        final ResourceAccumulator actualAccumulator =
                getObjectUnderTest().createResourceAccumulator();

        Assert.assertEquals("ResourceAccumulator is wrong", getMockResourceAccumulator(),
                actualAccumulator);
    }


    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedCssResourcePath())).andReturn(
                getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
                Arrays.asList(getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice())).andReturn(
                matchingGroupsIterator);

    }

    private void recordCheckIfNewIphoneGroupPathExists(final Boolean exists) {
        EasyMock.expect(getMockFileIoFacade().fileExists(
                getResourcePathTestData().getRootResourcesPath(),
                getMappedIphoneGroupCssResourcePath().getNewPath()))
                .andReturn(exists);
    }

    private void recordCheckIfNewAppleGroupPathExists(final Boolean exists) {
        EasyMock.expect(getMockFileIoFacade().fileExists(
                getResourcePathTestData().getRootResourcesPath(),
                getResourcePathTestData().getMappedAppleGroupCssResourcePath().getNewPath()))
                .andReturn(exists);
    }

    /**
     * @return the objectUnderTest
     */
    private CssResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final CssResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}

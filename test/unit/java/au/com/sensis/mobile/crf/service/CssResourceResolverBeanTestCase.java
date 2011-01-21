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
    public void testResolveWhenMappingPerformedAndResourceExists()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIterator();

            recordCheckIfNewIphoneGroupPathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedIphoneGroupCssResourcePath()));

            recordCheckIfNewAppleGroupPathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedAppleGroupCssResourcePath()));

            final List<Resource> accumulatedResources = Arrays.asList(
                    getMappedAppleGroupCssResourcePath(),
                    getMappedIphoneGroupCssResourcePath());
            recordGetResourcesFromAccumulator(accumulatedResources);

            recordPutResourceCache(resourceCacheKey, accumulatedResources
                    .toArray(new Resource[] {}));

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertEquals("actualResources is wrong",
                    accumulatedResources, actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();
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

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData().getRequestedCssResourcePath(),
                        new Group[] { getGroupTestData().createIPhoneGroup(),
                                getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist() throws Throwable {
        final String[] testValues =
                { getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator();
            recordGetResourceAccumulator();

            recordCheckIfNewIphoneGroupPathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordCheckIfNewAppleGroupPathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordGetResourcesFromAccumulator(new ArrayList<Resource>());
            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedCssResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertNotNull("actualResources should not be null", actualResources);
            Assert.assertTrue("actualResources should be empty", actualResources.isEmpty());

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
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
    public void testResolveWhenMappingPerformedAndResourceFromCache()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            final List<Resource> accumulatedResources = Arrays.asList(
                    getMappedAppleGroupCssResourcePath(),
                    getMappedIphoneGroupCssResourcePath());
            recordGetFromResourceCache(resourceCacheKey, accumulatedResources);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertEquals("actualResources is wrong",
                    accumulatedResources, actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

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

    private void recordGetMatchingGroups() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedCssResourcePath())).andReturn(
                getMockUiConfiguration());

        final Group[] matchingGroups =
                new Group[] { getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

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

package au.com.sensis.mobile.crf.service;

import java.io.File;
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
public class CssResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

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
                getResourcesRootDir(), getMockResourceCache()));
    }

    // Override Abstract test methods //

    @Override
    protected CssResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new CssResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir(), getMockResourceCache());
    }

    @Override
    protected CssResourceResolverBean createWithRootResourcesDir(final File rootResourcesDir) {

        return new CssResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceCache());
    }

    @Override
    protected CssResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new CssResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceCache());
    }

    @Override
    protected CssResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new CssResourceResolverBean(commonParams,
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceCache());
    }

    // Tests //

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordCheckIfNewPathExists(Boolean.TRUE);

            recordPutResourceCache(resourceCacheKey, getResourcePathTestData()
                    .getMappedIphoneGroupCssResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupCssResourcePath()),
                            actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey = new ResourceCacheKeyBean(
                getResourcePathTestData().getRequestedCssResourcePath(),
                getGroupTestData().createIPhoneGroup());
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

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordCheckIfNewPathExists(Boolean.FALSE);

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

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
    public void testResolveWhenMappingPerformedAndResourceFromCache() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            recordGetFromResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupCssResourcePath()),
                            actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getResourcePathTestData().getMappedIphoneGroupCssResourcePath()});
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

        final ResourceAccumulator actualAccumulator =
            getObjectUnderTest().createResourceAccumulator();

        Assert.assertTrue(actualAccumulator instanceof BundleResourceAccumulatorBean);
    }


    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(getMockConfigurationFactory().getUiConfiguration(
                getResourcePathTestData().getRequestedCssResourcePath())).andReturn(
                        getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
        .andReturn(matchingGroupsIterator);

    }

    private void recordCheckIfNewPathExists(final Boolean exists) {
        EasyMock.expect(getMockFileIoFacade().fileExists(
                getResourcePathTestData().getRootResourcesPath(),
                getResourcePathTestData().getMappedIphoneGroupCssResourcePath().getNewPath()))
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

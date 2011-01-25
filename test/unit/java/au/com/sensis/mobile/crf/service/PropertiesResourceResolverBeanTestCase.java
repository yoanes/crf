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
 * Unit test {@link PropertiesResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesResourceResolverBeanTestCase
    extends AbstractMultipleResourceResolverTestCase {

    private PropertiesResourceResolverBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new PropertiesResourceResolverBean(
                getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceAccumulatorFactory()));
    }

    @Override
    protected PropertiesResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new PropertiesResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir(),
                getMockResourceAccumulatorFactory());
    }

    @Override
    protected PropertiesResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new PropertiesResourceResolverBean(commonParams,
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceAccumulatorFactory());
    }

    @Override
    protected PropertiesResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new PropertiesResourceResolverBean(commonParams,
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceAccumulatorFactory());
    }

    @Override
    protected PropertiesResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new PropertiesResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                rootResourcesDir, getMockResourceAccumulatorFactory());
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues =
                { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIterator();

            recordCheckIfNewIphonePathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedIphoneGroupPropertiesResourcePath()));

            recordCheckIfNewApplePathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getMappedAppleGroupPropertiesResourcePath()));

            final List<Resource> accumulatedResources =
                    Arrays.asList(getMappedAppleGroupPropertiesResourcePath(),
                            getMappedIphoneGroupPropertiesResourcePath());
            recordGetResourcesFromAccumulator(accumulatedResources);

            recordPutResourceCache(resourceCacheKey, accumulatedResources
                    .toArray(new Resource[] {}));

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedPropertiesResourcePath(),
                            getMockDevice());

            Assert.assertEquals("actualResources is wrong", accumulatedResources, actualResources);
            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordGetResourceAccumulator() {
        EasyMock.expect(getMockResourceAccumulatorFactory().getPropertiesResourceAccumulator())
            .andReturn(getMockResourceAccumulator());
    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData()
                        .getRequestedPropertiesResourcePath(), new Group[] {
                        getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist() throws Throwable {
        final String[] testValues =
        { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIterator();

            recordCheckIfNewIphonePathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordCheckIfNewApplePathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordGetResourcesFromAccumulator(new ArrayList<Resource>());

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
            recordLogWarningIfEmptyResolvedResources(
                    getResourcePathTestData().getRequestedPropertiesResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPropertiesResourcePath(),
                        getMockDevice());

            Assert.assertNotNull("actualResources should not be null", actualResources);
            Assert.assertTrue("actualResources should be empty", actualResources.isEmpty());
            assertResourceResolutionTreeNotUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordCheckIfNewIphonePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(getResourcePathTestData().getRootResourcesPath(),
                        getMappedIphoneGroupPropertiesResourcePath().getNewPath())).andReturn(
                exists);

    }

    private Resource getMappedIphoneGroupPropertiesResourcePath() {
        return getResourcePathTestData()
        .getMappedIphoneGroupPropertiesResourcePath();
    }

    private void recordCheckIfNewApplePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(getResourcePathTestData().getRootResourcesPath(),
                        getMappedAppleGroupPropertiesResourcePath().getNewPath()))
                .andReturn(exists);

    }

    private Resource getMappedAppleGroupPropertiesResourcePath() {
        return getResourcePathTestData().getMappedAppleGroupPropertiesResourcePath();
    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {

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
        final String[] testValues =
        { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            final List<Resource> accumulatedResources =
                Arrays.asList(getMappedAppleGroupPropertiesResourcePath(),
                        getMappedIphoneGroupPropertiesResourcePath());
            recordGetFromResourceCache(resourceCacheKey, accumulatedResources);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPropertiesResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong", accumulatedResources, actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }
    }

    @Test
    public void testSupportsWhenTrue() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedPropertiesResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedJspResourcePath()));
    }


    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedPropertiesResourcePath())).andReturn(
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
                        getResourcePathTestData().getRequestedPropertiesResourcePath()))
                .andReturn(getMockUiConfiguration());

        final Group[] matchingGroups =
                new Group[] { getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

    }

    /**
     * @return the objectUnderTest
     */
    private PropertiesResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final PropertiesResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}

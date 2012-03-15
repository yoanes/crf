package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.config.UiConfiguration;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;
import au.com.sensis.mobile.crf.debug.ResourceTreeNode;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.util.TimeGeneratorFactory;
import au.com.sensis.mobile.crf.util.TimerGeneratorStub;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Abstract base class for testing subclasses of {@link AbstractResourceResolver}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceResolverTestCase extends AbstractJUnit4TestCase {

    private static final int DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT = 1;
    private static final int DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS = 60000;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private final ResourceAccumulator resolvedResourcePaths = new ResourceAccumulatorBean();
    private Device mockDevice;
    private File resourcesRootDir;
    private FileIoFacade mockFileIoFacade;

    private ResourceResolverCommonParamHolder resourceResolverCommonParamHolder;
    private ConfigurationFactory mockConfigurationFactory;
    private UiConfiguration mockUiConfiguration;
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;
    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();
    private DeploymentMetadata deploymentMetadata;
    private ResourceAccumulatorFactory mockResourceAccumulatorFactory;
    private ResourceAccumulator mockResourceAccumulator;
    private ResourceCache mockResourceCache;
    private TimerGeneratorStub timeGenerator;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUpAbstractBaseClass() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setTimeGenerator(new TimerGeneratorStub());
        TimeGeneratorFactory.changeDefaultTimeGeneratorSingleton(getTimeGenerator());

        setResourcesRootDir(new File(getClass().getResource("/").toURI()));

        setDeploymentMetadata(getDeploymentMetadataTestData().createDevDeploymentMetadata());

        setResourceResolverCommonParamHolder(new ResourceResolverCommonParamHolder(
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(),
                getMockConfigurationFactory(),
                getMockResourceCache()));

        initResourceResolutionTree();
    }

    /**
     * Initialise the resource resolution tree.
     */
    protected final void initResourceResolutionTree() {
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree(true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        initResourceResolutionTree();
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDownAbstractBaseClass() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();
        TimeGeneratorFactory.restoreDefaultTimeGeneratorSingleton();
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree());
    }

    @Test
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                createWithAbstractResourceExtension(testValue);

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "abstractResourceExtension must not be blank: '"
                                + testValue + "'", e.getMessage());
            }
        }
    }

    /**
     * Return a {@link AbstractResourceResolver} subclass created with the given
     * abstractResourceExtension and default values for every other argument.
     *
     * @param abstractResourceExtension
     *            to construct the {@link AbstractResourceResolver} with.
     * @return a {@link AbstractResourceResolver} subclass created with the
     *         given abstractResourceExtension and default values for every
     *         other argument.
     */
    protected abstract AbstractResourceResolver createWithAbstractResourceExtension(
            final String abstractResourceExtension);

    @Test
    public void testConstructorWhenResourcesRootPathInvalid() throws Throwable {
        final File[] invalidPaths =
            {
                new File(StringUtils.EMPTY),
                new File(" "),
                new File("  "),
                new File("I-do-not-exist"),
                new File(
                        getClass()
                        .getResource(
                                "/au/com/sensis/mobile/crf/service/"
                                        + "CssResourceResolverBeanTestCase.class")
                                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                createWithRootResourcesDir(invalidPath);
                Assert
                .fail("IllegalArgumentException expected for invalidPath: '"
                        + invalidPath + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message",
                        "rootResourcesDir must be a directory: '" + invalidPath
                        + "'", e.getMessage());
            }
        }
    }

    /**
     * Return a {@link AbstractResourceResolver} subclass created with the given
     * rootResourcesDir and default values for every other argument.
     *
     * @param rootResourcesDir
     *            to construct the {@link AbstractResourceResolver} with.
     * @return a {@link AbstractResourceResolver} subclass created with the
     *         given rootResourcesDir and default values for every other
     *         argument.
     */
    protected abstract AbstractResourceResolver createWithRootResourcesDir(
            final File rootResourcesDir);

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerIsNull()
            throws Throwable {
        try {
            createWithResourceResolutionWarnLogger(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                    .getMessage());
        }

    }

    /**
     * Return a {@link AbstractResourceResolver} subclass created with the given
     * resourceResolutionWarnLogger and default values for every other argument.
     *
     * @param resourceResolutionWarnLogger
     *            to construct the {@link AbstractResourceResolver} with.
     * @return a {@link AbstractResourceResolver} subclass created with the
     *         given resourceResolutionWarnLogger and default values for every
     *         other argument.
     */
    protected abstract AbstractResourceResolver createWithResourceResolutionWarnLogger(
            ResourceResolutionWarnLogger resourceResolutionWarnLogger);

    @Test
    public void testConstructorWhenDeploymentMetadatIsNull()
            throws Throwable {
        try {
            createWithDeploymentMetadata(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "deploymentMetadata must not be null", e
                    .getMessage());
        }

    }

    @Test
    public void testResolveWhenCachedResourcesEmptyAndRefreshNeeded() throws Throwable {
        final AbstractResourceResolver localObjectUnderTest = createAbstractResourceResolver();

        recordGetMatchingGroups();

        final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
        recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

        final ResourceCacheEntryBean resourceCacheEntryBean =
                new ResourceCacheEntryBean(new Resource[] { },
                        ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                        ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
        recordGetFromResourceCache(resourceCacheKey, resourceCacheEntryBean);

        recordLogWarningIfEmptyCachedResourcesToBeRefreshed(
                getResourcePathTestData().getRequestedJspResourcePath(),
                resourceCacheEntryBean);

        recordPutResourceCache(resourceCacheKey, resourceCacheEntryBean);

        recordLogWarningIfEmptyResolvedResources(
                getResourcePathTestData().getRequestedJspResourcePath());

        replay();

        final List<Resource> actualResources =
                localObjectUnderTest.resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

        // Explicit verify since we are in a loop.
        verify();

        Assert.assertEquals("actualResources is wrong",
                Arrays.asList(new Resource [] {}),
                actualResources);

        assertResourceResolutionTreeNotUpdated();

        // Explicit reset since we are in a loop.
        reset();
    }

    @Test
    public void testResolveWhenCachedResourcesEmptyAndRefreshNotNeeded() throws Throwable {
        final AbstractResourceResolver localObjectUnderTest = createAbstractResourceResolver();

        recordGetMatchingGroups();

        final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
        recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

        final ResourceCacheEntryBean resourceCacheEntryBean =
                new ResourceCacheEntryBean(new Resource[] { },
                        DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                        DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
        // TODo: fix this hardcoded timestamp update.
        getTimeGenerator().setTimestamp(new Date(getTimeGenerator().getTimeInMillis() + 80000));
        resourceCacheEntryBean.incrementRefreshCountRateLimited();
        recordGetFromResourceCache(resourceCacheKey, resourceCacheEntryBean);

        recordLogWarningIfEmptyCachedResources(
                getResourcePathTestData().getRequestedJspResourcePath(),
                getMockDevice(), resourceCacheEntryBean);

        replay();

        final List<Resource> actualResources =
                localObjectUnderTest.resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

        // Explicit verify since we are in a loop.
        verify();

        Assert.assertEquals("actualResources is wrong",
                Arrays.asList(new Resource [] {}),
                actualResources);

        assertResourceResolutionTreeNotUpdated();

        // Explicit reset since we are in a loop.
        reset();
    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey,
            final ResourceCacheEntry resourceCacheEntry) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                resourceCacheEntry);
    }

    private void recordGetMatchingGroups() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedJspResourcePath()))
                        .andReturn(getMockUiConfiguration());

        final Group[] matchingGroups =
                new Group[] { getGroupTestData().createIPhoneGroup(),
                getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData().getRequestedJspResourcePath(),
                        new Group[] { getGroupTestData().createIPhoneGroup(),
                    getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    /**
     * Return a {@link AbstractResourceResolver} subclass created with the given
     * deploymentMetadata and default values for every other argument.
     *
     * @param deploymentMetadata
     *            to construct the {@link AbstractResourceResolver} with.
     * @return a {@link AbstractResourceResolver} subclass created with the
     *         given deploymentMetadata and default values for every
     *         other argument.
     */
    protected abstract AbstractResourceResolver createWithDeploymentMetadata(
            DeploymentMetadata deploymentMetadata);

    /**
     * @return the groupTestData
     */
    protected GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the resolvedResourcePaths
     */
    protected ResourceAccumulator getResolvedResourcePaths() {
        return resolvedResourcePaths;
    }

    /**
     * @return the resourcePathTestData
     */
    protected ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    public ResourceResolutionWarnLogger getMockResourceResolutionWarnLogger() {
        return mockResourceResolutionWarnLogger;
    }

    public void setMockResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResourceResolutionWarnLogger) {
        this.mockResourceResolutionWarnLogger = mockResourceResolutionWarnLogger;
    }

    protected DeploymentMetadataTestData getDeploymentMetadataTestData() {
        return deploymentMetadataTestData;
    }

    protected DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }

    protected void setDeploymentMetadata(final DeploymentMetadata deploymentMetadata) {
        this.deploymentMetadata = deploymentMetadata;
    }

    /**
     * @return the resourcesRootDir
     */
    protected File getResourcesRootDir() {
        return resourcesRootDir;
    }

    /**
     * @return {@link ResourceAccumulatorFactory}
     */
    public ResourceAccumulatorFactory getMockResourceAccumulatorFactory() {
        return mockResourceAccumulatorFactory;
    }

    /**
     * @param mockResourceAccumulatorFactory
     *            {@link ResourceAccumulatorFactory}
     */
    public void setMockResourceAccumulatorFactory(
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {
        mockResourceAccumulatorFactory = resourceAccumulatorFactory;
    }

    /**
     * @param resourcesRootDir the resourcesRootDir to set
     */
    protected void setResourcesRootDir(final File resourcesRootDir) {
        this.resourcesRootDir = resourcesRootDir;
    }

    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }

    /**
     * @return the mockConfigurationFactory
     */
    public ConfigurationFactory getMockConfigurationFactory() {
        return mockConfigurationFactory;
    }

    /**
     * @param mockConfigurationFactory the mockConfigurationFactory to set
     */
    public void setMockConfigurationFactory(
            final ConfigurationFactory mockConfigurationFactory) {
        this.mockConfigurationFactory = mockConfigurationFactory;
    }

    /**
     * @return the mockUiConfiguration
     */
    public UiConfiguration getMockUiConfiguration() {
        return mockUiConfiguration;
    }

    /**
     * @param mockUiConfiguration the mockUiConfiguration to set
     */
    public void setMockUiConfiguration(final UiConfiguration mockUiConfiguration) {
        this.mockUiConfiguration = mockUiConfiguration;
    }

    /**
     * @return the mockDevice
     */
    public Device getMockDevice() {

        return mockDevice;
    }

    /**
     * @param mockDevice  the mockDevice to set
     */
    public void setMockDevice(final Device mockDevice) {

        this.mockDevice = mockDevice;
    }

    /**
     * @return the resourceResolverCommonParamHolder
     */
    public ResourceResolverCommonParamHolder getResourceResolverCommonParamHolder() {

        return resourceResolverCommonParamHolder;
    }

    /**
     * @param resourceResolverCommonParamHolder  the resourceResolverCommonParamHolder to set
     */
    public void setResourceResolverCommonParamHolder(
            final ResourceResolverCommonParamHolder resourceResolverCommonParamHolder) {

        this.resourceResolverCommonParamHolder = resourceResolverCommonParamHolder;
    }

    /**
     * @return the mockResourceCache
     */
    public ResourceCache getMockResourceCache() {
        return mockResourceCache;
    }

    /**
     * @param mockResourceCache the mockResourceCache to set
     */
    public void setMockResourceCache(final ResourceCache mockResourceCache) {
        this.mockResourceCache = mockResourceCache;
    }

    /**
     * @return the mockResourceAccumulator
     */
    public ResourceAccumulator getMockResourceAccumulator() {
        return mockResourceAccumulator;
    }

    /**
     * @param mockResourceAccumulator the mockResourceAccumulator to set
     */
    public void setMockResourceAccumulator(final ResourceAccumulator mockResourceAccumulator) {
        this.mockResourceAccumulator = mockResourceAccumulator;
    }

    protected void recordCheckResourceCache(final ResourceCacheKey resourceCacheKey,
            final Boolean resourceInCache) {
        EasyMock.expect(getMockResourceCache().contains(resourceCacheKey)).andReturn(
                resourceInCache);
    }

    protected void recordPutResourceCache(final ResourceCacheKey resourceCacheKey,
            final ResourceCacheEntry resourceCacheEntry) {
        getMockResourceCache().put(resourceCacheKey, resourceCacheEntry);
    }

    protected void recordPutResourceCache(final ResourceCacheKey resourceCacheKey,
            final Resource resource) {
        recordResourceCacheResourecsNotFoundProperties();
        getMockResourceCache().put(resourceCacheKey,
                new ResourceCacheEntryBean(new Resource[] { resource },
                        ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                        ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));
    }

    protected void recordPutResourceCache(final ResourceCacheKey resourceCacheKey,
            final Resource [] resources) {
        recordResourceCacheResourecsNotFoundProperties();
        getMockResourceCache().put(resourceCacheKey,
                new ResourceCacheEntryBean(resources,
                        ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                        ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));
    }

    protected void recordPutEmptyResultsIntoResourceCache(final ResourceCacheKey resourceCacheKey) {
        recordResourceCacheResourecsNotFoundProperties();
        getMockResourceCache()
        .put(resourceCacheKey, new ResourceCacheEntryBean(new Resource[] {},
                ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));
    }

    private void recordResourceCacheResourecsNotFoundProperties() {

        EasyMock.expect(getMockResourceCache().getResourcesNotFoundMaxRefreshCount()).andReturn(
                DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT);
        EasyMock
        .expect(getMockResourceCache().getResourcesNotFoundRefreshCountUpdateMilliseconds())
        .andReturn(DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
    }

    protected void recordLogWarningIfEmptyCachedResourcesToBeRefreshed(
            final String requestedResourcePath,
            final ResourceCacheEntryBean resourceCacheEntryBean) {
        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);
        getMockResourceResolutionWarnLogger().warn(
                "Empty cached resources found for requested resource '" + requestedResourcePath
                + "' and device " + getMockDevice() + " but refreshCount is "
                + resourceCacheEntryBean.getRefreshCount() + ". Will refresh the entry.");
    }

    protected void recordLogWarningIfEmptyResolvedResources(final String requestedResourcePath) {
        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);

        getMockResourceResolutionWarnLogger().warn(
                "No resource was found for requested resource '" + requestedResourcePath
                + "' and device " + getMockDevice());
    }

    protected void recordLogWarningIfEmptyCachedResources(final String requestedResourcePath,
            final Device device, final ResourceCacheEntry resourceCacheEntry) {
        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled()).andReturn(
                Boolean.TRUE);
        getMockResourceResolutionWarnLogger().warn(
                "Cached empty resources found and returned for requested resource '"
                        + requestedResourcePath + "' and device " + device + ". refreshCount is "
                        + resourceCacheEntry.getRefreshCount());
    }


    protected void assertResourceResolutionTreeUpdated(final List<Resource> resources) {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        int i = 0;
        for (final Resource currResource : resources) {
            Assert.assertTrue(
                    "ResourceResolutionTree treePreOrderIterator should have a next item "
                            + "for reource: " + currResource, treePreOrderIterator.hasNext());

            final ResourceTreeNode resourceTreeNode = treePreOrderIterator.next();
            Assert.assertNotNull("item from preOrderIterator should not be null for resource: "
                    + currResource, resourceTreeNode);
            Assert.assertEquals("item from preOrderIterator has wrong resource", currResource,
                    resourceTreeNode.getResource());
            i++;
        }

        Assert.assertFalse("There shouldn't be anymore ResourceTreeNodes",
                treePreOrderIterator.hasNext());
    }

    protected void assertResourceResolutionTreeNotUpdated() {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        Assert.assertFalse("ResourceResolutionTree treePreOrderIterator should not have any items",
                treePreOrderIterator.hasNext());
    }

    private AbstractResourceResolver createAbstractResourceResolver() {

        return new StubbedAbstractResourceResolver(getResourceResolverCommonParamHolder(),
                "crf", getResourcesRootDir());
    }


    private void setTimeGenerator(final TimerGeneratorStub timeGenerator) {
        this.timeGenerator = timeGenerator;
    }

    private TimerGeneratorStub getTimeGenerator() {
        return timeGenerator;
    }


    /**
     * Stubbed {@link AbstractResourceResolver}.
     */
    public static class StubbedAbstractResourceResolver extends AbstractResourceResolver {

        public StubbedAbstractResourceResolver(
                final ResourceResolverCommonParamHolder commonParams,
                final String abstractResourceExtension, final File rootResourcesDir) {
            super(commonParams, abstractResourceExtension, rootResourcesDir);
        }

        private static final Logger LOGGER
        = Logger.getLogger(StubbedAbstractResourceResolver.class);

        @Override
        protected List<Resource> doResolve(final String requestedResourcePath,
                final Device device) {
            return new ArrayList<Resource>();
        }

        @Override
        protected String getDebugResourceTypeName() {
            return "jsp";
        }

        @Override
        protected Logger getLogger() {
            return LOGGER;
        }

        @Override
        protected String getRealResourcePathExtension() {
            return ".jsp";
        }

        @Override
        protected String getResourceSubDirName() {
            return StringUtils.EMPTY;
        }
    }
}

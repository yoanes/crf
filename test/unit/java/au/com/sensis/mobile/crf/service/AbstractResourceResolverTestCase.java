package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.config.UiConfiguration;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Abstract base class for testing subclasses of {@link AbstractResourceResolver}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceResolverTestCase extends AbstractJUnit4TestCase {

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
    private final ResourceAccumulatorFactory resourceAccumulatorFactory =
        new ResourceAccumulatorFactory(true);
    private ResourceCache mockResourceCache;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUpAbstractBaseClass() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setResourcesRootDir(new File(getClass().getResource("/").toURI()));

        setDeploymentMetadata(getDeploymentMetadataTestData().createDevDeploymentMetadata());

        setResourceResolverCommonParamHolder(new ResourceResolverCommonParamHolder(
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(),
                getMockConfigurationFactory(),
                getMockResourceCache()));
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDownAbstractBaseClass() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();
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

    protected ResourceAccumulatorFactory getResourceAccumulatorFactory() {
        return resourceAccumulatorFactory;
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

    protected void recordCheckResourceCache(final ResourceCacheKey resourceCacheKey,
            final Boolean resourceInCache) {
        EasyMock.expect(getMockResourceCache().contains(resourceCacheKey)).andReturn(
                resourceInCache);
    }

    protected void recordPutResourceCache(final ResourceCacheKey resourceCacheKey,
            final Resource resource) {
        getMockResourceCache().put(EasyMock.eq(resourceCacheKey),
                EasyMock.aryEq(new Resource[] { resource }));
    }

    protected void recordPutResourceCache(final ResourceCacheKey resourceCacheKey,
            final Resource [] resources) {
        getMockResourceCache().put(EasyMock.eq(resourceCacheKey),
                EasyMock.aryEq(resources));
    }

    protected void recordPutEmptyResultsIntoResourceCache(final ResourceCacheKey resourceCacheKey) {
        getMockResourceCache()
                .put(EasyMock.eq(resourceCacheKey), EasyMock.aryEq(new Resource[] {}));
    }

}

package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Abstract base class for testing subclasses of {@link AbstractResourceResolver}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceResolverTestCase extends AbstractJUnit4TestCase {

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;
    private final DeploymentMetadataTestData deploymentMetadataTestData
        = new DeploymentMetadataTestData();
    private DeploymentMetadata deploymentMetadata;
    private File resourcesRootDir;
    private FileIoFacade mockFileIoFacade;

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

}

package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;

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

        setObjectUnderTest(new CssResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(), getDeploymentMetadata()));
    }

    @Override
    protected CssResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {
        return new CssResourceResolverBean(abstractResourceExtension,
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata());
    }

    @Override
    protected CssResourceResolverBean createWithRootResourcesDir(final File rootResourcesDir) {
        return new CssResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceResolutionWarnLogger(), getDeploymentMetadata());
    }

    @Override
    protected CssResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        return new CssResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                resourceResolutionWarnLogger, getDeploymentMetadata());
    }

    @Override
    protected CssResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {
        return new CssResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(), deploymentMetadata);
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupCssResourcePath()),
                            actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
    throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedCssResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            Assert.assertNotNull("actualResources should not be null",
                    actualResources);
            Assert.assertTrue("actualResources should be empty",
                    actualResources.isEmpty());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordCheckIfNewPathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData()
                        .getMappedIphoneGroupCssResourcePath()
                        .getNewPath())).andReturn(exists);

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<Resource> actualResources =
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedJspResourcePath(),
                    getGroupTestData().createIPhoneGroup(),
                    getResolvedResourcePaths());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

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

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
 * Unit test {@link PropertiesResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private PropertiesResourceResolverBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new PropertiesResourceResolverBean(
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata()));
    }

    @Override
    protected PropertiesResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {
        return new PropertiesResourceResolverBean(
                abstractResourceExtension,
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata());
    }

    @Override
    protected PropertiesResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {
        return new PropertiesResourceResolverBean(
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(),
                deploymentMetadata);
    }

    @Override
    protected PropertiesResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        return new PropertiesResourceResolverBean(
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                resourceResolutionWarnLogger,
                getDeploymentMetadata());
    }

    @Override
    protected PropertiesResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {
        return new PropertiesResourceResolverBean(
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                rootResourcesDir,
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata());
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues =
                { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedPropertiesResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            Assert.assertEquals("actualResources is wrong", Arrays.asList(getResourcePathTestData()
                    .getMappedIphoneGroupPropertiesResourcePath()), actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist() throws Throwable {
        final String[] testValues =
                { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedPropertiesResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            Assert.assertNotNull("actualResources should not be null", actualResources);
            Assert.assertTrue("actualResources should be empty", actualResources.isEmpty());

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
                                .getMappedIphoneGroupPropertiesResourcePath()
                                .getNewPath())).andReturn(exists);

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<Resource> actualResources =
                getObjectUnderTest()
                        .resolve(
                                getResourcePathTestData()
                                        .getRequestedJspResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

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

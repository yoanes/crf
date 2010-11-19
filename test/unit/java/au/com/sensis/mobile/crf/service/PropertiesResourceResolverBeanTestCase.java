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
                getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir()));
    }

    @Override
    protected PropertiesResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new PropertiesResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension,
                getResourcesRootDir());
    }

    @Override
    protected PropertiesResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new PropertiesResourceResolverBean(commonParams,
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir());
    }

    @Override
    protected PropertiesResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getResourceAccumulatorFactory(), getMockConfigurationFactory());

        return new PropertiesResourceResolverBean(commonParams,
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcesRootDir());
    }

    @Override
    protected PropertiesResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new PropertiesResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                rootResourcesDir);
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues =
        { getResourcePathTestData().getPropertiesExtensionWithoutLeadingDot(),
                getResourcePathTestData().getPropertiesExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupIterator();

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPropertiesResourcePath(),
                        getMockDevice());

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

            recordGetMatchingGroupIterator();

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPropertiesResourcePath(),
                        getMockDevice());

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
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedJspResourcePath(),
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
                        getResourcePathTestData().getRequestedPropertiesResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedJspResourcePath()));
    }


    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(getMockConfigurationFactory().getUiConfiguration(
                getResourcePathTestData().getRequestedPropertiesResourcePath())).andReturn(
                        getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
        .andReturn(matchingGroupsIterator);

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

package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;

/**
 * Unit test {@link JspResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private JspResourceResolverBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new JspResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getResourcePathTestData().getJspResourcesRootServletPath()));
    }

    @Override
    protected JspResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new JspResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension, getResourcesRootDir(),
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new JspResourceResolverBean(commonParams,
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {
        return new JspResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(), rootResourcesDir,
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new JspResourceResolverBean(commonParams,
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Test
    public void testConstructorWithBlankJspResourcesRootServletPath()
    throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new JspResourceResolverBean(getResourceResolverCommonParamHolder(),
                        getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                        getResourcesRootDir(), testValue);

                Assert
                .fail("IllegalArgumentException expected for testValue: '"
                        + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "jspResourcesRootServletPath must not be blank: '"
                        + testValue + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator();
            recordCheckIfNewIphonePathExists(Boolean.TRUE);

            recordPutResourceCache(resourceCacheKey,
                    getResourcePathTestData().getMappedIphoneGroupResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath()),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData().getRequestedJspResourcePath(),
                        new Group[] { getGroupTestData().createIPhoneGroup(),
                                getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey) {
        final Resource[] resources =
                new Resource[] { getResourcePathTestData().getMappedIphoneGroupResourcePath() };
        final ResourceCacheEntryBean resourceCacheEntryBean =
                new ResourceCacheEntryBean(resources,
                    ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                    ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                resourceCacheEntryBean);
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
    throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetMatchingGroupIterator();
            recordCheckIfNewIphonePathExists(Boolean.FALSE);
            recordCheckIfNewApplePathExists(Boolean.FALSE);

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);
            recordLogWarningIfEmptyResolvedResources(
                    getResourcePathTestData().getRequestedJspResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertNotNull("actualResources should not be null",
                    actualResources);
            Assert.assertTrue("actualResources should be empty",
                    actualResources.isEmpty());

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceFromCache() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroups();
            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            recordGetFromResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath()),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private void recordCheckIfNewIphonePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData().getMappedIphoneGroupResourcePath().getNewPath()))
                .andReturn(exists);

    }

    private void recordCheckIfNewApplePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData().getMappedAppleGroupResourcePath().getNewPath()))
                .andReturn(exists);

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<Resource> actualResources =
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedCssResourcePath(),
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
                        getResourcePathTestData().getRequestedJspResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
    }

    private void recordGetMatchingGroupIterator() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedJspResourcePath())).andReturn(
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
                        getResourcePathTestData().getRequestedJspResourcePath()))
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
    private JspResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest
     *            the objectUnderTest to set
     */
    private void setObjectUnderTest(final JspResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}

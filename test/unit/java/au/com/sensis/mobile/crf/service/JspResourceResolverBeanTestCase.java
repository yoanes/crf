package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;
import au.com.sensis.mobile.crf.debug.ResourceTreeNode;

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

        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree(true));
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree());
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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordCheckIfNewPathExists(Boolean.TRUE);

            recordPutResourceCache(resourceCacheKey,
                    getResourcePathTestData().getMappedIphoneGroupResourcePath());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath()),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private ResourceCacheKey createResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey = new ResourceCacheKeyBean(
                getResourcePathTestData().getRequestedJspResourcePath(),
                getGroupTestData().createIPhoneGroup());
        return resourceCacheKey;
    }

    private void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey) {
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                new Resource [] {getResourcePathTestData().getMappedIphoneGroupResourcePath()});
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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordCheckIfNewPathExists(Boolean.FALSE);

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            Assert.assertNotNull("actualResources should not be null",
                    actualResources);
            Assert.assertTrue("actualResources should be empty",
                    actualResources.isEmpty());

            assertResourceResolutionTreeNotUpdated();

            // Explicit verify and reset since we are in a loop.
            verify();
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

            recordGetMatchingGroupIterator();

            final ResourceCacheKey resourceCacheKey = createResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            recordGetFromResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath()),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

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
                        .getMappedIphoneGroupResourcePath()
                        .getNewPath())).andReturn(exists);

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

        EasyMock.expect(getMockConfigurationFactory().getUiConfiguration(
                getResourcePathTestData().getRequestedJspResourcePath())).andReturn(
                        getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
        .andReturn(matchingGroupsIterator);

    }

    private void assertResourceResolutionTreeNotUpdated() {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        Assert.assertFalse("ResourceResolutionTree treePreOrderIterator should not have any items",
                treePreOrderIterator.hasNext());
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

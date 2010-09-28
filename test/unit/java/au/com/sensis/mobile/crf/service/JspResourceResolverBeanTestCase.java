package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;

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
        setObjectUnderTest(new JspResourceResolverBean(
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(),
                getResourcePathTestData().getJspResourcesRootServletPath()));
    }

    @Override
    protected JspResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {
        return new JspResourceResolverBean(abstractResourceExtension,
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(), getResourcePathTestData()
                        .getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        return new JspResourceResolverBean(getResourcePathTestData()
                .getCrfExtensionWithoutLeadingDot(), getResourcesRootDir(),
                resourceResolutionWarnLogger, getDeploymentMetadata(),
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {
        return new JspResourceResolverBean(getResourcePathTestData()
                .getCrfExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceResolutionWarnLogger(), getDeploymentMetadata(),
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Override
    protected JspResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {
        return new JspResourceResolverBean(getResourcePathTestData()
                .getCrfExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(), deploymentMetadata,
                getResourcePathTestData().getJspResourcesRootServletPath());
    }

    @Test
    public void testConstructorWithBlankJspResourcesRootServletPath()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new JspResourceResolverBean(getResourcePathTestData()
                        .getCrfExtensionWithoutLeadingDot(),
                        getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        getDeploymentMetadata(),
                        testValue);

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

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            Assert.assertEquals("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath()),
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
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedJspResourcePath(),
                        getGroupTestData().createIPhoneGroup());

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
                                .getMappedIphoneGroupResourcePath()
                                .getNewPath())).andReturn(exists);

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<Resource> actualResources =
                getObjectUnderTest()
                        .resolve(
                                getResourcePathTestData()
                                        .getRequestedCssResourcePath(),
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
                        getResourcePathTestData().getRequestedJspResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
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

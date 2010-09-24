package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JspResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourceResolverBeanTestCase extends AbstractJUnit4TestCase {

    private JspResourceResolverBean objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;
    private FileIoFacade mockFileIoFacade;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new JspResourceResolverBean(
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(),
                getResourcePathTestData().getJspResourcesRootServletPath()));
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();
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
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new JspResourceResolverBean(testValue, getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        getResourcePathTestData()
                                .getJspResourcesRootServletPath());

                Assert
                        .fail("IllegalArgumentException expected for testValue: '"
                                + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "abstractResourceExtension must not be blank: '"
                                + testValue + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenRootResourcesDirInvalid() throws Throwable {
        final File [] invalidPaths = { new File(StringUtils.EMPTY), new File(" "),
                new File("  "), new File("I-do-not-exist"),
                new File(getClass().getResource(
                        "/au/com/sensis/mobile/crf/service/"
                        + "JspResourceResolverBeanTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new JspResourceResolverBean(
                        getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                        invalidPath, getMockResourceResolutionWarnLogger(),
                        getResourcePathTestData()
                        .getJspResourcesRootServletPath());
                Assert.fail("IllegalArgumentException expected for invalidPath: '"
                      + invalidPath + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message",
                        "rootResourcesDir must be a directory: '"
                                + invalidPath + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerIsNull()
            throws Throwable {
        try {
            new JspResourceResolverBean(
                    getResourcePathTestData()
                            .getCrfExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), null,
                    getResourcePathTestData().getJspResourcesRootServletPath());

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                            .getMessage());
        }

    }

    @Test
    public void testMapResourcePathWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JspResourceResolverBean(
                    testValue, getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                    getResourcePathTestData().getJspResourcesRootServletPath()));

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
    public void testMapResourcePathWhenMappingPerformedAndResourceDoesNotExist()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JspResourceResolverBean(
                    testValue, getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                    getResourcePathTestData().getJspResourcesRootServletPath()));

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
    public void testMapResourcePathWhenNoMappingPerformed() throws Throwable {
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

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the resourcesRootDir
     */
    private File getResourcesRootDir() {
        return getResourcePathTestData().getRootResourcesPath();
    }

    public ResourceResolutionWarnLogger getMockResourceResolutionWarnLogger() {
        return mockResourceResolutionWarnLogger;
    }

    public void setMockResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResourceResolutionWarnLogger) {
        this.mockResourceResolutionWarnLogger = mockResourceResolutionWarnLogger;
    }

    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }
}

package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
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
 * Unit test {@link JavaScriptResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBeanTestCase extends AbstractJUnit4TestCase {

    private static final String ABSTRACT_PATH_PACKAGE_KEYWORD = "package";

    private JavaScriptResourceResolverBean objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;
    private JavaScriptFileFinder mockJavaScriptFileFinder;
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

        setObjectUnderTest(new JavaScriptResourceResolverBean(
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                ABSTRACT_PATH_PACKAGE_KEYWORD,
                getMockJavaScriptFileFinder()));
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
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  "};
        for (final String testValue : testValues) {
            try {
                new JavaScriptResourceResolverBean(testValue, getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        ABSTRACT_PATH_PACKAGE_KEYWORD,
                        getMockJavaScriptFileFinder());

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "abstractResourceExtension must not be blank: '"
                                + testValue + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenResourcesRootPathInvalid() throws Throwable {
        final File [] invalidPaths = { new File(StringUtils.EMPTY), new File(" "),
                new File("  "), new File("I-do-not-exist"),
                new File(getClass().getResource(
                        "/au/com/sensis/mobile/crf/service/"
                        + "JavaScriptResourceResolverBeanTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new JavaScriptResourceResolverBean(
                        getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        invalidPath, getMockResourceResolutionWarnLogger(),
                        ABSTRACT_PATH_PACKAGE_KEYWORD,
                        getMockJavaScriptFileFinder());
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
            new JavaScriptResourceResolverBean(
                    getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), null,
                    ABSTRACT_PATH_PACKAGE_KEYWORD,
                    getMockJavaScriptFileFinder());

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                            .getMessage());
        }

    }

    @Test
    public void testConstructorWithBlankAbstractPathPackageKeyword()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  "};
        for (final String testValue : testValues) {
            try {
                new JavaScriptResourceResolverBean(
                        getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        testValue,
                        getMockJavaScriptFileFinder());

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "abstractPathPackageKeyword must not be blank: '"
                                + testValue + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenJavaScriptFileFinderIsNull()
    throws Throwable {
        try {
            new JavaScriptResourceResolverBean(
                    getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                    ABSTRACT_PATH_PACKAGE_KEYWORD,
                    null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "javaScriptFileFinder must not be null", e
                    .getMessage());
        }

    }

    @Test
    public void testResolveWhenPackageRequestedAndResourcesFound()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                    getMockJavaScriptFileFinder()));

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedIphoneGroupPackagedScriptBundleResourcePath()
                        .getNewFile())).andReturn(createExistsByFilterExpectedFileFilterResults());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedPackageScriptResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    createExistsByFilterExpectedResources(),
                        actualResources);
        }

    }

    @Test
    public void testResolveWhenPackageRequestedAndNoResourcesFound()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                    getMockJavaScriptFileFinder()));

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedIphoneGroupPackagedScriptBundleResourcePath()
                        .getNewFile())).andReturn(new ArrayList<File>());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedPackageScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                    actualResources);
        }

    }

    private List<File> createExistsByFilterExpectedFileFilterResults() {
        final File expectedFile1 =
                getResourcePathTestData()
                        .getMappedDefaultGroupPackagedScriptResourcePath1()
                        .getNewFile();
        final File expectedFile2 =
                getResourcePathTestData()
                        .getMappedDefaultGroupPackagedScriptResourcePath2()
                        .getNewFile();
        return Arrays.asList(expectedFile1, expectedFile2);
    }

    private List<Resource> createExistsByFilterExpectedResources() {
        return Arrays.asList(
            getResourcePathTestData()
                    .getMappedDefaultGroupPackagedScriptResourcePath1(),
            getResourcePathTestData()
                    .getMappedDefaultGroupPackagedScriptResourcePath2());
    }

    @Test
    public void testResolveWhenPackageNotRequestedAndResourcesFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                    getMockJavaScriptFileFinder()));

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupNamedScriptResourcePath()),
                    actualResources);
        }

    }

    @Test
    public void testResolveWhenPackageNotRequestedAndNoResourcesFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                    getMockJavaScriptFileFinder()));

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                            actualResources);
        }

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
    public void testSupportsTrueWhenPackageRequested() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath()));
    }

    @Test
    public void testSupportsTrueWhenPackageNotRequested() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
    }


    private void recordCheckIfNewPathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData()
                                .getMappedIphoneGroupNamedScriptResourcePath()
                                .getNewPath())).andReturn(exists);

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
     * @return the objectUnderTest
     */
    private JavaScriptResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JavaScriptResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
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

    public JavaScriptFileFinder getMockJavaScriptFileFinder() {
        return mockJavaScriptFileFinder;
    }

    public void setMockJavaScriptFileFinder(
            final JavaScriptFileFinder mockJavaScriptFileFinder) {
        this.mockJavaScriptFileFinder = mockJavaScriptFileFinder;
    }

    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }


    @Override
    public void verify() {
        // Override to prevent auto verify.
    }
}

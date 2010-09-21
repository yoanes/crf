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
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JavaScriptResourcePathMapper}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourcePathMapperTestCase extends AbstractJUnit4TestCase {

    private JavaScriptResourcePathMapper objectUnderTest;

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

        setObjectUnderTest(new JavaScriptResourcePathMapper(
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger()));

        // TODO: get rid of setter injection.
        getObjectUnderTest().setPathExpander(getMockJavaScriptFileFinder());
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
                new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger());

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
                        + "JavaScriptResourcePathMapperTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new JavaScriptResourcePathMapper(
                        getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        invalidPath, getMockResourceResolutionWarnLogger());
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
            new JavaScriptResourcePathMapper(
                    getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                            .getMessage());
        }

    }

    @Test
    public void testResolveWhenBundleRequestedAndResourcesFound()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            // TODO: get rid of setter injection.
            getObjectUnderTest().setPathExpander(getMockJavaScriptFileFinder());


            EasyMock.expect(getMockJavaScriptFileFinder().findJavaScriptFiles(
                    getResourcePathTestData().getMappedIphoneGroupBundledScriptBundleResourcePath()
                        .getBundleParentDirFile())).andReturn(
                    createExistsByFilterExpectedFileFilterResults());

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedBundledScriptResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                    createExistsByFilterExpectedMappedResourcePaths(),
                        actualMappedResourcePaths);
        }

    }

    @Test
    public void testResolveWhenBundleRequestedAndNoResourcesFound()
        throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            // TODO: get rid of setter injection.
            getObjectUnderTest().setPathExpander(getMockJavaScriptFileFinder());


            EasyMock.expect(getMockJavaScriptFileFinder().findJavaScriptFiles(
                    getResourcePathTestData().getMappedIphoneGroupBundledScriptBundleResourcePath()
                    .getBundleParentDirFile())).andReturn(
                            new ArrayList<File>());

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedBundledScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                    new ArrayList<MappedResourcePath>(),
                    actualMappedResourcePaths);
        }

    }

    private List<File> createExistsByFilterExpectedFileFilterResults() {
        final File expectedFile1 =
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath1().getNewResourceFile();
        final File expectedFile2 =
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath2().getNewResourceFile();
        return Arrays.asList(expectedFile1, expectedFile2);
    }

    private List<MappedResourcePath> createExistsByFilterExpectedMappedResourcePaths() {
        return Arrays.asList(
            getResourcePathTestData()
                    .getMappedDefaultGroupBundledScriptResourcePath1(),
            getResourcePathTestData()
                    .getMappedDefaultGroupBundledScriptResourcePath2());
    }

    @Test
    public void testResolveWhenBundleNotRequestedAndResourcesFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordCheckIfNewResourcePathExists(Boolean.TRUE);

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupNamedScriptResourcePath()),
                    actualMappedResourcePaths);
        }

    }

    @Test
    public void testResolveWhenBundleNotRequestedAndNoResourcesFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordCheckIfNewResourcePathExists(Boolean.FALSE);

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                    new ArrayList<MappedResourcePath>(),
                            actualMappedResourcePaths);
        }

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest()
                        .resolve(
                                getResourcePathTestData()
                                        .getRequestedJspResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertNotNull("actualMappedResourcePaths should not be null",
                actualMappedResourcePaths);
        Assert.assertTrue("actualMappedResourcePaths should be empty",
                actualMappedResourcePaths.isEmpty());

    }

    private void recordCheckIfNewResourcePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData()
                                .getMappedIphoneGroupNamedScriptResourcePath()
                                .getNewResourcePath())).andReturn(exists);

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
    private JavaScriptResourcePathMapper getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JavaScriptResourcePathMapper objectUnderTest) {
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
}

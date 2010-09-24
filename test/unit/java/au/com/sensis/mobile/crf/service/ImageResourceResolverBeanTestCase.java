package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
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
 * Unit test {@link ImageResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourceResolverBeanTestCase extends AbstractJUnit4TestCase {

    private static final String[] FILE_EXTENSION_WILDCARDS = new String[] { "*" };

    private ImageResourceResolverBean objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private File resourcesRootDir;
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

        setResourcesRootDir(new File(getClass().getResource("/").toURI()));

        setObjectUnderTest(new ImageResourceResolverBean(
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                FILE_EXTENSION_WILDCARDS));
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
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new ImageResourceResolverBean(testValue, getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        FILE_EXTENSION_WILDCARDS);

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
                        + "ImageResourceResolverBeanTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new ImageResourceResolverBean(
                        getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        invalidPath, getMockResourceResolutionWarnLogger(),
                        FILE_EXTENSION_WILDCARDS);
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
            new ImageResourceResolverBean(getResourcePathTestData()
                    .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                    null, FILE_EXTENSION_WILDCARDS);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                            .getMessage());
        }

    }

    @Test
    public void testConstructorWhenFileExtensionWildcardsIsInvalid()
    throws Throwable {
        final List<String []> testVaues = Arrays.asList(
                null,
                new String [] {},
                new String [] { null },
                new String [] { StringUtils.EMPTY },
                new String [] { " " },
                new String [] { "  " }
        );

        for (final String [] testValue : testVaues) {
            try {
                new ImageResourceResolverBean(getResourcePathTestData()
                        .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(), testValue);

                Assert.fail("IllegalArgumentException expected for testValue: '"
                        + ArrayUtils.toString(testValue) + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals("IllegalArgumentException has wrong message",
                        "fileExtensionWildcards must be an array of non-blank Strings but was: '"
                        + ArrayUtils.toString(testValue) + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndSingleResourceFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), FILE_EXTENSION_WILDCARDS));

            recordListFilesByExtension(getSingleMatchedPngImageArray());

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedImageResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                        actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private File[] getSingleMatchedPngImageArray() {
        return new File[] {
                getMappedIPhonePngImageResourcePath().getNewFile() };
    }

    private File[] getMultipleMatchedPngImageArray() {
        return new File[] {
                        getMappedIPhonePngImageResourcePath().getNewFile(),
                        getMappedIPhoneGroupGifImageResourcePath().getNewFile() };
    }

    private Resource getMappedIPhonePngImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupPngImageResourcePath();
    }

    private Resource getMappedIPhoneGroupGifImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupGifImageResourcePath();
    }

    @Test
    public void testResolveWhenMappingPerformedAndMultipleResourcesFound() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), FILE_EXTENSION_WILDCARDS));

            recordListFilesByExtension(getMultipleMatchedPngImageArray());

            recordLogWarningResolveToSingleFoundMultipleResources();

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedImageResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                        actualResources);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    private void recordLogWarningResolveToSingleFoundMultipleResources() {
        EasyMock.expect(getMockResourceResolutionWarnLogger().isWarnEnabled())
            .andReturn(Boolean.TRUE);

        getMockResourceResolutionWarnLogger()
            .warn("Requested resource '"
                    + getResourcePathTestData().getRequestedImageResourcePath()
                    + "' resolved to multiple real resources with extensions matching "
                    + ArrayUtils.toString(FILE_EXTENSION_WILDCARDS)
                    + ". Will only return the first resource. Total found: ["
                    + getMappedIPhonePngImageResourcePath().getNewFile()
                    + ", " + getMappedIPhoneGroupGifImageResourcePath().getNewFile()
                    + "].");
    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourceResolverBean(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger(), FILE_EXTENSION_WILDCARDS));

            recordListFilesByExtension(new File[] {});

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedImageResourcePath(),
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

    private void recordListFilesByExtension(final File[] files) {
        EasyMock.expect(
                getMockFileIoFacade().list(
                        EasyMock.eq(getRootResourcesDir()),
                        EasyMock.eq(getMappedIphoneGroupImageResourcePath()
                                .getNewPath()),
                        EasyMock.aryEq(FILE_EXTENSION_WILDCARDS))).andReturn(
                files);

    }

    private Resource getMappedIphoneGroupImageResourcePath() {
        return getResourcePathTestData()
        .getMappedIphoneGroupImageResourcePath();
    }

    private Resource getMappedIphoneGroupPngImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupPngImageResourcePath();
    }

    private File getRootResourcesDir() {
        return getResourcePathTestData().getRootResourcesPath();
    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {
        final List<Resource> actualResources =
                getObjectUnderTest()
                        .resolve(
                                getResourcePathTestData()
                                        .getRequestedImageResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

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
    private ImageResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the resourcesRootDir
     */
    private File getResourcesRootDir() {
        return resourcesRootDir;
    }

    /**
     * @param resourcesRootDir the resourcesRootDir to set
     */
    private void setResourcesRootDir(final File resourcesRootDir) {
        this.resourcesRootDir = resourcesRootDir;
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

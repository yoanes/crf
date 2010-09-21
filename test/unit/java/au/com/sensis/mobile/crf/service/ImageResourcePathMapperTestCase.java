package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImageResourcePathMapper}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourcePathMapperTestCase extends AbstractJUnit4TestCase {

    private static final String[] FILE_EXTENSION_WILDCARDS = new String[] { "*" };

    private ImageResourcePathMapper objectUnderTest;

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

        setObjectUnderTest(new ImageResourcePathMapper(
                getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger()));
    }

    @Test
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new ImageResourcePathMapper(testValue, getResourcesRootDir(),
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
                        + "ImageResourcePathMapperTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new ImageResourcePathMapper(
                        getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
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
            new ImageResourcePathMapper(getResourcePathTestData()
                    .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                    null);

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
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordListFilesByExtension(
                new File[] { getMappedIphoneGroupPngImageResourcePath().getNewResourceFile() });

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedImageResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                    Arrays.asList(getMappedIphoneGroupPngImageResourcePath()),
                        actualMappedResourcePaths);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testMapResourcePathWhenMappingPerformedAndResourceDoesNotExist()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordListFilesByExtension(new File[] {});

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedImageResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            Assert.assertNotNull("actualMappedResourcePaths should not be null",
                    actualMappedResourcePaths);
            Assert.assertTrue("actualMappedResourcePaths should be empty",
                    actualMappedResourcePaths.isEmpty());

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
                                .getNewResourcePath()),
                        EasyMock.aryEq(FILE_EXTENSION_WILDCARDS))).andReturn(
                files);

    }

    private MappedResourcePath getMappedIphoneGroupImageResourcePath() {
        return getResourcePathTestData()
        .getMappedIphoneGroupImageResourcePath();
    }

    private MappedResourcePath getMappedIphoneGroupPngImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupPngImageResourcePath();
    }

    private File getRootResourcesDir() {
        return getResourcePathTestData().getRootResourcesPath();
    }

    @Test
    public void testMapResourcePathWhenNoMappingPerformed() throws Throwable {
        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest()
                        .resolve(
                                getResourcePathTestData()
                                        .getRequestedImageResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertNotNull("actualMappedResourcePaths should not be null",
                actualMappedResourcePaths);
        Assert.assertTrue("actualMappedResourcePaths should be empty",
                actualMappedResourcePaths.isEmpty());

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
    private ImageResourcePathMapper getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageResourcePathMapper objectUnderTest) {
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

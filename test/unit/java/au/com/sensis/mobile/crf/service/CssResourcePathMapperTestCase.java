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
 * Unit test {@link CssResourcePathMapper}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CssResourcePathMapperTestCase extends AbstractJUnit4TestCase {

    private CssResourcePathMapper objectUnderTest;

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

        setObjectUnderTest(new CssResourcePathMapper(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceResolutionWarnLogger()));
    }

    @Test
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new CssResourcePathMapper(testValue, getResourcesRootDir(),
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
        final File[] invalidPaths =
                {
                        new File(StringUtils.EMPTY),
                        new File(" "),
                        new File("  "),
                        new File("I-do-not-exist"),
                        new File(
                                getClass()
                                        .getResource(
                                                "/au/com/sensis/mobile/crf/service/"
                                                        + "CssResourcePathMapperTestCase.class")
                                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new CssResourcePathMapper(getResourcePathTestData()
                        .getCssExtensionWithoutLeadingDot(), invalidPath,
                        getMockResourceResolutionWarnLogger());
                Assert
                        .fail("IllegalArgumentException expected for invalidPath: '"
                                + invalidPath + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message",
                        "rootResourcesDir must be a directory: '" + invalidPath
                                + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerIsNull()
            throws Throwable {
        try {
            new CssResourcePathMapper(getResourcePathTestData()
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
    public void testResolveWhenMappingPerformedAndResourceExists() throws Throwable {
        final String[] testValues =
                {
                        getResourcePathTestData()
                                .getCssExtensionWithLeadingDot(),
                        getResourcePathTestData()
                                .getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new CssResourcePathMapper(testValue,
                    getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordCheckIfNewResourcePathExists(Boolean.TRUE);

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData()
                                    .getRequestedCssResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            Assert.assertEquals("actualMappedResourcePaths is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupCssResourcePath()),
                    actualMappedResourcePaths);

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }

    }

    @Test
    public void testResolveWhenMappingPerformedAndResourceDoesNotExist()
        throws Throwable {
        final String[] testValues =
        {
                getResourcePathTestData()
                .getCssExtensionWithLeadingDot(),
                getResourcePathTestData()
                .getCssExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new CssResourcePathMapper(testValue,
                    getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            recordCheckIfNewResourcePathExists(Boolean.FALSE);

            replay();

            final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(
                        getResourcePathTestData()
                        .getRequestedCssResourcePath(),
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

    private void recordCheckIfNewResourcePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData()
                                .getMappedIphoneGroupCssResourcePath()
                                .getNewResourcePath())).andReturn(exists);

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
    private CssResourcePathMapper getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final CssResourcePathMapper objectUnderTest) {
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

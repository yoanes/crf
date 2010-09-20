package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
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
    private File resourcesRootDir;
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setResourcesRootDir(new File(getClass().getResource("/").toURI()));

        setObjectUnderTest(new JavaScriptResourcePathMapper(
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger()));
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
    public void testMapResourcePathWhenBundleRequestedAndMappingPerformed() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            final MappedResourcePath actualMappedResourcePath =
                    getObjectUnderTest().mapResourcePath(
                            getResourcePathTestData()
                                    .getRequestedBundledScriptResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("mappedResourcePath is wrong",
                    getResourcePathTestData().getMappedIphoneGroupBundledScriptBundleResourcePath(),
                        actualMappedResourcePath);
        }

    }

    @Test
    public void testMapResourcePathWhenBundleNotRequestedMappingPerformed() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JavaScriptResourcePathMapper(testValue, getResourcesRootDir(),
                    getMockResourceResolutionWarnLogger()));

            final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().mapResourcePath(
                        getResourcePathTestData()
                        .getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("mappedResourcePath is wrong",
                    getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath(),
                    actualMappedResourcePath);
        }

    }

    @Test
    public void testMapResourcePathWhenNoMappingPerformed() throws Throwable {
        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest()
                        .mapResourcePath(
                                getResourcePathTestData()
                                        .getRequestedJspResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertNull("mappedResourcePath is wrong", actualMappedResourcePath);

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

}

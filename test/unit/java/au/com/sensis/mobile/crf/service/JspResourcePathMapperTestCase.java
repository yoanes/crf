package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JspResourcePathMapper}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourcePathMapperTestCase extends AbstractJUnit4TestCase {

    private JspResourcePathMapper objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private ResourceResolutionWarnLogger mockResourceResolutionWarnLogger;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new JspResourcePathMapper(
                getResourcePathTestData().getJspResourcesRootServletPath(),
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceResolutionWarnLogger()));
    }

    @Test
    public void testConstructorWithBlankJspResourcesRootServletPath()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new JspResourcePathMapper(testValue, getResourcePathTestData()
                        .getCrfExtensionWithoutLeadingDot(),
                        getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger());

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
                new JspResourcePathMapper(getResourcePathTestData()
                        .getJspResourcesRootServletPath(), testValue,
                        getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger());

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
                        + "JspResourcePathMapperTestCase.class")
                        .toURI()) };
        for (final File invalidPath : invalidPaths) {
            try {
                new JspResourcePathMapper(getResourcePathTestData()
                        .getJspResourcesRootServletPath(),
                        getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
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
            new JspResourcePathMapper(getResourcePathTestData()
                    .getJspResourcesRootServletPath(),
                    getResourcePathTestData()
                            .getCrfExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e
                            .getMessage());
        }

    }

    @Test
    public void testMapResourcePathWhenMappingPerformed() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getCrfExtensionWithoutLeadingDot(),
                getResourcePathTestData().getCrfExtensionWithLeadingDot()
        };

        for (final String testValue : testValues) {
            setObjectUnderTest(new JspResourcePathMapper(
                    getResourcePathTestData().getJspResourcesRootServletPath(),
                    testValue, getResourcesRootDir(), getMockResourceResolutionWarnLogger()));

            final MappedResourcePath actualMappedResourcePath =
                    getObjectUnderTest().mapResourcePath(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            Assert.assertEquals("mappedResourcePath is wrong",
                    getResourcePathTestData().getMappedIphoneGroupResourcePath(),
                    actualMappedResourcePath);
        }

    }

    @Test
    public void testMapResourcePathWhenNoMappingPerformed() throws Throwable {
        final NullMappedResourcePath expectedMappedResourcePath =
                new NullMappedResourcePath(getResourcePathTestData()
                        .getRequestedCssResourcePath());
        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest()
                        .mapResourcePath(
                                getResourcePathTestData()
                                        .getRequestedCssResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertEquals("mappedResourcePath is wrong",
                expectedMappedResourcePath, actualMappedResourcePath);

    }

    /**
     * @return the objectUnderTest
     */
    private JspResourcePathMapper getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest
     *            the objectUnderTest to set
     */
    private void setObjectUnderTest(final JspResourcePathMapper objectUnderTest) {
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
}

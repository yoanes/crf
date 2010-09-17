package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
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

    private ImageResourcePathMapper objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private File resourcesRootDir;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setResourcesRootDir(new File(getClass().getResource("/").toURI()));

        setObjectUnderTest(new ImageResourcePathMapper(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir()));
    }

    @Test
    public void testConstructorWithBlankAbstractResourceExtension()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  "};
        for (final String testValue : testValues) {
            try {
                new ImageResourcePathMapper(testValue, getResourcesRootDir());

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
                        invalidPath);
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
    public void testMapResourcePathWhenMappingPerformed() throws Throwable {
        final String[] testValues = {
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcePathTestData().getAbstractImageExtensionWithoutLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(new ImageResourcePathMapper(testValue, getResourcesRootDir()));

            final MappedResourcePath actualMappedResourcePath =
                    getObjectUnderTest().mapResourcePath(
                            getResourcePathTestData()
                                    .getRequestedImageResourcePath(),
                            getGroupTestData().createIPhoneGroup());

            assertComplexObjectsEqual("mappedResourcePath is wrong",
                    getResourcePathTestData().getMappedIphoneGroupImageResourcePath(),
                        actualMappedResourcePath);
        }

    }

    @Test
    public void testMapResourcePathWhenNoMappingPerformed() throws Throwable {
        final NullMappedResourcePath expectedMappedResourcePath =
                new NullMappedResourcePath(getResourcePathTestData()
                        .getRequestedImageResourcePath());
        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest()
                        .mapResourcePath(
                                getResourcePathTestData()
                                        .getRequestedImageResourcePath(),
                                getGroupTestData().createIPhoneGroup());

        Assert.assertEquals("mappedResourcePath is wrong",
                expectedMappedResourcePath, actualMappedResourcePath);

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
}

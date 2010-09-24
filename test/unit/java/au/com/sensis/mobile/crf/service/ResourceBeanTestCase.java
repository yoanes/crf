package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link Resource}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceBeanTestCase extends AbstractJUnit4TestCase {

    private Resource objectUnderTest;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private FileIoFacade mockFileIoFacade;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());
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
    public void testConstructorWhenOriginalPathIsBlank()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new ResourceBean(testValue, getResourcePathTestData()
                        .getMappedDefaultGroupResourcePath()
                        .getNewPath(), getResourcePathTestData()
                        .getRootResourcesPath());

                Assert.fail("IllegalArgumentException expected for testValue '"
                        + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testValue '"
                                + testValue + "'",
                        "originalPath must not be blank: '" + testValue
                                + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testConstructorWhenNewPathIsBlank() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new ResourceBean(getResourcePathTestData()
                        .getMappedDefaultGroupResourcePath()
                        .getOriginalPath(), testValue,
                        getResourcePathTestData().getRootResourcesPath());

                Assert.fail("IllegalArgumentException expected for testValue '"
                        + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testValue '"
                                + testValue + "'",
                        "newPath must not be blank: '" + testValue
                                + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testEndsWithDotNullWhenTrue() throws Throwable {
        final Resource resource =
                getResourcePathTestData().getDotNullMappedImageResourcePath();
        setObjectUnderTest(resource);

        replay();

        Assert.assertTrue("endsWithDotNull() should return true", getObjectUnderTest()
                .newPathEndsWithDotNull());

    }

    @Test
    public void testEndsWithDotNullWhenFalse() throws Throwable {
        final Resource resource =
            getResourcePathTestData().getMappedDefaultGroupImageResourcePath();
        setObjectUnderTest(resource);

        replay();

        Assert.assertFalse("endsWithDotNull() should return false", getObjectUnderTest()
                .newPathEndsWithDotNull());
    }

    @Test
    public void testGetNewResourceFile() throws Throwable {
        final Resource resource =
                getResourcePathTestData()
                        .getMappedDefaultGroupImageResourcePath();
        setObjectUnderTest(resource);

        replay();

        Assert.assertEquals("getNewResourceFile() is wrong",
                new File(resource.getRootResourceDir(),
                        resource.getNewPath()),
                getObjectUnderTest().getNewFile());
    }

    /**
     * @return the objectUnderTest
     */
    private Resource getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final Resource objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockFileIoFacade
     */
    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    /**
     * @param mockFileIoFacade the mockFileIoFacade to set
     */
    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }
}

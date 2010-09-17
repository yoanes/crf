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

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link MappedResourcePath}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MappedResourcePathBeanTestCase extends AbstractJUnit4TestCase {

    private MappedResourcePath objectUnderTest;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private FileIoFacade mockFileIoFacade;

    public MappedResourcePathBeanTestCase() {
    }

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
    public void testConstructorWhenOriginalResourcePathIsBlank()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new MappedResourcePathBean(testValue, getResourcePathTestData()
                        .getMappedDefaultGroupResourcePath()
                        .getNewResourcePath(), getResourcePathTestData()
                        .getRootResourcesPath());

                Assert.fail("IllegalArgumentException expected for testValue '"
                        + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testValue '"
                                + testValue + "'",
                        "originalResourcePath must not be blank: '" + testValue
                                + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testConstructorWhenNewResourcePathIsBlank() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new MappedResourcePathBean(getResourcePathTestData()
                        .getMappedDefaultGroupResourcePath()
                        .getOriginalResourcePath(), testValue,
                        getResourcePathTestData().getRootResourcesPath());

                Assert.fail("IllegalArgumentException expected for testValue '"
                        + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testValue '"
                                + testValue + "'",
                        "newResourcePath must not be blank: '" + testValue
                                + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testIsIdentityMappingWhenTrue() throws Throwable {
        setObjectUnderTest(new MappedResourcePathBean(getResourcePathTestData()
                .getRequestedJspResourcePath(), getResourcePathTestData()
                .getRequestedJspResourcePath(),
                getResourcePathTestData().getRootResourcesPath()));

        Assert.assertTrue("isIdentityMapping() should be true",
                getObjectUnderTest().isIdentityMapping());
    }

    @Test
    public void testIsIdentityMappingWhenFalse() throws Throwable {
        setObjectUnderTest(getResourcePathTestData().getMappedIphoneGroupResourcePath());

        Assert.assertFalse("isIdentityMapping() should be false",
                getObjectUnderTest().isIdentityMapping());
    }

    @Test
    public void testExistsWhenTrue() throws Throwable {
        final MappedResourcePath mappedResourcePath
            = getResourcePathTestData().getMappedIphoneGroupResourcePath();
        setObjectUnderTest(mappedResourcePath);

        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        mappedResourcePath.getNewResourcePath()))
                .andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("exists() should return true", getObjectUnderTest().exists());

    }

    @Test
    public void testExistsWhenFalse() throws Throwable {
        final MappedResourcePath mappedResourcePath
            = getResourcePathTestData().getMappedIphoneGroupResourcePath();
        setObjectUnderTest(mappedResourcePath);

        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        mappedResourcePath.getNewResourcePath()))
                .andReturn(Boolean.FALSE);

        replay();

        Assert.assertFalse("exists() should return true", getObjectUnderTest().exists());

    }

    @Test
    public void testResolveToSingleWhenFound() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData().getMappedIphoneGroupResourcePath();
        setObjectUnderTest(mappedResourcePath);

        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        mappedResourcePath.getNewResourcePath())).andReturn(
                Boolean.TRUE);

        replay();

        Assert.assertEquals("resolveToSingle() should return 'this'", getObjectUnderTest(),
                getObjectUnderTest().resolveToSingle());

    }

    @Test
    public void testResolveToSingleWhenNotFound() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData().getMappedIphoneGroupResourcePath();
        setObjectUnderTest(mappedResourcePath);

        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        mappedResourcePath.getNewResourcePath())).andReturn(
                Boolean.FALSE);

        replay();

        Assert.assertNull("resolveToSingle() should be null", getObjectUnderTest()
                .resolveToSingle());

    }

    @Test
    public void testExistWithExtensions() throws Throwable {
        final String[] extensions = { "png", "jpg" };

        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData()
                        .getMappedDefaultGroupImageResourcePath();

        EasyMock.expect(
                getMockFileIoFacade().list(
                        getResourcePathTestData().getRootResourcesPath(),
                        mappedResourcePath.getNewResourcePath(), extensions))
                .andReturn(
                        getMatchedPngImageArray());
        replay();

        final List<MappedResourcePath> expectedMappedResourcePaths =
                Arrays.asList(getResourcePathTestData()
                        .getMappedDefaultGroupPngImageResourcePath());
        final List<MappedResourcePath> actualMappedResourcePaths =
                mappedResourcePath.existWithExtensions(extensions);

        assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                expectedMappedResourcePaths, actualMappedResourcePaths);
    }

    private File[] getMatchedPngImageArray() {
        return new File[] { new File(getResourcePathTestData()
                .getRootResourcesPath(), getResourcePathTestData()
                .getMappedDefaultGroupPngImageResourcePath()
                .getNewResourcePath()) };
    }

    @Test
    public void testEndsWithDotNullWhenTrue() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData().getDotNullMappedImageResourcePath();
        setObjectUnderTest(mappedResourcePath);

        replay();

        Assert.assertTrue("endsWithDotNull() should return true", getObjectUnderTest()
                .endsWithDotNull());

    }

    @Test
    public void testEndsWithDotNullWhenFalse() throws Throwable {
        final MappedResourcePath mappedResourcePath =
            getResourcePathTestData().getMappedDefaultGroupImageResourcePath();
        setObjectUnderTest(mappedResourcePath);

        replay();

        Assert.assertFalse("endsWithDotNull() should return false", getObjectUnderTest()
                .endsWithDotNull());
    }

    @Test
    public void testGetNewResourceFile() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData()
                        .getMappedDefaultGroupImageResourcePath();
        setObjectUnderTest(mappedResourcePath);

        replay();

        Assert.assertEquals("getNewResourceFile() is wrong",
                new File(mappedResourcePath.getRootResourceDir(),
                        mappedResourcePath.getNewResourcePath()),
                getObjectUnderTest().getNewResourceFile());
    }

    @Test
    public void testIsBundlePathWhenTrue() throws Throwable {
        final String[] testValues =
                { "default/util/bundle/bundle-all.js",
                        "default/util/bundle/main.js",
                        "iphone/bundle/bundle-all.js", "iphone/bundle/main.js" };

        for (final String testValue : testValues) {
            final MappedResourcePath mappedResourcePath =
                    new MappedResourcePathBean(getResourcePathTestData()
                            .getRequestedNamedScriptResourcePath(), testValue,
                            getResourcePathTestData().getRootResourcesPath());
            setObjectUnderTest(mappedResourcePath);

            Assert.assertTrue("isBundlePath() is wrong for testValue: '"
                    + testValue + "'", getObjectUnderTest().isBundlePath());
        }
    }

    @Test
    public void testIsBundlePathWhenFalse() throws Throwable {
        final String[] testValues = { "default/util/bundl/bundle-all.js",
            "default/util/bundl/main.js", "iphone/bundl/bundle-all.js",
            "iphone/bundl/main.js", "default/util.js" };

        for (final String testValue : testValues) {
            final MappedResourcePath mappedResourcePath =
                    new MappedResourcePathBean(getResourcePathTestData()
                            .getRequestedNamedScriptResourcePath(), testValue,
                            getResourcePathTestData().getRootResourcesPath());
            setObjectUnderTest(mappedResourcePath);

            Assert.assertFalse("isBundlePath() is wrong for testValue: '"
                    + testValue + "'", getObjectUnderTest().isBundlePath());
        }
    }

    @Test
    public void testGetBundleParentDirWhenIsBundlePathTrue() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData()
                        .getMappedDefaultGroupBundledScriptBundleResourcePath();

        Assert.assertEquals("getBundleParentDir() is wrong",
                new File(mappedResourcePath.getRootResourceDir(), "default/util"),
                mappedResourcePath.getBundleParentDirFile());
    }

    @Test
    public void testGetBundleParentDirWhenIsBundlePathFalse() throws Throwable {
        final MappedResourcePath mappedResourcePath =
                getResourcePathTestData()
                        .getMappedDefaultGroupNamedScriptResourcePath();

        try {
            mappedResourcePath.getBundleParentDirFile();

            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {

            Assert.assertEquals("IllegalStateException has wrong message",
                    "Illegal to call this method when isBundlePath() is false.",
                    e.getMessage());
        }
    }

    /**
     * @return the objectUnderTest
     */
    private MappedResourcePath getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final MappedResourcePath objectUnderTest) {
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

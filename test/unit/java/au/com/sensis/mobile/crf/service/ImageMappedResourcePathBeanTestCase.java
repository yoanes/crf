package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImageMappedResourcePathBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageMappedResourcePathBeanTestCase extends AbstractJUnit4TestCase {

    private ImageMappedResourcePathBean objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private FileIoFacade mockFileIoFacade;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    private static final String[] FILE_EXTENSIONS = { "*" };

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
    public void testResolveWhenSingleResourceFound() throws Throwable {
        setObjectUnderTest((ImageMappedResourcePathBean) getResourcePathTestData()
                .getMappedIphoneGroupImageResourcePath());
        getObjectUnderTest().setResourceResolutionWarnLogger(getMockResolutionWarnLogger());

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesPath()),
                        EasyMock.eq(getObjectUnderTest().getNewResourcePath()),
                        EasyMock.aryEq(FILE_EXTENSIONS))).andReturn(
                getSingleMatchedPngImageArray());
        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve();

        assertComplexObjectsEqual("actualMappedResourcePath is wrong",
                Arrays.asList(getMappedPngImageResourcePath()), actualMappedResourcePaths);
    }

    private File[] getSingleMatchedPngImageArray() {
        return new File[] {
                getMappedPngImageResourcePath().getNewResourceFile() };
    }

    private MappedResourcePath getMappedPngImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupPngImageResourcePath();
    }

    private File getRootResourcesPath() {
        return getResourcePathTestData()
                .getRootResourcesPath();
    }

    @Test
    public void testResolveToSingleWhenMultipleResourcesFound()
            throws Throwable {
        setObjectUnderTest((ImageMappedResourcePathBean) getResourcePathTestData()
                .getMappedIphoneGroupImageResourcePath());
        getObjectUnderTest().setResourceResolutionWarnLogger(getMockResolutionWarnLogger());

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesPath()),
                        EasyMock.eq(getObjectUnderTest().getNewResourcePath()),
                        EasyMock.aryEq(FILE_EXTENSIONS))).andReturn(
                getMultipleMatchedPngImageArray());

        recordLogWarningResolveToSingleFoundMultipleResources();

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve();

        assertComplexObjectsEqual("actualMappedResourcePath is wrong",
                Arrays.asList(getMappedPngImageResourcePath()), actualMappedResourcePaths);
    }

    private File[] getMultipleMatchedPngImageArray() {
        return new File[] {
                        getMappedPngImageResourcePath().getNewResourceFile(),
                        getMappedGifImageResourcePath().getNewResourceFile() };
    }

    private MappedResourcePath getMappedGifImageResourcePath() {
        return getResourcePathTestData()
                .getMappedIphoneGroupGifImageResourcePath();
    }


    private void recordLogWarningResolveToSingleFoundMultipleResources() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled()).andReturn(Boolean.TRUE);

        getMockResolutionWarnLogger()
            .warn("Requested resource '"
                    + getResourcePathTestData().getRequestedImageResourcePath()
                    + "' resolved to multiple real resources with extensions matching "
                    + ArrayUtils.toString(FILE_EXTENSIONS)
                    + ". Will only return the first resource. Total found: ["
                    + getMappedPngImageResourcePath().getNewResourceFile()
                    + ", " + getMappedGifImageResourcePath().getNewResourceFile()
                    + "].");
    }

    @Test
    public void testResolveToSingleWhenNoResourcesFound()
            throws Throwable {
        setObjectUnderTest((ImageMappedResourcePathBean) getResourcePathTestData()
                .getMappedIphoneGroupImageResourcePath());
        getObjectUnderTest().setResourceResolutionWarnLogger(getMockResolutionWarnLogger());

        EasyMock.expect(
                getMockFileIoFacade().list(EasyMock.eq(getRootResourcesPath()),
                        EasyMock.eq(getObjectUnderTest().getNewResourcePath()),
                        EasyMock.aryEq(FILE_EXTENSIONS))).andReturn(
                new File [] {});

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve();

        Assert.assertNotNull("actualMappedResourcePaths should not be null",
                actualMappedResourcePaths);
        Assert.assertTrue("actualMappedResourcePaths should be empty",
                actualMappedResourcePaths.isEmpty());
    }

    private ImageMappedResourcePathBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final ImageMappedResourcePathBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }

    public ResourceResolutionWarnLogger getMockResolutionWarnLogger() {
        return mockResolutionWarnLogger;
    }

    public void setMockResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResolutionWarnLogger) {
        this.mockResolutionWarnLogger = mockResolutionWarnLogger;
    }
}

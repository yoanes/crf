package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;

/**
 * Unit test {@link ImageResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private static final String[] FILE_EXTENSION_WILDCARDS = new String[] { "*" };

    private ImageResourceResolverBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new ImageResourceResolverBean(
                getResourcePathTestData()
                .getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(), FILE_EXTENSION_WILDCARDS));
    }

    @Override
    protected ImageResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new ImageResourceResolverBean(abstractResourceExtension,
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(), FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        return new ImageResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), getResourcesRootDir(),
                resourceResolutionWarnLogger, getDeploymentMetadata(),
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new ImageResourceResolverBean(getResourcePathTestData()
                .getCssExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceResolutionWarnLogger(), getDeploymentMetadata(),
                FILE_EXTENSION_WILDCARDS);
    }

    @Override
    protected ImageResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        return new ImageResourceResolverBean(
                getResourcePathTestData().getAbstractImageExtensionWithLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                deploymentMetadata, FILE_EXTENSION_WILDCARDS);
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
                new ImageResourceResolverBean(
                        getResourcePathTestData().getCssExtensionWithoutLeadingDot(),
                        getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                        getDeploymentMetadata(), testValue);

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
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordListFilesByExtension(getSingleMatchedPngImageArray());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

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
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordListFilesByExtension(getMultipleMatchedPngImageArray());

            recordLogWarningResolveToSingleFoundMultipleResources();

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

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
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordListFilesByExtension(new File[] {});

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedImageResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

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

        return getResourcePathTestData().getMappedIphoneGroupPngImageResourcePath();
    }

    private File getRootResourcesDir() {
        return getResourcePathTestData().getRootResourcesPath();
    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedCssResourcePath(),
                    getGroupTestData().createIPhoneGroup(),
                    getResolvedResourcePaths());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

    }

    @Test
    public void testSupportsWhenTrue() throws Throwable {

        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedImageResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {

        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
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
}

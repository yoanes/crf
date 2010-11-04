package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;

/**
 * Unit test {@link JavaScriptResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBeanTestCase extends AbstractResourceResolverTestCase {

    private static final String ABSTRACT_PATH_PACKAGE_KEYWORD = "package";

    private JavaScriptResourceResolverBean objectUnderTest;

    private JavaScriptFileFinder mockJavaScriptFileFinder;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new JavaScriptResourceResolverBean(
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                getMockJavaScriptFileFinder()));
    }


    @Override
    protected JavaScriptResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new JavaScriptResourceResolverBean(abstractResourceExtension,
                getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                getDeploymentMetadata(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                getMockJavaScriptFileFinder());
    }

    @Override
    protected JavaScriptResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        return new JavaScriptResourceResolverBean(getResourcePathTestData()
                .getScriptExtensionWithoutLeadingDot(), getResourcesRootDir(),
                resourceResolutionWarnLogger, getDeploymentMetadata(),
                ABSTRACT_PATH_PACKAGE_KEYWORD, getMockJavaScriptFileFinder());
    }

    @Override
    protected JavaScriptResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new JavaScriptResourceResolverBean(getResourcePathTestData()
                .getScriptExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceResolutionWarnLogger(), getDeploymentMetadata(),
                ABSTRACT_PATH_PACKAGE_KEYWORD, getMockJavaScriptFileFinder());
    }


    @Override
    protected JavaScriptResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        return new JavaScriptResourceResolverBean(getResourcePathTestData()
                .getScriptExtensionWithoutLeadingDot(), getResourcesRootDir(),
                getMockResourceResolutionWarnLogger(), deploymentMetadata,
                ABSTRACT_PATH_PACKAGE_KEYWORD, getMockJavaScriptFileFinder());
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
    public void testConstructorWithBlankAbstractPathPackageKeyword()
    throws Throwable {

        final String[] testValues = { null, StringUtils.EMPTY, " ", "  "};
        for (final String testValue : testValues) {
            try {
                new JavaScriptResourceResolverBean(
                        getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcesRootDir(),
                        getMockResourceResolutionWarnLogger(),
                        getDeploymentMetadata(),
                        testValue,
                        getMockJavaScriptFileFinder());

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(" has wrong message",
                        "abstractPathPackageKeyword must not be blank: '"
                        + testValue + "'", e.getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenJavaScriptFileFinderIsNull()
    throws Throwable {

        try {
            new JavaScriptResourceResolverBean(
                    getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), getMockResourceResolutionWarnLogger(),
                    getDeploymentMetadata(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                    null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "javaScriptFileFinder must not be null", e
                    .getMessage());
        }

    }

    @Test
    public void testResolveWhenPackageRequestedAndResourcesFound()
    throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedIphoneGroupPackagedScriptBundleResourcePath()
                    .getNewFile())).andReturn(createExistsByFilterExpectedFileFilterResults());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    createExistsByFilterExpectedResources(),
                    actualResources);
        }

    }

    @Test
    public void testResolveWhenPackageRequestedAndNoResourcesFound()
    throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedIphoneGroupPackagedScriptBundleResourcePath()
                    .getNewFile())).andReturn(new ArrayList<File>());

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                    actualResources);
        }

    }

    private List<File> createExistsByFilterExpectedFileFilterResults() {

        final File expectedFile1 =
            getResourcePathTestData()
            .getMappedDefaultGroupPackagedScriptResourcePath1()
            .getNewFile();
        final File expectedFile2 =
            getResourcePathTestData()
            .getMappedDefaultGroupPackagedScriptResourcePath2()
            .getNewFile();
        return Arrays.asList(expectedFile1, expectedFile2);
    }

    private List<Resource> createExistsByFilterExpectedResources() {

        return Arrays.asList(
                getResourcePathTestData()
                .getMappedDefaultGroupPackagedScriptResourcePath2(),
                getResourcePathTestData()
                .getMappedDefaultGroupPackagedScriptResourcePath1());
    }

    @Test
    public void testResolveWhenPackageNotRequestedAndResourcesFound() throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.TRUE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupNamedScriptResourcePath()),
                            actualResources);
        }

    }

    @Test
    public void testResolveWhenPackageNotRequestedAndNoResourcesFound() throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordCheckIfNewPathExists(Boolean.FALSE);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath(),
                        getGroupTestData().createIPhoneGroup(),
                        getResolvedResourcePaths());

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                    actualResources);
        }

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {

        final List<Resource> actualResources =
            getObjectUnderTest()
            .resolve(
                    getResourcePathTestData().getRequestedJspResourcePath(),
                    getGroupTestData().createIPhoneGroup(),
                    getResolvedResourcePaths());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

    }

    @Test
    public void testResolveWhenPackageRequestedAndIOExceptionWhenFindingFiles()
    throws Throwable {

        final IOException expectedWrappedException = new IOException("test");
        EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                getResourcePathTestData().getMappedIphoneGroupPackagedScriptBundleResourcePath()
                .getNewFile())).andThrow(expectedWrappedException);

        replay();

        try {
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                    getGroupTestData().createIPhoneGroup(),
                    getResolvedResourcePaths());

            Assert.fail("ResourceResolutionRuntimeException expected");
        } catch (final ResourceResolutionRuntimeException e) {
            Assert.assertEquals("ResourceResolutionRuntimeException has wrong message",
                    "Unexpected error when resolving requested resource '"
                    + getResourcePathTestData().getRequestedPackageScriptResourcePath()
                    + "' for group " + getGroupTestData().createIPhoneGroup(),
                    e.getMessage());
        }

    }

    @Test
    public void testSupportsTrueWhenPackageRequested() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath()));
    }

    @Test
    public void testSupportsTrueWhenPackageNotRequested() throws Throwable {
        Assert.assertTrue("supports should be true",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {
        Assert.assertFalse("supports should be false",
                getObjectUnderTest().supports(
                        getResourcePathTestData().getRequestedCssResourcePath()));
    }

    @Test
    public void testAccumulateGroupResourcesOverridesSameFilename() throws Throwable {

        final ResourceAccumulator resourceAccumulator = new ResourceAccumulator();
        resourceAccumulator.getAllResourcePaths().push(
                getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath());

        // Matches 3 different versions of the same Javascript file
        final List<Resource> resolvedPaths = new ArrayList<Resource>();
        resolvedPaths.add(getResourcePathTestData().getMappedAppleGroupNamedScriptResourcePath());
        resolvedPaths.add(getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath());

        getObjectUnderTest().accumulateGroupResources(resolvedPaths, resourceAccumulator);

        // Expect to only return the most specific version of the Javascript file
        final ResourceAccumulator expectedResourceAccumulator = new ResourceAccumulator();
        expectedResourceAccumulator.getAllResourcePaths().add(
                getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath());

        Assert.assertEquals(expectedResourceAccumulator, resourceAccumulator);
    }

    @Test
    public void testAccumulateGroupResourcesOverridesPackages() throws Throwable {

        final ResourceAccumulator resourceAccumulator = new ResourceAccumulator();
        resourceAccumulator.getAllResourcePaths().push(
                getResourcePathTestData().getMappedIphoneGroupBundledScriptResourcePath2());
        resourceAccumulator.getAllResourcePaths().push(
                getResourcePathTestData().getMappedIphoneGroupBundledScriptResourcePath1());

        // Matches 2 sets of the Javascript package
        final List<Resource> resolvedPaths = new ArrayList<Resource>();
        resolvedPaths.add(
                getResourcePathTestData().getMappedAppleGroupBundledScriptResourcePath1());
        resolvedPaths.add(
                getResourcePathTestData().getMappedAppleGroupBundledScriptResourcePath2());

        getObjectUnderTest().accumulateGroupResources(resolvedPaths, resourceAccumulator);

        // Expect to only return the most specific version of the Javascript file
        final ResourceAccumulator expectedResourceAccumulator = new ResourceAccumulator();
        expectedResourceAccumulator.getAllResourcePaths().add(
                getResourcePathTestData().getMappedIphoneGroupBundledScriptResourcePath1());
        expectedResourceAccumulator.getAllResourcePaths().add(
                getResourcePathTestData().getMappedIphoneGroupBundledScriptResourcePath2());

        Assert.assertEquals(expectedResourceAccumulator, resourceAccumulator);
    }

    @Test
    public void testAccumulateGroupResourcesReturnsAllUnique() throws Throwable {

        final ResourceAccumulator resourceAccumulator = new ResourceAccumulator();

        // Matches 2 different versions of the same 2 Javascript files
        final List<Resource> resolvedPaths = new ArrayList<Resource>();
        resolvedPaths.add(getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath());
        resolvedPaths.add(
                getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath2());
        resolvedPaths.add(getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath());
        resolvedPaths.add(getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath2());


        getObjectUnderTest().accumulateGroupResources(resolvedPaths, resourceAccumulator);

        // Expect to only return the most specific versions of the Javascript files
        final ResourceAccumulator expectedResourceAccumulator = new ResourceAccumulator();
        expectedResourceAccumulator.getAllResourcePaths().add(
                getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath());
        expectedResourceAccumulator.getAllResourcePaths().add(
                getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath2());

        Assert.assertEquals(expectedResourceAccumulator, resourceAccumulator);
    }


    private void recordCheckIfNewPathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData()
                        .getMappedIphoneGroupNamedScriptResourcePath()
                        .getNewPath())).andReturn(exists);

    }

    /**
     * @return the objectUnderTest
     */
    private JavaScriptResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JavaScriptResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    public JavaScriptFileFinder getMockJavaScriptFileFinder() {
        return mockJavaScriptFileFinder;
    }

    public void setMockJavaScriptFileFinder(
            final JavaScriptFileFinder mockJavaScriptFileFinder) {
        this.mockJavaScriptFileFinder = mockJavaScriptFileFinder;
    }
}

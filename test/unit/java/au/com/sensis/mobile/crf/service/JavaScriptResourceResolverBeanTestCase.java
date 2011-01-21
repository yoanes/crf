package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Unit test {@link JavaScriptResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBeanTestCase
    extends AbstractMultipleResourceResolverTestCase {

    private static final String ABSTRACT_PATH_PACKAGE_KEYWORD = "package";

    private JavaScriptResourceResolverBean objectUnderTest;

    private JavaScriptFileFinder mockJavaScriptFileFinder;
    private Device mockDevice;
    private ResourceCache mockResourceCache;

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
                getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(),
                getMockResourceAccumulatorFactory(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                getMockJavaScriptFileFinder()));
    }


    @Override
    protected JavaScriptResourceResolverBean createWithAbstractResourceExtension(
            final String abstractResourceExtension) {

        return new JavaScriptResourceResolverBean(getResourceResolverCommonParamHolder(),
                abstractResourceExtension, getResourcesRootDir(),
                getMockResourceAccumulatorFactory(),
                ABSTRACT_PATH_PACKAGE_KEYWORD, getMockJavaScriptFileFinder());
    }

    @Override
    protected JavaScriptResourceResolverBean createWithResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    resourceResolutionWarnLogger, getDeploymentMetadata(),
                    getMockConfigurationFactory(), getMockResourceCache());

        return new JavaScriptResourceResolverBean(commonParams,
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceAccumulatorFactory(),
                ABSTRACT_PATH_PACKAGE_KEYWORD, getMockJavaScriptFileFinder());
    }

    @Override
    protected JavaScriptResourceResolverBean createWithRootResourcesDir(
            final File rootResourcesDir) {

        return new JavaScriptResourceResolverBean(getResourceResolverCommonParamHolder(),
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(), rootResourcesDir,
                getMockResourceAccumulatorFactory(), ABSTRACT_PATH_PACKAGE_KEYWORD,
                getMockJavaScriptFileFinder());
    }


    @Override
    protected JavaScriptResourceResolverBean createWithDeploymentMetadata(
            final DeploymentMetadata deploymentMetadata) {

        final ResourceResolverCommonParamHolder commonParams =
            new ResourceResolverCommonParamHolder(
                    getMockResourceResolutionWarnLogger(), deploymentMetadata,
                    getMockConfigurationFactory(), getMockResourceCache());

        return new JavaScriptResourceResolverBean(commonParams,
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcesRootDir(), getMockResourceAccumulatorFactory(),
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
                new JavaScriptResourceResolverBean(getResourceResolverCommonParamHolder(),
                        getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcesRootDir(),
                        getMockResourceAccumulatorFactory(), testValue,
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
            new JavaScriptResourceResolverBean(getResourceResolverCommonParamHolder(),
                    getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                    getResourcesRootDir(), getMockResourceAccumulatorFactory(),
                    ABSTRACT_PATH_PACKAGE_KEYWORD, null);

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

        final String[] testValues =
                { getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForPackage();
            final ResourceCacheKey resourceCacheKey = createPackageScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIteratorForPackage();
            EasyMock.expect(
                    getMockJavaScriptFileFinder().findFiles(
                            getResourcePathTestData()
                                    .getMappedIphoneGroupPackagedScriptBaseDirResource()
                                    .getNewFile())).andReturn(createIphonePackageFiles());
            getMockResourceAccumulator().accumulate(createIphonePackageResources());

            EasyMock.expect(
                    getMockJavaScriptFileFinder().findFiles(
                            getResourcePathTestData()
                                    .getMappedAppleGroupPackagedScriptBaseDirResource()
                                    .getNewFile())).andReturn(createApplePackageFiles());
            getMockResourceAccumulator().accumulate(createApplePackageResources());

            final List<Resource> accumulatedResources = new ArrayList<Resource>();
            accumulatedResources.addAll(createIphonePackageResources());
            accumulatedResources.addAll(createApplePackageResources());

            recordGetResourcesFromAccumulator(accumulatedResources);

            recordPutResourceCache(resourceCacheKey, accumulatedResources
                    .toArray(new Resource[] {}));

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong", accumulatedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();

        }

    }

    private void recordGetResourceAccumulator() {
        EasyMock.expect(
                getMockResourceAccumulatorFactory().getJavaScriptResourceAccumulator(
                        ABSTRACT_PATH_PACKAGE_KEYWORD)).andReturn(getMockResourceAccumulator());
    }

    private ResourceCacheKey createPackageScriptResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
                new ResourceCacheKeyBean(getResourcePathTestData()
                        .getRequestedPackageScriptResourcePath(), new Group[] {
                        getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    private ResourceCacheKey createNamedScriptResourceCacheKey() {
        final ResourceCacheKey resourceCacheKey =
            new ResourceCacheKeyBean(getResourcePathTestData()
                    .getRequestedNamedScriptResourcePath(), new Group[] {
                getGroupTestData().createIPhoneGroup(),
                getGroupTestData().createAppleGroup() });
        return resourceCacheKey;
    }

    @Test
    public void testResolveWhenPackageRequestedAndNoResourcesFound()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForPackage();
            final ResourceCacheKey resourceCacheKey = createPackageScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIteratorForPackage();

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedIphoneGroupPackagedScriptBaseDirResource()
                    .getNewFile())).andReturn(new ArrayList<File>());
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                    getResourcePathTestData().getMappedAppleGroupPackagedScriptBaseDirResource()
                    .getNewFile())).andReturn(new ArrayList<File>());
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordGetResourcesFromAccumulator(new ArrayList<Resource>());

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    private List<File> createIphonePackageFiles() {

        final File expectedFile1 =
                getResourcePathTestData().getMappedIphoneGroupPackagedScriptResource1()
                        .getNewFile();
        final File expectedFile2 =
                getResourcePathTestData().getMappedIphoneGroupPackagedScriptResource2()
                        .getNewFile();
        return Arrays.asList(expectedFile1, expectedFile2);
    }

    private List<Resource> createIphonePackageResources() {

        return Arrays.asList(getResourcePathTestData()
                .getMappedIphoneGroupPackagedScriptResource1(), getResourcePathTestData()
                .getMappedIphoneGroupPackagedScriptResource2());
    }

    private List<File> createApplePackageFiles() {

        final File expectedFile1 =
                getResourcePathTestData().getMappedAppleGroupPackagedScriptResource1()
                        .getNewFile();
        return Arrays.asList(expectedFile1);
    }

    private List<Resource> createApplePackageResources() {

        return Arrays.asList(getResourcePathTestData()
                .getMappedAppleGroupPackagedScriptResource1());
    }

    @Test
    public void testResolveWhenNamedScriptRequestedAndResourcesFound()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForNamedScript();
            final ResourceCacheKey resourceCacheKey = createNamedScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIteratorNamedScript();

            recordCheckIfNewIphonePathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupNamedScriptResource()));

            recordCheckIfNewApplePathExists(Boolean.TRUE);
            getMockResourceAccumulator().accumulate(
                    Arrays.asList(getResourcePathTestData()
                            .getMappedAppleGroupNamedScriptResource()));

            final List<Resource> accumulatedResources =
                    Arrays.asList(getResourcePathTestData()
                            .getMappedIphoneGroupNamedScriptResource());
            recordGetResourcesFromAccumulator(accumulatedResources);

            recordPutResourceCache(resourceCacheKey, accumulatedResources
                    .toArray(new Resource[] {}));

            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedNamedScriptResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong", accumulatedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenNamedScriptRequestedAndNoResourcesFound()
        throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForNamedScript();
            final ResourceCacheKey resourceCacheKey = createNamedScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

            recordGetResourceAccumulator();
            recordGetMatchingGroupIteratorNamedScript();

            recordCheckIfNewIphonePathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordCheckIfNewApplePathExists(Boolean.FALSE);
            getMockResourceAccumulator().accumulate(new ArrayList<Resource>());

            recordGetResourcesFromAccumulator(new ArrayList<Resource>());

            recordPutEmptyResultsIntoResourceCache(resourceCacheKey);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong",
                    new ArrayList<Resource>(),
                    actualResources);

            assertResourceResolutionTreeNotUpdated();

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenPackageRequestedAndResourcesFromCache()
    throws Throwable {

        final String[] testValues = {
                getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForPackage();
            final ResourceCacheKey resourceCacheKey = createPackageScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            final List<Resource> accumulatedResources = new ArrayList<Resource>();
            accumulatedResources.addAll(createIphonePackageResources());
            accumulatedResources.addAll(createApplePackageResources());
            recordGetFromResourceCache(resourceCacheKey, accumulatedResources);

            replay();

            final List<Resource> actualResources =
                getObjectUnderTest().resolve(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                        getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong",
                    accumulatedResources,
                    actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenNamedScriptRequestedAndResourcesFromCache()
            throws Throwable {

        final String[] testValues =
                { getResourcePathTestData().getScriptExtensionWithoutLeadingDot(),
                        getResourcePathTestData().getScriptExtensionWithLeadingDot() };

        for (final String testValue : testValues) {
            setObjectUnderTest(createWithAbstractResourceExtension(testValue));

            recordGetMatchingGroupsForNamedScript();
            final ResourceCacheKey resourceCacheKey = createNamedScriptResourceCacheKey();
            recordCheckResourceCache(resourceCacheKey, Boolean.TRUE);

            final List<Resource> accumulatedResources =
                Arrays.asList(getResourcePathTestData()
                        .getMappedIphoneGroupNamedScriptResource());
            recordGetFromResourceCache(resourceCacheKey, accumulatedResources);


            replay();

            final List<Resource> actualResources =
                    getObjectUnderTest().resolve(
                            getResourcePathTestData().getRequestedNamedScriptResourcePath(),
                            getMockDevice());

            // Explicit verify since we are in a loop.
            verify();

            assertComplexObjectsEqual("actualResources is wrong",
                    accumulatedResources, actualResources);

            assertResourceResolutionTreeUpdated(accumulatedResources);

            // Explicit reset since we are in a loop.
            reset();
        }

    }

    @Test
    public void testResolveWhenNoMappingPerformed() throws Throwable {

        final List<Resource> actualResources =
            getObjectUnderTest()
            .resolve(
                    getResourcePathTestData().getRequestedJspResourcePath(),
                    getMockDevice());

        Assert.assertNotNull("actualResources should not be null",
                actualResources);
        Assert.assertTrue("actualResources should be empty",
                actualResources.isEmpty());

    }

    @Test
    public void testResolveWhenPackageRequestedAndIOExceptionWhenFindingFiles()
        throws Throwable {

        recordGetResourceAccumulator();

        recordGetMatchingGroupsForPackage();
        final ResourceCacheKey resourceCacheKey = createPackageScriptResourceCacheKey();
        recordCheckResourceCache(resourceCacheKey, Boolean.FALSE);

        recordGetMatchingGroupIteratorForPackage();
        final IOException expectedWrappedException = new IOException("test");
        EasyMock.expect(getMockJavaScriptFileFinder().findFiles(
                getResourcePathTestData().getMappedIphoneGroupPackagedScriptBaseDirResource()
                .getNewFile())).andThrow(expectedWrappedException);

        replay();

        try {
            getObjectUnderTest().resolve(
                    getResourcePathTestData().getRequestedPackageScriptResourcePath(),
                    getMockDevice());

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

    private void recordGetMatchingGroupIteratorForPackage() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath()))
                .andReturn(getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
                Arrays.asList(getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice())).andReturn(
                matchingGroupsIterator);

    }

    private void recordGetMatchingGroupsForPackage() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedPackageScriptResourcePath()))
                .andReturn(getMockUiConfiguration());

        final Group[] matchingGroups =
                new Group[] { getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

    }

    private void recordGetMatchingGroupsForNamedScript() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath()))
                        .andReturn(getMockUiConfiguration());

        final Group[] matchingGroups =
            new Group[] { getGroupTestData().createIPhoneGroup(),
                getGroupTestData().createAppleGroup() };

        EasyMock.expect(getMockUiConfiguration().matchingGroups(getMockDevice())).andReturn(
                matchingGroups);

    }

    private void recordGetMatchingGroupIteratorNamedScript() {

        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedNamedScriptResourcePath()))
                .andReturn(getMockUiConfiguration());

        final Iterator<Group> matchingGroupsIterator =
                Arrays.asList(getGroupTestData().createIPhoneGroup(),
                        getGroupTestData().createAppleGroup()).iterator();

        EasyMock.expect(getMockUiConfiguration().matchingGroupIterator(getMockDevice())).andReturn(
                matchingGroupsIterator);

    }

    private void recordCheckIfNewIphonePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData().getMappedIphoneGroupNamedScriptResource()
                                .getNewPath())).andReturn(exists);

    }

    private void recordCheckIfNewApplePathExists(final Boolean exists) {
        EasyMock.expect(
                getMockFileIoFacade().fileExists(
                        getResourcePathTestData().getRootResourcesPath(),
                        getResourcePathTestData().getMappedAppleGroupNamedScriptResource()
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

    /**
     * @return the mockDevice
     */
    @Override
    public Device getMockDevice() {

        return mockDevice;
    }

    /**
     * @param mockDevice  the mockDevice to set
     */
    @Override
    public void setMockDevice(final Device mockDevice) {

        this.mockDevice = mockDevice;
    }


    /**
     * @return the mockResourceCache
     */
    @Override
    public ResourceCache getMockResourceCache() {
        return mockResourceCache;
    }


    /**
     * @param mockResourceCache the mockResourceCache to set
     */
    @Override
    public void setMockResourceCache(final ResourceCache mockResourceCache) {
        this.mockResourceCache = mockResourceCache;
    }
}

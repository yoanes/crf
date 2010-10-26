package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.config.UiConfiguration;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceResolverEngineBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverEngineBeanTestCase extends
AbstractJUnit4TestCase {

    private ResourceResolverEngineBean objectUnderTest;
    private ConfigurationFactory mockConfigurationFactory;
    private UiConfiguration mockUiConfiguration;
    private ResourceResolver mockResourceResolver;
    private FileIoFacade mockFileIoFacade;
    private Device mockDevice;
    private Resource mockIphoneResource1;
    private Resource mockIphoneResource2;

    private Resource mockAndroidResource1;
    private Resource mockAndroidResource2;
    private Resource mockDefaultResource1;
    private Resource mockDefaultResource2;
    private Resource mockAppleResource1;
    private Resource mockAppleResource2;
    private Resource mockHD800Resource1;
    private Resource mockHD800Resource2;
    private Resource mockMediumResource1;
    private Resource mockMediumResource2;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();
    private ResourceAccumulator mockResolvedResourcePaths;
    private final ResourceAccumulator resourceAccumulator = new ResourceAccumulator();
    private final Deque<Resource> allResourcePaths = new ArrayDeque<Resource>();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new ResourceResolverEngineBean(
                getMockConfigurationFactory(), getMockResourceResolver(),
                getMockResolutionWarnLogger()));
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
    public void testConstructorWhenConfigurationFactoryNull() throws Throwable {
        try {
            new ResourceResolverEngineBean(null,
                    getMockResourceResolver(), getMockResolutionWarnLogger());
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "configurationFactory must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWhenResourceResolverIsNull() throws Throwable {
        try {
            new ResourceResolverEngineBean(getMockConfigurationFactory(),
                    null, getMockResolutionWarnLogger());
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolver must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerNull() throws Throwable {
        try {
            new ResourceResolverEngineBean(getMockConfigurationFactory(),
                    getMockResourceResolver(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e.getMessage());
        }
    }

    @Test
    public void testGetResourcePathWhenFirstMappedResourceMatchesAndHasResource() throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasSingleResource(Boolean.TRUE);

        replay();

        final Resource actualResourcePath =
            getObjectUnderTest().getResource(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockIphoneResource1(),
                actualResourcePath);
    }

    @Test
    public void testGetResourcePathWhenFirstMappedResourceMatchesAndMultipleResourcesFound()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        //recordCheckIfIphoneGroupHasResource(Boolean.TRUE);

        recordCheckIfIphoneGroupHasSingleResourceReturnsMultiple(Boolean.TRUE);

        recordLogWarningResolveTFoundMultipleResources();

        replay();

        final Resource actualResourcePath =
            getObjectUnderTest().getResource(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockIphoneResource1(),
                actualResourcePath);
    }

    private void recordLogWarningResolveTFoundMultipleResources() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled()).andReturn(Boolean.TRUE);

        getMockResolutionWarnLogger().warn("Requested resource '"
                + getResourcePathTestData().getRequestedJspResourcePath()
                + "' resolved to multiple resources when only one was requested. "
                + "Will only return the first. Total found: "
                + Arrays.asList(getMockIphoneResource1(),
                        getMockIphoneResource2()) + ".");
    }

    @Test
    public void testGetResourcePathWhenLaterMappedResourceMatchesAndHasResource() throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasSingleResource(Boolean.TRUE);

        replay();

        final Resource actualResourcePath =
            getObjectUnderTest().getResource(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockAndroidResource1(),
                actualResourcePath);
    }


    private void recordCheckIfIphoneGroupHasSingleResource(final Boolean fileExists)
    throws IOException {
        final List<Resource> mapperResults = Arrays.asList(getMockIphoneResource1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            null)).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            null)).andReturn(new ArrayList<Resource>());
        }
    }

    private void recordCheckIfIphoneGroupHasSingleResourceReturnsMultiple(final Boolean fileExists)
    throws IOException {
        final List<Resource> mapperResults = Arrays.asList(getMockIphoneResource1(),
                getMockIphoneResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            null)).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            null)).andReturn(new ArrayList<Resource>());
        }
    }

    private void recordCheckIfIphoneGroupHasResource(final Boolean fileExists)
    throws IOException {

        final List<Resource> mapperResults =
            Arrays.asList(getMockIphoneResource1(), getMockIphoneResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            getResourceAccumulator())).andReturn(mapperResults);

            //EasyMock.expect(getMockResolvedResourcePaths().getAllResourcePaths()).andReturn(allResourcePaths).times(2);


        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }

    }

    private void recordCheckIfAppleGroupHasResource(final Boolean fileExists)
    throws IOException {

        final List<Resource> mapperResults =
            Arrays.asList(getMockAppleResource1(), getMockAppleResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAppleGroup(),
                            getResourceAccumulator())).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAppleGroup(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }
    }

    private void recordCheckIfHD800GroupHasResource(final Boolean fileExists)
    throws IOException {

        final List<Resource> mapperResults =
            Arrays.asList(getMockHD800Resource1(), getMockHD800Resource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createHD800Group(),
                            getResourceAccumulator())).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createHD800Group(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }

    }

    private void recordCheckIfMediumGroupHasResource(final Boolean fileExists)
    throws IOException {

        final List<Resource> mapperResults =
            Arrays.asList(getMockMediumResource1(), getMockMediumResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createMediumGroup(),
                            getResourceAccumulator())).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createMediumGroup(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }

    }

    private void recordCheckIfAndroidGroupHasResource(final Boolean fileExists)
    throws IOException {
        final List<Resource> mapperResults =
            Arrays.asList(getMockAndroidResource1(),
                    getMockAndroidResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup(),
                            getResourceAccumulator())).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }

    }

    private void recordCheckIfAndroidGroupHasSingleResource(
            final Boolean fileExists) throws IOException {
        final List<Resource> mapperResults =
            Arrays.asList(getMockAndroidResource1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup(),
                            null)).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup(),
                            null)).andReturn(new ArrayList<Resource>());
        }
    }

    @Test
    public void testGetResourcePathWhenDefaultGroupHasResource() throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfDefaultGroupHasSingleResource(Boolean.TRUE);

        replay();

        final Resource actualResourcePath =
            getObjectUnderTest().getResource(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockDefaultResource1(),
                actualResourcePath);
    }

    private void recordCheckIfDefaultGroupHasResource(final Boolean fileExists)
    throws IOException {
        final List<Resource> mapperResults =
            Arrays.asList(getMockDefaultResource1(),
                    getMockDefaultResource2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup(),
                            getResourceAccumulator())).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup(),
                            getResourceAccumulator())).andReturn(new ArrayList<Resource>());
        }
    }

    private void recordCheckIfDefaultGroupHasSingleResource(
            final Boolean fileExists) throws IOException {
        final List<Resource> mapperResults =
            Arrays.asList(getMockDefaultResource1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup(),
                            null)).andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourceResolver().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup(),
                            null)).andReturn(new ArrayList<Resource>());
        }
    }

    @Test
    public void testGetResourcePathWhenNoGroupsHaveResource() throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfDefaultGroupHasSingleResource(Boolean.FALSE);

        replay();

        final Resource actualResourcePath =
            getObjectUnderTest().getResource(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertNull("resourcePath is wrong", actualResourcePath);
    }

    @Test
    public void testGetAllResourcePathsWhenAllGroupsHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.TRUE);

        recordCheckIfAndroidGroupHasResource(Boolean.TRUE);

        recordCheckIfAppleGroupHasResource(Boolean.TRUE);

        recordCheckIfHD800GroupHasResource(Boolean.TRUE);

        recordCheckIfMediumGroupHasResource(Boolean.TRUE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths = getObjectUnderTest().getAllResources(
                getMockDevice(),
                getResourcePathTestData().getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockMediumResource1(),
        //                getMockMediumResource2(),
        //                getMockHD800Resource1(),
        //                getMockHD800Resource2(),
        //                getMockAppleResource1(),
        //                getMockAppleResource2(),
        //                getMockAndroidResource1(),
        //                getMockAndroidResource2(),
        //                getMockIphoneResource1(),
        //                getMockIphoneResource2()),
        //                actualResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenLaterGroupsHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasResource(Boolean.FALSE);

        recordCheckIfAppleGroupHasResource(Boolean.FALSE);

        recordCheckIfHD800GroupHasResource(Boolean.FALSE);

        recordCheckIfMediumGroupHasResource(Boolean.TRUE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest().getAllResources(
                    getMockDevice(), getResourcePathTestData()
                    .getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockMediumResource1(),
        //                getMockMediumResource2()),
        //                actualResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenNonContiguousIntermediateGroupsHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasResource(Boolean.TRUE);

        recordCheckIfAppleGroupHasResource(Boolean.FALSE);

        recordCheckIfHD800GroupHasResource(Boolean.TRUE);

        recordCheckIfMediumGroupHasResource(Boolean.FALSE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest().getAllResources(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockHD800Resource1(),
        //                getMockHD800Resource2(),
        //                getMockAndroidResource1(),
        //                getMockAndroidResource2()),
        //                actualResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenContiguousIntermediateGroupsHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasResource(Boolean.FALSE);

        recordCheckIfAppleGroupHasResource(Boolean.TRUE);

        recordCheckIfHD800GroupHasResource(Boolean.TRUE);

        recordCheckIfMediumGroupHasResource(Boolean.FALSE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest().getAllResources(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockHD800Resource1(),
        //                getMockHD800Resource2(),
        //                getMockAppleResource1(),
        //                getMockAppleResource2()),
        //                actualResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenNonContiguousIntermediateGroupsDoNotHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.TRUE);

        recordCheckIfAndroidGroupHasResource(Boolean.FALSE);

        recordCheckIfAppleGroupHasResource(Boolean.TRUE);

        recordCheckIfHD800GroupHasResource(Boolean.FALSE);

        recordCheckIfMediumGroupHasResource(Boolean.TRUE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest().getAllResources(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockMediumResource1(),
        //                getMockMediumResource2(),
        //                getMockAppleResource1(),
        //                getMockAppleResource2(),
        //                getMockIphoneResource1(),
        //                getMockIphoneResource2()),
        //                actualResourcePaths);
    }
    @Test
    public void testGetAllResourcePathsWhenContiguousIntermediateGroupsDoNotHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.TRUE);

        recordCheckIfAndroidGroupHasResource(Boolean.TRUE);

        recordCheckIfAppleGroupHasResource(Boolean.FALSE);

        recordCheckIfHD800GroupHasResource(Boolean.FALSE);

        recordCheckIfMediumGroupHasResource(Boolean.TRUE);

        recordCheckIfDefaultGroupHasResource(Boolean.TRUE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest().getAllResources(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        //        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
        //                getMockDefaultResource1(),
        //                getMockDefaultResource2(),
        //                getMockMediumResource1(),
        //                getMockMediumResource2(),
        //                getMockAndroidResource1(),
        //                getMockAndroidResource2(),
        //                getMockIphoneResource1(),
        //                getMockIphoneResource2()),
        //                actualResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenNoGroupsHaveResource()
    throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetAllResourcePaths();

        recordCheckIfIphoneGroupHasResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasResource(Boolean.FALSE);

        recordCheckIfAppleGroupHasResource(Boolean.FALSE);

        recordCheckIfHD800GroupHasResource(Boolean.FALSE);

        recordCheckIfMediumGroupHasResource(Boolean.FALSE);

        recordCheckIfDefaultGroupHasResource(Boolean.FALSE);

        replay();

        final List<Resource> actualResourcePaths =
            getObjectUnderTest()
            .getAllResources(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertNotNull("actualResourcePaths should not be null",
                actualResourcePaths);
        Assert.assertTrue("actualResourcePaths should be empty",
                actualResourcePaths.isEmpty());
    }

    private void recordGetMatchingGroupsIteratorForGetResourcePath() {
        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup(),
                    getGroupTestData().createAndroidGroup(),
                    getGroupTestData().createDefaultGroup())
                    .iterator();
        EasyMock.expect(
                getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
                .andReturn(matchingGroupsIterator);
    }

    private void recordGetMatchingGroupsIteratorForGetAllResourcePaths() {

        final Iterator<Group> matchingGroupsIterator =
            Arrays.asList(getGroupTestData().createIPhoneGroup(),
                    getGroupTestData().createAndroidGroup(),
                    getGroupTestData().createAppleGroup(),
                    getGroupTestData().createHD800Group(),
                    getGroupTestData().createMediumGroup(),
                    getGroupTestData().createDefaultGroup())
                    .iterator();

        EasyMock.expect(
                getMockUiConfiguration().matchingGroupIterator(getMockDevice()))
                .andReturn(matchingGroupsIterator);
    }

    private void recordGetUiConfiguration() {
        EasyMock.expect(
                getMockConfigurationFactory().getUiConfiguration(
                        getResourcePathTestData().getRequestedJspResourcePath())).andReturn(
                                getMockUiConfiguration()).atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceResolverEngineBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final ResourceResolverEngineBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockDevice
     */
    public Device getMockDevice() {
        return mockDevice;
    }

    /**
     * @param mockDevice the mockDevice to set
     */
    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }

    /**
     * @return the mockConfigurationFactory
     */
    public ConfigurationFactory getMockConfigurationFactory() {
        return mockConfigurationFactory;
    }

    /**
     * @param mockConfigurationFactory the mockConfigurationFactory to set
     */
    public void setMockConfigurationFactory(
            final ConfigurationFactory mockConfigurationFactory) {
        this.mockConfigurationFactory = mockConfigurationFactory;
    }

    /**
     * @return the mockUiConfiguration
     */
    public UiConfiguration getMockUiConfiguration() {
        return mockUiConfiguration;
    }

    /**
     * @param mockUiConfiguration the mockUiConfiguration to set
     */
    public void setMockUiConfiguration(final UiConfiguration mockUiConfiguration) {
        this.mockUiConfiguration = mockUiConfiguration;
    }

    /**
     * @return the mockResourceResolver
     */
    public ResourceResolver getMockResourceResolver() {
        return mockResourceResolver;
    }

    /**
     * @param mockResourceResolver the mockResourceResolver to set
     */
    public void setMockResourceResolver(final ResourceResolver mockResourceResolver) {
        this.mockResourceResolver = mockResourceResolver;
    }

    /**
     * @return the resourceAccumulator
     */
    protected ResourceAccumulator getResourceAccumulator() {

        return resourceAccumulator;
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

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the resolvedResourcePaths
     */
    public ResourceAccumulator getMockResolvedResourcePaths() {
        return mockResolvedResourcePaths;
    }

    /**
     * @param mockResolvedResourcePaths  the mockResolvedResourcePaths to set
     */
    public void setMockResolvedResourcePaths(
            final ResourceAccumulator mockResolvedResourcePaths) {

        this.mockResolvedResourcePaths = mockResolvedResourcePaths;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockIphoneResource1
     */
    public Resource getMockIphoneResource1() {
        return mockIphoneResource1;
    }

    /**
     * @param mockIphoneResource1 the mockIphoneResource1 to set
     */
    public void setMockIphoneResource1(
            final Resource mockIphoneResource1) {
        this.mockIphoneResource1 = mockIphoneResource1;
    }

    public Resource getMockIphoneResource2() {
        return mockIphoneResource2;
    }

    public void setMockIphoneResource2(
            final Resource mockIphoneResource2) {
        this.mockIphoneResource2 = mockIphoneResource2;
    }

    /**
     * @return the mockAndroidResource1
     */
    public Resource getMockAndroidResource1() {
        return mockAndroidResource1;
    }

    /**
     * @param mockAndroidResource1 the mockAndroidResource1 to set
     */
    public void setMockAndroidResource1(
            final Resource mockAndroidResource1) {
        this.mockAndroidResource1 = mockAndroidResource1;
    }

    /**
     * @return the mockDefaultResource1
     */
    public Resource getMockDefaultResource1() {
        return mockDefaultResource1;
    }

    /**
     * @param mockDefaultResource1 the mockDefaultResource1 to set
     */
    public void setMockDefaultResource1(
            final Resource mockDefaultResource1) {
        this.mockDefaultResource1 = mockDefaultResource1;
    }

    /**
     * @return the mockAppleResource1
     */
    public Resource getMockAppleResource1() {
        return mockAppleResource1;
    }

    /**
     * @param mockAppleResource1 the mockAppleResource1 to set
     */
    public void setMockAppleResource1(
            final Resource mockAppleResource1) {
        this.mockAppleResource1 = mockAppleResource1;
    }

    /**
     * @return the mockHD800Resource1
     */
    public Resource getMockHD800Resource1() {
        return mockHD800Resource1;
    }

    /**
     * @param mockHD800Resource1 the mockHD800Resource1 to set
     */
    public void setMockHD800Resource1(
            final Resource mockHD800Resource1) {
        this.mockHD800Resource1 = mockHD800Resource1;
    }

    /**
     * @return the mockMediumResource1
     */
    public Resource getMockMediumResource1() {
        return mockMediumResource1;
    }

    /**
     * @param mockMediumResource1 the mockMediumResource1 to set
     */
    public void setMockMediumResource1(
            final Resource mockMediumResource1) {
        this.mockMediumResource1 = mockMediumResource1;
    }

    /**
     * @return the mockResolutionWarnLogger
     */
    public ResourceResolutionWarnLogger getMockResolutionWarnLogger() {
        return mockResolutionWarnLogger;
    }

    /**
     * @param mockResolutionWarnLogger the mockResolutionWarnLogger to set
     */
    public void setMockResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResolutionWarnLogger) {
        this.mockResolutionWarnLogger = mockResolutionWarnLogger;
    }

    public Resource getMockMediumResource2() {
        return mockMediumResource2;
    }

    public void setMockMediumResource2(
            final Resource mockMediumResource2) {
        this.mockMediumResource2 = mockMediumResource2;
    }

    public Resource getMockAndroidResource2() {
        return mockAndroidResource2;
    }

    public void setMockAndroidResource2(
            final Resource mockAndroidResource2) {
        this.mockAndroidResource2 = mockAndroidResource2;
    }

    public Resource getMockDefaultResource2() {
        return mockDefaultResource2;
    }

    public void setMockDefaultResource2(
            final Resource mockDefaultResource2) {
        this.mockDefaultResource2 = mockDefaultResource2;
    }

    public Resource getMockAppleResource2() {
        return mockAppleResource2;
    }

    public void setMockAppleResource2(
            final Resource mockAppleResource2) {
        this.mockAppleResource2 = mockAppleResource2;
    }

    public Resource getMockHD800Resource2() {
        return mockHD800Resource2;
    }

    public void setMockHD800Resource2(
            final Resource mockHD800Resource2) {
        this.mockHD800Resource2 = mockHD800Resource2;
    }

    /* (non-Javadoc)
     * @see au.com.sensis.wireless.test.AbstractJUnit4TestCase#verify()
     */
    @Override
    public void verify() {
    }


}

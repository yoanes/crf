package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceSelectorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorBeanTestCase extends
        AbstractJUnit4TestCase {

    private ResourceSelectorBean objectUnderTest;
    private ConfigurationFactory mockConfigurationFactory;
    private UiConfiguration mockUiConfiguration;
    private ResourcePathMapper mockResourcePathMapper;
    private FileIoFacade mockFileIoFacade;
    private Device mockDevice;
    private MappedResourcePath mockIphoneMappedResourcePath1;
    private MappedResourcePath mockIphoneMappedResourcePath2;

    private MappedResourcePath mockAndroidMappedResourcePath1;
    private MappedResourcePath mockAndroidMappedResourcePath2;
    private MappedResourcePath mockDefaultMappedResourcePath1;
    private MappedResourcePath mockDefaultMappedResourcePath2;
    private MappedResourcePath mockAppleMappedResourcePath1;
    private MappedResourcePath mockAppleMappedResourcePath2;
    private MappedResourcePath mockHD800MappedResourcePath1;
    private MappedResourcePath mockHD800MappedResourcePath2;
    private MappedResourcePath mockMediumMappedResourcePath1;
    private MappedResourcePath mockMediumMappedResourcePath2;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new ResourceSelectorBean(
                getMockConfigurationFactory(), getMockResourcePathMapper(),
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
            new ResourceSelectorBean(null,
                    getMockResourcePathMapper(), getMockResolutionWarnLogger());
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "configurationFactory must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWhenResourcePathMapperNull() throws Throwable {
        try {
            new ResourceSelectorBean(getMockConfigurationFactory(),
                    null, getMockResolutionWarnLogger());
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourcePathMapper must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerNull() throws Throwable {
        try {
            new ResourceSelectorBean(getMockConfigurationFactory(),
                    getMockResourcePathMapper(), null);
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

        final MappedResourcePath actualResourcePath =
            getObjectUnderTest().getResourcePath(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockIphoneMappedResourcePath1(),
                actualResourcePath);
    }

    @Test
    public void testGetResourcePathWhenFirstMappedResourceMatchesAndMultipleResourcesFound()
        throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasResource(Boolean.TRUE);

        recordLogWarningResolveTFoundMultipleResources();

        replay();

        final MappedResourcePath actualResourcePath =
            getObjectUnderTest().getResourcePath(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockIphoneMappedResourcePath1(),
                actualResourcePath);
    }

    private void recordLogWarningResolveTFoundMultipleResources() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled()).andReturn(Boolean.TRUE);

        getMockResolutionWarnLogger().warn("Requested resource '"
                + getResourcePathTestData().getRequestedJspResourcePath()
                + "' resolved to multiple resources when only one was requested. "
                + "Will only return the first. Total found: "
                + Arrays.asList(getMockIphoneMappedResourcePath1(),
                        getMockIphoneMappedResourcePath2()) + ".");
    }

    @Test
    public void testGetResourcePathWhenLaterMappedResourceMatchesAndHasResource() throws Throwable {

        recordGetUiConfiguration();

        recordGetMatchingGroupsIteratorForGetResourcePath();

        recordCheckIfIphoneGroupHasSingleResource(Boolean.FALSE);

        recordCheckIfAndroidGroupHasSingleResource(Boolean.TRUE);

        replay();

        final MappedResourcePath actualResourcePath =
                getObjectUnderTest().getResourcePath(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockAndroidMappedResourcePath1(),
                actualResourcePath);
    }


    private void recordCheckIfIphoneGroupHasSingleResource(final Boolean fileExists)
        throws IOException {
        final List<MappedResourcePath> mapperResults
            = Arrays.asList(getMockIphoneMappedResourcePath1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup()))
                            .andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData().getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup()))
                            .andReturn(new ArrayList<MappedResourcePath>());
        }
    }

    private void recordCheckIfIphoneGroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockIphoneMappedResourcePath1(),
                        getMockIphoneMappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup())).andReturn(
                    mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createIPhoneGroup())).andReturn(
                    new ArrayList<MappedResourcePath>());
        }

    }

    private void recordCheckIfAppleGroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockAppleMappedResourcePath1(),
                        getMockAppleMappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAppleGroup())).andReturn(
                    mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAppleGroup())).andReturn(
                    new ArrayList<MappedResourcePath>());
        }
    }

    private void recordCheckIfHD800GroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockHD800MappedResourcePath1(),
                        getMockHD800MappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createHD800Group())).andReturn(
                    mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createHD800Group())).andReturn(
                    new ArrayList<MappedResourcePath>());
        }

    }

    private void recordCheckIfMediumGroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockMediumMappedResourcePath1(),
                        getMockMediumMappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createMediumGroup())).andReturn(
                    mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createMediumGroup())).andReturn(
                    new ArrayList<MappedResourcePath>());
        }

    }

    private void recordCheckIfAndroidGroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockAndroidMappedResourcePath1(),
                        getMockAndroidMappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup()))
                    .andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup()))
                    .andReturn(new ArrayList<MappedResourcePath>());
        }

    }

    private void recordCheckIfAndroidGroupHasSingleResource(
            final Boolean fileExists) throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockAndroidMappedResourcePath1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup()))
                    .andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createAndroidGroup()))
                    .andReturn(new ArrayList<MappedResourcePath>());
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

        final MappedResourcePath actualResourcePath =
            getObjectUnderTest().getResourcePath(getMockDevice(),
                    getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong",
                getMockDefaultMappedResourcePath1(),
                actualResourcePath);
    }

    private void recordCheckIfDefaultGroupHasResource(final Boolean fileExists)
            throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockDefaultMappedResourcePath1(),
                        getMockDefaultMappedResourcePath2());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup()))
                    .andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup()))
                    .andReturn(new ArrayList<MappedResourcePath>());
        }
    }

    private void recordCheckIfDefaultGroupHasSingleResource(
            final Boolean fileExists) throws IOException {
        final List<MappedResourcePath> mapperResults =
                Arrays.asList(getMockDefaultMappedResourcePath1());

        if (fileExists) {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup()))
                    .andReturn(mapperResults);
        } else {
            EasyMock.expect(
                    getMockResourcePathMapper().resolve(
                            getResourcePathTestData()
                                    .getRequestedJspResourcePath(),
                            getGroupTestData().createDefaultGroup()))
                    .andReturn(new ArrayList<MappedResourcePath>());
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

        final MappedResourcePath actualResourcePath =
            getObjectUnderTest().getResourcePath(getMockDevice(),
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

        final List<MappedResourcePath> actualResourcePaths =
                getObjectUnderTest()
                        .getAllResourcePaths(
                                getMockDevice(),
                                getResourcePathTestData()
                                        .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockMediumMappedResourcePath1(),
                getMockMediumMappedResourcePath2(),
                getMockHD800MappedResourcePath1(),
                getMockHD800MappedResourcePath2(),
                getMockAppleMappedResourcePath1(),
                getMockAppleMappedResourcePath2(),
                getMockAndroidMappedResourcePath1(),
                getMockAndroidMappedResourcePath2(),
                getMockIphoneMappedResourcePath1(),
                getMockIphoneMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest().getAllResourcePaths(
                    getMockDevice(), getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockMediumMappedResourcePath1(),
                getMockMediumMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest().getAllResourcePaths(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockHD800MappedResourcePath1(),
                getMockHD800MappedResourcePath2(),
                getMockAndroidMappedResourcePath1(),
                getMockAndroidMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest().getAllResourcePaths(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockHD800MappedResourcePath1(),
                getMockHD800MappedResourcePath2(),
                getMockAppleMappedResourcePath1(),
                getMockAppleMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest().getAllResourcePaths(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockMediumMappedResourcePath1(),
                getMockMediumMappedResourcePath2(),
                getMockAppleMappedResourcePath1(),
                getMockAppleMappedResourcePath2(),
                getMockIphoneMappedResourcePath1(),
                getMockIphoneMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest().getAllResourcePaths(
                    getMockDevice(),
                    getResourcePathTestData()
                    .getRequestedJspResourcePath());

        Assert.assertEquals("resourcePath is wrong", Arrays.asList(
                getMockDefaultMappedResourcePath1(),
                getMockDefaultMappedResourcePath2(),
                getMockMediumMappedResourcePath1(),
                getMockMediumMappedResourcePath2(),
                getMockAndroidMappedResourcePath1(),
                getMockAndroidMappedResourcePath2(),
                getMockIphoneMappedResourcePath1(),
                getMockIphoneMappedResourcePath2()),
                actualResourcePaths);
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

        final List<MappedResourcePath> actualResourcePaths =
            getObjectUnderTest()
            .getAllResourcePaths(
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
        EasyMock.expect(getMockConfigurationFactory().getUiConfiguration())
            .andReturn(getMockUiConfiguration()).atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceSelectorBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final ResourceSelectorBean objectUnderTest) {
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
     * @return the mockResourcePathMapper
     */
    public ResourcePathMapper getMockResourcePathMapper() {
        return mockResourcePathMapper;
    }

    /**
     * @param mockResourcePathMapper the mockResourcePathMapper to set
     */
    public void setMockResourcePathMapper(final ResourcePathMapper mockResourcePathMapper) {
        this.mockResourcePathMapper = mockResourcePathMapper;
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
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockIphoneMappedResourcePath1
     */
    public MappedResourcePath getMockIphoneMappedResourcePath1() {
        return mockIphoneMappedResourcePath1;
    }

    /**
     * @param mockIphoneMappedResourcePath1 the mockIphoneMappedResourcePath1 to set
     */
    public void setMockIphoneMappedResourcePath1(
            final MappedResourcePath mockIphoneMappedResourcePath1) {
        this.mockIphoneMappedResourcePath1 = mockIphoneMappedResourcePath1;
    }

    public MappedResourcePath getMockIphoneMappedResourcePath2() {
        return mockIphoneMappedResourcePath2;
    }

    public void setMockIphoneMappedResourcePath2(
            final MappedResourcePath mockIphoneMappedResourcePath2) {
        this.mockIphoneMappedResourcePath2 = mockIphoneMappedResourcePath2;
    }

    /**
     * @return the mockAndroidMappedResourcePath1
     */
    public MappedResourcePath getMockAndroidMappedResourcePath1() {
        return mockAndroidMappedResourcePath1;
    }

    /**
     * @param mockAndroidMappedResourcePath1 the mockAndroidMappedResourcePath1 to set
     */
    public void setMockAndroidMappedResourcePath1(
            final MappedResourcePath mockAndroidMappedResourcePath1) {
        this.mockAndroidMappedResourcePath1 = mockAndroidMappedResourcePath1;
    }

    /**
     * @return the mockDefaultMappedResourcePath1
     */
    public MappedResourcePath getMockDefaultMappedResourcePath1() {
        return mockDefaultMappedResourcePath1;
    }

    /**
     * @param mockDefaultMappedResourcePath1 the mockDefaultMappedResourcePath1 to set
     */
    public void setMockDefaultMappedResourcePath1(
            final MappedResourcePath mockDefaultMappedResourcePath1) {
        this.mockDefaultMappedResourcePath1 = mockDefaultMappedResourcePath1;
    }

    /**
     * @return the mockAppleMappedResourcePath1
     */
    public MappedResourcePath getMockAppleMappedResourcePath1() {
        return mockAppleMappedResourcePath1;
    }

    /**
     * @param mockAppleMappedResourcePath1 the mockAppleMappedResourcePath1 to set
     */
    public void setMockAppleMappedResourcePath1(
            final MappedResourcePath mockAppleMappedResourcePath1) {
        this.mockAppleMappedResourcePath1 = mockAppleMappedResourcePath1;
    }

    /**
     * @return the mockHD800MappedResourcePath1
     */
    public MappedResourcePath getMockHD800MappedResourcePath1() {
        return mockHD800MappedResourcePath1;
    }

    /**
     * @param mockHD800MappedResourcePath1 the mockHD800MappedResourcePath1 to set
     */
    public void setMockHD800MappedResourcePath1(
            final MappedResourcePath mockHD800MappedResourcePath1) {
        this.mockHD800MappedResourcePath1 = mockHD800MappedResourcePath1;
    }

    /**
     * @return the mockMediumMappedResourcePath1
     */
    public MappedResourcePath getMockMediumMappedResourcePath1() {
        return mockMediumMappedResourcePath1;
    }

    /**
     * @param mockMediumMappedResourcePath1 the mockMediumMappedResourcePath1 to set
     */
    public void setMockMediumMappedResourcePath1(
            final MappedResourcePath mockMediumMappedResourcePath1) {
        this.mockMediumMappedResourcePath1 = mockMediumMappedResourcePath1;
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

    public MappedResourcePath getMockMediumMappedResourcePath2() {
        return mockMediumMappedResourcePath2;
    }

    public void setMockMediumMappedResourcePath2(
            final MappedResourcePath mockMediumMappedResourcePath2) {
        this.mockMediumMappedResourcePath2 = mockMediumMappedResourcePath2;
    }

    public MappedResourcePath getMockAndroidMappedResourcePath2() {
        return mockAndroidMappedResourcePath2;
    }

    public void setMockAndroidMappedResourcePath2(
            final MappedResourcePath mockAndroidMappedResourcePath2) {
        this.mockAndroidMappedResourcePath2 = mockAndroidMappedResourcePath2;
    }

    public MappedResourcePath getMockDefaultMappedResourcePath2() {
        return mockDefaultMappedResourcePath2;
    }

    public void setMockDefaultMappedResourcePath2(
            final MappedResourcePath mockDefaultMappedResourcePath2) {
        this.mockDefaultMappedResourcePath2 = mockDefaultMappedResourcePath2;
    }

    public MappedResourcePath getMockAppleMappedResourcePath2() {
        return mockAppleMappedResourcePath2;
    }

    public void setMockAppleMappedResourcePath2(
            final MappedResourcePath mockAppleMappedResourcePath2) {
        this.mockAppleMappedResourcePath2 = mockAppleMappedResourcePath2;
    }

    public MappedResourcePath getMockHD800MappedResourcePath2() {
        return mockHD800MappedResourcePath2;
    }

    public void setMockHD800MappedResourcePath2(
            final MappedResourcePath mockHD800MappedResourcePath2) {
        this.mockHD800MappedResourcePath2 = mockHD800MappedResourcePath2;
    }
}

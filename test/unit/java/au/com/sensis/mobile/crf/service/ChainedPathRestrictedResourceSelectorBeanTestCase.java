package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test
 * {@link ChainedPathRestrictedResourceSelectorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ChainedPathRestrictedResourceSelectorBeanTestCase
        extends AbstractJUnit4TestCase {

    private static final String BEAN_NAME = "beanName";

    private static final String [] WILDCARD_EXTENSIONS = new String [] {"*"};

    private ChainedPathRestrictedResourceSelectorBean objectUnderTest;

    private ResourceSelector mockDefaultSelector;
    private PathRestrictedResourceSelector mockPathRestrictedSelector;
    private Object mockBean;
    private Device mockDevice;
    private final ResourcePathTestData resourcePathTestData =
            new ResourcePathTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(
                new ChainedPathRestrictedResourceSelectorBean(
                        getMockDefaultSelector()));
        swapOutRealLoggerForMock(
                ChainedPathRestrictedResourceSelectorBean.class);

    }

    private Logger getMockLogger() {
        return getMockLogger(
                ChainedPathRestrictedResourceSelectorBean.class);
    }

    @Test
    public void testConstructorWhenDefaultSelectorIsNull() throws Throwable {
        try {
            new ChainedPathRestrictedResourceSelectorBean(
                    null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "defaultSelector must not be null", e.getMessage());
        }

    }

    @Test
    public void testPostProcessBeforeInitialization() throws Throwable {
        final Object actualBean =
                getObjectUnderTest().postProcessBeforeInitialization(
                        getMockBean(), BEAN_NAME);

        Assert.assertEquals("actualBean is wrong", getMockBean(), actualBean);

    }

    @Test
    public void testPostProcessAfterInitializationWhenBeanIsOfInterest()
            throws Throwable {

        setupForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

    }

    private void assertForPostProcessAfterInitializationWhenBeanIsOfInterest(final Object bean) {
        final Object actualBean =
                getObjectUnderTest().postProcessAfterInitialization(
                        bean, BEAN_NAME);

        Assert.assertEquals("actualBean is wrong", bean, actualBean);
    }

    private void setupForPostProcessAfterInitializationWhenBeanIsOfInterest(final Object bean) {
        EasyMock.expect(getMockLogger().isInfoEnabled())
                .andReturn(Boolean.TRUE);

        getMockLogger().info(
                "Adding bean to list. Bean name: '" + BEAN_NAME + "'. Bean: "
                        + bean
                        + ".");
    }

    @Test
    public void testPostProcessAfterInitializationWhenBeanIsNotOfInterest()
        throws Throwable {
        final Object actualBean =
            getObjectUnderTest().postProcessAfterInitialization(
                    getMockBean(), BEAN_NAME);

        Assert.assertEquals("actualBean is wrong", getMockBean(), actualBean);

    }

    @Test
    public void testGetResourcePathWhenPathRestrictedSelectorFound() throws Throwable {

        setupForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        EasyMock.expect(
                getMockPathRestrictedSelector().isInterestedIn(
                        getRequestedResourcePath())).andReturn(
                Boolean.TRUE);

        EasyMock.expect(
                getMockPathRestrictedSelector().getResourcePath(
                        getMockDevice(), getRequestedResourcePath()))
                .andReturn(getExpectedMappedResourcePath());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePath(getMockDevice(),
                        getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePath is wrong",
                getExpectedMappedResourcePath(), actualMappedResourcePath);

    }

    private NullMappedResourcePath getExpectedMappedResourcePath() {
        return getResourcePathTestData().getNullMappedResourcePath();
    }

    private List<MappedResourcePath> getExpectedMappedResourcePaths() {
        final List<MappedResourcePath> list = new ArrayList<MappedResourcePath>();
        list.add(getExpectedMappedResourcePath());
        return list;
    }

    private String getRequestedResourcePath() {
        return getResourcePathTestData().getRequestedJspResourcePath();
    }

    @Test
    public void testGetResourcePathWhenNoPathRestrictedSelectorFound()
            throws Throwable {

        EasyMock.expect(
                getMockDefaultSelector().getResourcePath(getMockDevice(),
                        getRequestedResourcePath())).andReturn(
                getExpectedMappedResourcePath());

        replay();

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePath(getMockDevice(),
                        getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePath is wrong",
                getExpectedMappedResourcePath(), actualMappedResourcePath);

    }

    @Test
    public void testGetResourcePathWithExtensionsWhenPathRestrictedSelectorFound()
        throws Throwable {

        setupForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        EasyMock.expect(
                getMockPathRestrictedSelector().isInterestedIn(
                        getRequestedResourcePath())).andReturn(
                Boolean.TRUE);

        EasyMock.expect(
                getMockPathRestrictedSelector().getResourcePathWithExtensions(
                        getMockDevice(), getRequestedResourcePath(), WILDCARD_EXTENSIONS))
                .andReturn(getExpectedMappedResourcePath());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePathWithExtensions(getMockDevice(),
                        getRequestedResourcePath(), WILDCARD_EXTENSIONS);

        Assert.assertEquals("MappedResourcePath is wrong",
                getExpectedMappedResourcePath(), actualMappedResourcePath);

    }

    @Test
    public void testGetResourcePathWithExtensionsWhenNoPathRestrictedSelectorFound()
            throws Throwable {

        EasyMock.expect(
                getMockDefaultSelector().getResourcePathWithExtensions(
                        getMockDevice(), getRequestedResourcePath(),
                        WILDCARD_EXTENSIONS)).andReturn(
                getExpectedMappedResourcePath());

        replay();

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePathWithExtensions(
                        getMockDevice(), getRequestedResourcePath(),
                        WILDCARD_EXTENSIONS);

        Assert.assertEquals("MappedResourcePath is wrong",
                getExpectedMappedResourcePath(), actualMappedResourcePath);

    }

    @Test
    public void testGetAllResourcePathsWhenPathRestrictedSelectorFound()
        throws Throwable {

        setupForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        EasyMock.expect(
                getMockPathRestrictedSelector().isInterestedIn(
                        getRequestedResourcePath())).andReturn(
                Boolean.TRUE);

        EasyMock.expect(
                getMockPathRestrictedSelector().getAllResourcePaths(
                        getMockDevice(), getRequestedResourcePath()))
                .andReturn(getExpectedMappedResourcePaths());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedSelector());

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().getAllResourcePaths(getMockDevice(),
                        getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePaths are wrong",
                getExpectedMappedResourcePaths(), actualMappedResourcePaths);

    }

    @Test
    public void testGetAllResourcePathsWhenNoPathRestrictedSelectorFound()
            throws Throwable {

        EasyMock.expect(
                getMockDefaultSelector().getAllResourcePaths(getMockDevice(),
                        getRequestedResourcePath())).andReturn(
                getExpectedMappedResourcePaths());

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().getAllResourcePaths(
                        getMockDevice(), getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePaths are wrong",
                getExpectedMappedResourcePaths(), actualMappedResourcePaths);

    }

    private ChainedPathRestrictedResourceSelectorBean
        getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(
            final ChainedPathRestrictedResourceSelectorBean
                objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    public ResourceSelector getMockDefaultSelector() {
        return mockDefaultSelector;
    }

    public void setMockDefaultSelector(
            final ResourceSelector mockDefaultSelector) {
        this.mockDefaultSelector = mockDefaultSelector;
    }

    public Object getMockBean() {
        return mockBean;
    }

    public void setMockBean(final Object mockBean) {
        this.mockBean = mockBean;
    }

    public PathRestrictedResourceSelector
        getMockPathRestrictedSelector() {
        return mockPathRestrictedSelector;
    }

    public void setMockPathRestrictedSelector(
            final PathRestrictedResourceSelector
                mockPathRestrictedSelector) {
        this.mockPathRestrictedSelector = mockPathRestrictedSelector;
    }

    public Device getMockDevice() {
        return mockDevice;
    }

    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }

    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }
}

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
 * {@link DelegatingPathRestrictedResourceResolverEngineBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DelegatingPathRestrictedResourceResolverEngineBeanTestCase
        extends AbstractJUnit4TestCase {

    private static final String BEAN_NAME = "beanName";

    private DelegatingPathRestrictedResourceResolverEngineBean objectUnderTest;

    private ResourceResolverEngine mockDefaultResourceResolverEngine;
    private PathRestrictedResourceResolverEngine mockPathRestrictedResourceResolverEngine;
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
                new DelegatingPathRestrictedResourceResolverEngineBean(
                        getMockDefaultResourceResolverEngine()));
        swapOutRealLoggerForMock(
                DelegatingPathRestrictedResourceResolverEngineBean.class);

    }

    private Logger getMockLogger() {
        return getMockLogger(
                DelegatingPathRestrictedResourceResolverEngineBean.class);
    }

    @Test
    public void testConstructorWhenDefaultSelectorIsNull() throws Throwable {
        try {
            new DelegatingPathRestrictedResourceResolverEngineBean(
                    null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "defaultResourceResolverEngine must not be null", e.getMessage());
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
                getMockPathRestrictedResourceResolverEngine());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedResourceResolverEngine());

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
                getMockPathRestrictedResourceResolverEngine());

        EasyMock.expect(
                getMockPathRestrictedResourceResolverEngine().isInterestedIn(
                        getRequestedResourcePath())).andReturn(
                Boolean.TRUE);

        EasyMock.expect(
                getMockPathRestrictedResourceResolverEngine().getResourcePath(
                        getMockDevice(), getRequestedResourcePath()))
                .andReturn(getExpectedMappedResourcePath());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedResourceResolverEngine());

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePath(getMockDevice(),
                        getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePath is wrong",
                getExpectedMappedResourcePath(), actualMappedResourcePath);

    }

    private MappedResourcePath getExpectedMappedResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupResourcePath();
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
                getMockDefaultResourceResolverEngine().getResourcePath(getMockDevice(),
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
    public void testGetAllResourcePathsWhenPathRestrictedSelectorFound()
        throws Throwable {

        setupForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedResourceResolverEngine());

        EasyMock.expect(
                getMockPathRestrictedResourceResolverEngine().isInterestedIn(
                        getRequestedResourcePath())).andReturn(
                Boolean.TRUE);

        EasyMock.expect(
                getMockPathRestrictedResourceResolverEngine().getAllResourcePaths(
                        getMockDevice(), getRequestedResourcePath()))
                .andReturn(getExpectedMappedResourcePaths());

        replay();

        assertForPostProcessAfterInitializationWhenBeanIsOfInterest(
                getMockPathRestrictedResourceResolverEngine());

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
                getMockDefaultResourceResolverEngine().getAllResourcePaths(getMockDevice(),
                        getRequestedResourcePath())).andReturn(
                getExpectedMappedResourcePaths());

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().getAllResourcePaths(
                        getMockDevice(), getRequestedResourcePath());

        Assert.assertEquals("MappedResourcePaths are wrong",
                getExpectedMappedResourcePaths(), actualMappedResourcePaths);

    }

    private DelegatingPathRestrictedResourceResolverEngineBean
        getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(
            final DelegatingPathRestrictedResourceResolverEngineBean
                objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    public ResourceResolverEngine getMockDefaultResourceResolverEngine() {
        return mockDefaultResourceResolverEngine;
    }

    public void setMockDefaultResourceResolverEngine(
            final ResourceResolverEngine mockDefaultResourceResolverEngine) {
        this.mockDefaultResourceResolverEngine = mockDefaultResourceResolverEngine;
    }

    public Object getMockBean() {
        return mockBean;
    }

    public void setMockBean(final Object mockBean) {
        this.mockBean = mockBean;
    }

    public PathRestrictedResourceResolverEngine
        getMockPathRestrictedResourceResolverEngine() {
        return mockPathRestrictedResourceResolverEngine;
    }

    public void setMockPathRestrictedResourceResolverEngine(
            final PathRestrictedResourceResolverEngine
            mockPathRestrictedResourceResolverEngine) {
        this.mockPathRestrictedResourceResolverEngine = mockPathRestrictedResourceResolverEngine;
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

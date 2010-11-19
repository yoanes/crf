package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link DelegatingResourceResolverBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DelegatingResourceResolverBeanTestCase extends AbstractJUnit4TestCase {

    private DelegatingResourceResolverBean objectUnderTest;
    private Device mockDevice;
    private ResourceResolver mockResourceResolver1;
    private ResourceResolver mockResourceResolver2;
    private final GroupTestData groupTestData = new GroupTestData();
    private final ResourceAccumulator resourceAccumulator = new ResourceAccumulatorBean();
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        swapOutRealLoggerForMock(DelegatingResourceResolverBean.class);

        setObjectUnderTest(new DelegatingResourceResolverBean(Arrays.asList(
                getMockResourceResolver1(), getMockResourceResolver2())));
    }

    @Test
    public void testConstructorWithNullResourceResolvers() throws Throwable {
        try {
            new DelegatingResourceResolverBean(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolvers must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWithEmptyResourceResolvers() throws Throwable {

        EasyMock.expect(
                getMockLogger(DelegatingResourceResolverBean.class).isEnabledFor(
                        Level.WARN)).andReturn(Boolean.TRUE);

        getMockLogger(DelegatingResourceResolverBean.class).warn(
                "resourceResolvers is empty. "
                + "This DelegatingResourceResolverBean will always "
                + "return an empty list of resources.");

        replay();

        new DelegatingResourceResolverBean(new ArrayList<ResourceResolver>());

    }

    @Test
    public void testResolveWhenFirstSecondResolverSupportsRequestedPathFound() throws Throwable {

        EasyMock.expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.TRUE);

        EasyMock.expect(getMockResourceResolver1().resolve(
                getRequestedPath(), getMockDevice()))
                .andReturn(getExpectedResources());

        replay();

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(getRequestedPath(),
                    getMockDevice());

        Assert.assertEquals("actualResource is wrong",
                getExpectedResources(), actualResources);
    }

    @Test
    public void testResolveWhenSecondResolverSupportsRequestedPathFound() throws Throwable {

        EasyMock.expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        EasyMock.expect(getMockResourceResolver2().supports(getRequestedPath()))
        .andReturn(Boolean.TRUE);

        EasyMock.expect(getMockResourceResolver2().resolve(
                getRequestedPath(), getMockDevice()))
                .andReturn(getExpectedResources());

        replay();

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(getRequestedPath(), getMockDevice());

        Assert.assertEquals("actualResource is wrong",
                getExpectedResources(), actualResources);
    }

    @Test
    public void testResolveWhenNoResolverSupports() throws Throwable {

        EasyMock
        .expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        EasyMock
        .expect(getMockResourceResolver2().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        replay();

        final List<Resource> actualResources =
            getObjectUnderTest().resolve(getRequestedPath(), getMockDevice());

        Assert.assertNotNull("actualResource should not be null",
                actualResources);
        Assert.assertTrue("actualResource should not be empty", actualResources
                .isEmpty());
    }

    @Test
    public void testSupportsWhenFirstResolverSupportsTrue() throws Throwable {

        EasyMock
        .expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("supports should be true", getObjectUnderTest()
                .supports(getRequestedPath()));
    }

    @Test
    public void testSupportsWhenSecondResolverSupportsTrue() throws Throwable {

        EasyMock
        .expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        EasyMock
        .expect(getMockResourceResolver2().supports(getRequestedPath()))
        .andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("supports should be true", getObjectUnderTest()
                .supports(getRequestedPath()));
    }

    @Test
    public void testSupportsWhenFalse() throws Throwable {

        EasyMock
        .expect(getMockResourceResolver1().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        EasyMock
        .expect(getMockResourceResolver2().supports(getRequestedPath()))
        .andReturn(Boolean.FALSE);

        replay();

        Assert.assertFalse("supports should be false", getObjectUnderTest()
                .supports(getRequestedPath()));
    }

    private List<Resource> getExpectedResources() {
        return Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath());
    }

    private String getRequestedPath() {
        return getResourcePathTestData()
        .getRequestedJspResourcePath();
    }

    /**
     * @return the objectUnderTest
     */
    private DelegatingResourceResolverBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final DelegatingResourceResolverBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockResourceResolver2
     */
    public ResourceResolver getMockResourceResolver2() {
        return mockResourceResolver2;
    }

    /**
     * @param mockResourceResolver2 the mockResourceResolver2 to set
     */
    public void setMockResourceResolver2(final ResourceResolver mockResourceResolver) {
        mockResourceResolver2 = mockResourceResolver;
    }

    /**
     * @return the mockResourceResolver1
     */
    public ResourceResolver getMockResourceResolver1() {
        return mockResourceResolver1;
    }

    /**
     * @param mockResourceResolver1 the mockResourceResolver1 to set
     */
    public void setMockResourceResolver1(final ResourceResolver mockResourceResolver) {
        mockResourceResolver1 = mockResourceResolver;
    }

    /**
     * @return the mockDevice
     */
    public Device getMockDevice() {

        return mockDevice;
    }


    /**
     * @param mockDevice  the mockDevice to set
     */
    public void setMockDevice(final Device mockDevice) {

        this.mockDevice = mockDevice;
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
    protected ResourceAccumulator getResourceAccumulator() {
        return resourceAccumulator;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }
}

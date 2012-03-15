package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceResolverEngineBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverEngineBeanTestCase extends
AbstractJUnit4TestCase {

    private ResourceResolverEngineBean objectUnderTest;
    private ResourceResolver mockResourceResolver;
    private Device mockDevice;
    private Resource mockDefaultResource1;
    private Resource mockDefaultResource2;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new ResourceResolverEngineBean(getMockResourceResolver(),
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


    // Tests //

    @Test
    public void testConstructorWhenResourceResolverIsNull() throws Throwable {
        try {
            new ResourceResolverEngineBean(null, getMockResolutionWarnLogger());
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolver must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWhenResourceResolutionWarnLoggerNull() throws Throwable {
        try {
            new ResourceResolverEngineBean(getMockResourceResolver(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolutionWarnLogger must not be null", e.getMessage());
        }
    }

    @Test
    public void testGetResourceWhenResolverReturnsNoResults() throws Throwable {

        recordResolverReturnsNoResults();

        replay();

        final Resource actualResourcePath =
                getObjectUnderTest().getResource(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertNull(actualResourcePath);
    }

    @Test
    public void testGetResourceWhenResolverReturnsSingleResult() throws Throwable {

        recordResolverReturnsSingleResult();

        replay();

        final Resource actualResourcePath =
                getObjectUnderTest().getResource(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals(getMockDefaultResource1(), actualResourcePath);
    }

    @Test
    public void testGetResourceWhenResolverReturnsMultipleResults() throws Throwable {

        recordResolverReturnsMultipleResults();

        recordLogWarningResolveFoundMultipleDefaultResources();

        replay();

        final Resource actualResourcePath =
                getObjectUnderTest().getResource(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        // Confirm that it returns the first of the multiple results
        Assert.assertEquals(getMockDefaultResource1(), actualResourcePath);
    }

    @Test
    public void testGetAllResourcesWhenResolverReturnsNoResults() throws Throwable {

        recordResolverReturnsNoResults();

        replay();

        final List<Resource> actualResourcePaths =
                getObjectUnderTest().getAllResources(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertTrue(actualResourcePaths.isEmpty());
    }

    @Test
    public void testGetAllResourcesWhenResolverReturnsMultipleResults() throws Throwable {

        recordResolverReturnsMultipleResults();

        replay();

        final List<Resource> actualResourcePaths =
                getObjectUnderTest().getAllResources(getMockDevice(),
                        getResourcePathTestData().getRequestedJspResourcePath());

        Assert.assertEquals(returnMultipleResources(), actualResourcePaths);
    }

    // Record Behaviour //

    private void recordResolverReturnsNoResults() {
        EasyMock.expect(
                getMockResourceResolver().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice())).andReturn(new ArrayList<Resource>());
    }

    private void recordResolverReturnsSingleResult() {

        final ArrayList<Resource> results = new ArrayList<Resource>();
        results.add(getMockDefaultResource1());

        EasyMock.expect(
                getMockResourceResolver().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice())).andReturn(results);
    }

    private void recordResolverReturnsMultipleResults() {

        EasyMock.expect(
                getMockResourceResolver().resolve(
                        getResourcePathTestData().getRequestedJspResourcePath(),
                        getMockDevice())).andReturn(returnMultipleResources());
    }

    private void recordLogWarningResolveFoundMultipleDefaultResources() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled()).andReturn(Boolean.TRUE);

        getMockResolutionWarnLogger().warn("Requested resource '"
                + getResourcePathTestData().getRequestedJspResourcePath()
                + "' resolved to multiple resources when only one was requested. "
                + "Will only return the first. Total found: "
                + Arrays.asList(getMockDefaultResource1(),
                        getMockDefaultResource2()) + ".");
    }


    private ArrayList<Resource> returnMultipleResources() {

        final ArrayList<Resource> results = new ArrayList<Resource>();
        results.add(getMockDefaultResource1());
        results.add(getMockDefaultResource2());

        return results;
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
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
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

    public Resource getMockDefaultResource2() {
        return mockDefaultResource2;
    }

    public void setMockDefaultResource2(
            final Resource mockDefaultResource2) {
        this.mockDefaultResource2 = mockDefaultResource2;
    }
}

package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.DefaultDevice;
import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;



/**
 * Tests the business logic rules of {@link CachedResourceResolverEngineBean}.
 *
 * @author Tony Filipe
 */
public class CachedResourceResolverEngineBeanTestCase
extends AbstractJUnit4TestCase {

    private CachedResourceResolverEngineBean objectUnderTest;

    private ResourceResolverEngineBean mockResourceResolverEngineBean;

    private Device device;
    private String requestedResourcePath;
    private String requestedResourcePath2;
    private List<Resource> resolvedResources;
    private ConcurrentHashMap<Integer, List<Resource>> cache;
    private List<Resource> cachedResolvedResources;
    private final GroupTestData groupTestData = new GroupTestData();


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        device = new DefaultDevice();
        requestedResourcePath = "common/main.jsp";
        requestedResourcePath2 = "common/mapInclude.jsp";

        resolvedResources = new ArrayList<Resource>();
        resolvedResources.add(
                new ResourceBean(requestedResourcePath, "default/common/main.jsp", null,
                        getGroupTestData().createDefaultGroup()));

        cache = new ConcurrentHashMap<Integer, List<Resource>>();

        cachedResolvedResources = new ArrayList<Resource>();
        cachedResolvedResources.add(
                new ResourceBean(requestedResourcePath, "default/common/mapInclude.jsp", null,
                        getGroupTestData().createDefaultGroup()));
    }


    @Test
    public void testConstructorWithNullResourceResolverEngine() throws Throwable {

        try {
            new CachedResourceResolverEngineBean(null, true);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolverEngine must not be null", e.getMessage());
        }
    }

    /**
     * When caching is disabled we expect it to call the underlying ResolverEngineBean's
     * getAllResources method.
     *
     * @throws Throwable
     */
    @Test
    public void testGetAllResourcesWithoutCaching() throws Throwable {

        setObjectUnderTestWithoutCaching();

        EasyMock.expect(
                getMockResourceResolverEngineBean().getAllResources(device, requestedResourcePath))
                .andReturn(resolvedResources);

        replay();

        final List<Resource> actualResolvedResources = getObjectUnderTest().getAllResources(
                device, requestedResourcePath);

        Assert.assertEquals(resolvedResources, actualResolvedResources);

    }

    /**
     * When caching is enabled and the requested resource is in the cache, we expect it to
     * return the cached result and not call the underlying ResolverEngineBean.
     *
     * @throws Throwable
     */
    @Test
    public void testGetAllResourcesWithCachingResourceFoundInCache() throws Throwable {

        setObjectUnderTestWithCaching();

        final int key = getObjectUnderTest().generateHashKey(device, requestedResourcePath);
        cache.put(key, cachedResolvedResources);
        getObjectUnderTest().setResourceMapCache(cache);

        replay();

        final List<Resource> actualResolvedResources = getObjectUnderTest().getAllResources(
                device, requestedResourcePath);

        Assert.assertEquals(cachedResolvedResources, actualResolvedResources);
    }

    /**
     * When caching is enabled and the requested resource is NOT in the cache, we expect it to
     * call the underlying ResolverEngineBean's getAllResources method and then add it to the cache.
     *
     * @throws Throwable
     */
    @Test
    public void testGetAllResourcesWithCachingResourceNotFoundInCache() throws Throwable {

        setObjectUnderTestWithCaching();

        // Cache contains resuls for requestedResourcePath
        final int key = getObjectUnderTest().generateHashKey(device, requestedResourcePath);
        cache.put(key, cachedResolvedResources);
        getObjectUnderTest().setResourceMapCache(cache);

        // Return a different result to what's in the cache
        EasyMock.expect(
                getMockResourceResolverEngineBean().getAllResources(device, requestedResourcePath2))
                .andReturn(resolvedResources);

        replay();

        // Request a different resource to what's in the cache
        final List<Resource> actualResolvedResources = getObjectUnderTest().getAllResources(
                device, requestedResourcePath2);

        // Result is from the ResourceResolverEngineBean not the cache
        Assert.assertEquals(resolvedResources, actualResolvedResources);

        final int newkey = getObjectUnderTest().generateHashKey(device, requestedResourcePath2);
        Assert.assertTrue(cache.containsKey(newkey));
    }


    /**
     * When caching is disabled we expect it to call the underlying ResolverEngineBean's
     * getResource method.
     *
     * @throws Throwable
     */
    @Test
    public void testGetResourceWithoutCaching() throws Throwable {

        setObjectUnderTestWithoutCaching();

        // getResource only returns a single Resource
        EasyMock.expect(
                getMockResourceResolverEngineBean().getResource(device, requestedResourcePath))
                .andReturn(resolvedResources.get(0));

        replay();

        final Resource actualResolvedResource = getObjectUnderTest().getResource(
                device, requestedResourcePath);

        Assert.assertEquals(resolvedResources.get(0), actualResolvedResource);
    }

    /**
     * When caching is enabled and the requested resource is in the cache, we expect it to
     * return the cached result and not call the underlying ResolverEngineBean.
     *
     * @throws Throwable
     */
    @Test
    public void testGetResourceWithCachingResourceFoundInCache() throws Throwable {

        setObjectUnderTestWithCaching();

        final int key = getObjectUnderTest().generateHashKey(device, requestedResourcePath);
        cache.put(key, cachedResolvedResources);
        getObjectUnderTest().setResourceMapCache(cache);

        replay();

        final Resource actualResolvedResource = getObjectUnderTest().getResource(
                device, requestedResourcePath);

        Assert.assertEquals(cachedResolvedResources.get(0), actualResolvedResource);
    }

    /**
     * When caching is enabled and the requested resource is NOT in the cache, we expect it to
     * call the underlying ResolverEngineBean's getResource method and then add it to the cache.
     *
     * @throws Throwable
     */
    @Test
    public void testGetResourceWithCachingResourceNotFoundInCache() throws Throwable {

        setObjectUnderTestWithCaching();

        // Cache contains resuls for requestedResourcePath
        final int key = getObjectUnderTest().generateHashKey(device, requestedResourcePath);
        cache.put(key, cachedResolvedResources);
        getObjectUnderTest().setResourceMapCache(cache);

        // Return a different result to what's in the cache
        EasyMock.expect(
                getMockResourceResolverEngineBean().getResource(device, requestedResourcePath2))
                .andReturn(resolvedResources.get(0));

        replay();

        // Request a different resource to what's in the cache
        final Resource actualResolvedResource = getObjectUnderTest().getResource(
                device, requestedResourcePath2);

        // Result is from the ResourceResolverEngineBean not the cache
        Assert.assertEquals(resolvedResources.get(0), actualResolvedResource);

        final int newkey = getObjectUnderTest().generateHashKey(device, requestedResourcePath2);
        Assert.assertTrue(cache.containsKey(newkey));
    }


    private void setObjectUnderTestWithCaching() {
        setObjectUnderTest(
                new CachedResourceResolverEngineBean(getMockResourceResolverEngineBean(), true));
    }

    private void setObjectUnderTestWithoutCaching() {
        setObjectUnderTest(
                new CachedResourceResolverEngineBean(getMockResourceResolverEngineBean(), false));
    }

    /**
     * @return the objectUnderTest
     */
    protected CachedResourceResolverEngineBean getObjectUnderTest() {

        return objectUnderTest;
    }

    /**
     * @param objectUnderTest  the objectUnderTest to set
     */
    protected void setObjectUnderTest(final CachedResourceResolverEngineBean objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockResourceResolverEngineBean
     */
    public ResourceResolverEngineBean getMockResourceResolverEngineBean() {

        return mockResourceResolverEngineBean;
    }

    /**
     * @param mockResourceResolverEngineBean  the mockResourceResolverEngineBean to set
     */
    public void setMockResourceResolverEngineBean(
            final ResourceResolverEngineBean mockResourceResolverEngineBean) {

        this.mockResourceResolverEngineBean = mockResourceResolverEngineBean;
    }


    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

}

package au.com.sensis.mobile.crf.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceBean;
import au.com.sensis.mobile.crf.service.ResourceCache;
import au.com.sensis.mobile.crf.service.ResourceCacheKeyBean;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceCacheFillerBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceCacheFillerBeanTestCase extends AbstractJUnit4TestCase {

    private ResourceCacheFillerBean objectUnderTest;
    private ResourceCache mockResourceCache;
    private File rootResourcesDir;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ResourceCacheFillerBean(getMockResourceCache()));

        setRootResourcesDir(new File(getClass().getResource("/").toURI()));
        getObjectUnderTest().setEnabled(true);
    }

    @Test
    public void testIsDisabledByDefault() throws Throwable {
        setObjectUnderTest(new ResourceCacheFillerBean(getMockResourceCache()));

        Assert.assertFalse("isEnabled() should be false", getObjectUnderTest().isEnabled());
    }

    @Test
    public void testClearCacheWhenEnabled() throws Throwable {

        getMockResourceCache().removeAll();

        replay();

        getObjectUnderTest().clearCache();
    }

    @Test
    public void testClearCacheWhenDisabled() throws Throwable {
        getObjectUnderTest().setEnabled(false);

        replay();

        getObjectUnderTest().clearCache();
    }

    @Test
    public void testFillCacheWhenEnabled() throws Throwable {

        Resource[] resources = createResources(createResourceCacheKeyBean1(), createGroup(0));
        getMockResourceCache().put(EasyMock.eq(createResourceCacheKeyBean1()),
                EasyMock.aryEq(resources));

        resources = createResources(createResourceCacheKeyBean2(), createGroup(1));
        getMockResourceCache().put(EasyMock.eq(createResourceCacheKeyBean2()),
                EasyMock.aryEq(resources));

        resources = createResources(createResourceCacheKeyBean3(), createGroup(0));
        getMockResourceCache().put(EasyMock.eq(createResourceCacheKeyBean3()),
                EasyMock.aryEq(resources));

        resources = createResources(createResourceCacheKeyBean4(), createGroup(1));
        getMockResourceCache().put(EasyMock.eq(createResourceCacheKeyBean4()),
                EasyMock.aryEq(resources));

        replay();

        getObjectUnderTest().fillCache(2, 2, 2);
    }

    @Test
    public void testFillCacheWhenDisabled() throws Throwable {
        getObjectUnderTest().setEnabled(false);

        replay();

        getObjectUnderTest().fillCache(2, 2, 2);
    }

    private ResourceCacheKeyBean createResourceCacheKeyBean1() {
        return new ResourceCacheKeyBean(ResourceCacheFillerBean.BASE_ABSTRACT_REQUESTED_PATH + "0",
                new Group [] {createGroup(0)});
    }

    private ResourceCacheKeyBean createResourceCacheKeyBean2() {
        return new ResourceCacheKeyBean(ResourceCacheFillerBean.BASE_ABSTRACT_REQUESTED_PATH + "0",
                new Group [] {createGroup(1)});
    }

    private ResourceCacheKeyBean createResourceCacheKeyBean3() {
        return new ResourceCacheKeyBean(ResourceCacheFillerBean.BASE_ABSTRACT_REQUESTED_PATH + "1",
                new Group [] {createGroup(0)});
    }

    private ResourceCacheKeyBean createResourceCacheKeyBean4() {
        return new ResourceCacheKeyBean(ResourceCacheFillerBean.BASE_ABSTRACT_REQUESTED_PATH + "1",
                new Group [] {createGroup(1)});
    }

    private Group createGroup(final int groupNumber) {
        final Group group = new Group();
        group.setName(ResourceCacheFillerBean.BASE_GROUP_NAME + groupNumber);
        group.setExpr(ResourceCacheFillerBean.BASE_EXPR + groupNumber);
        return group;
    }

    private Resource[] createResources(final ResourceCacheKeyBean resourceCacheKeyBean,
            final Group group) {
        final List<Resource> resources = new ArrayList<Resource>();

        Resource resource =
                new ResourceBean(resourceCacheKeyBean.getRequestedResourcePath(),
                        resourceCacheKeyBean.getRequestedResourcePath() + "/" + group.getName()
                                + "/" + 0, getRootResourcesDir(), group);
        resources.add(resource);

        resource =
                new ResourceBean(resourceCacheKeyBean.getRequestedResourcePath(),
                        resourceCacheKeyBean.getRequestedResourcePath() + "/" + group.getName()
                                + "/" + 1, getRootResourcesDir(), group);
        resources.add(resource);

        return resources.toArray(new Resource[] {});
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceCacheFillerBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ResourceCacheFillerBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockResourceCache
     */
    public ResourceCache getMockResourceCache() {
        return mockResourceCache;
    }

    /**
     * @param mockResourceCache the mockResourceCache to set
     */
    public void setMockResourceCache(final ResourceCache mockResourceCache) {
        this.mockResourceCache = mockResourceCache;
    }

    /**
     * @return the rootResourcesDir
     */
    private File getRootResourcesDir() {
        return rootResourcesDir;
    }

    /**
     * @param rootResourcesDir the rootResourcesDir to set
     */
    private void setRootResourcesDir(final File rootResourcesDir) {
        this.rootResourcesDir = rootResourcesDir;
    }
}

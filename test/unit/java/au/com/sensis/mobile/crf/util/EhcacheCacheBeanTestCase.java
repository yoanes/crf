package au.com.sensis.mobile.crf.util;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link EhcacheCacheBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class EhcacheCacheBeanTestCase extends AbstractJUnit4TestCase {

    private static final String USER_AGENT = "myUserAgent";

    private EhcacheCacheBean<String, Group> objectUnderTest;
    private Ehcache mockEhcache;
    private final GroupTestData groupTestData = new GroupTestData();

    @Test
    public void testIsEnabledWhenTrue() throws Throwable {
        Assert.assertTrue("isEnabled should be true", createEnabledEhcacheCacheBean()
                .isEnabled());
    }

    @Test
    public void testIsEnabledWhenFalse() throws Throwable {
        Assert.assertFalse("isEnabled should be false", createDisabledEhcacheCacheBean()
                .isEnabled());

    }

    @Test
    public void testContainsWhenTrueAndCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledEhcacheCacheBean());

        EasyMock.expect(getMockEhcache().isKeyInCache(USER_AGENT)).andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("contains should be true", getObjectUnderTest()
                .contains(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledEhcacheCacheBean());

        EasyMock.expect(getMockEhcache().isKeyInCache(USER_AGENT)).andReturn(Boolean.TRUE);

        final Group[] expectedGroups = new Group[] { getGroupTestData().createAppleGroup() };
        final Element element = new Element(USER_AGENT, expectedGroups);

        EasyMock.expect(getMockEhcache().get(USER_AGENT)).andReturn(element);

        replay();

        Assert.assertArrayEquals("get returned wrong groups", expectedGroups, getObjectUnderTest()
                .get(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheDisabled() throws Throwable {
        setObjectUnderTest(createDisabledEhcacheCacheBean());

        final Group[] groups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, groups);

        Assert.assertArrayEquals("get returned wrong groups", null,
                getObjectUnderTest().get(USER_AGENT));

    }

    @Test
    public void testContainsWhenFalseAndCacheEnabled() throws Throwable {
        Assert.assertFalse("contains should be false", createEnabledEhcacheCacheBean()
                .contains(USER_AGENT));

    }

    @Test
    public void testContainsWhenCacheDisabled() throws Throwable {

        Assert.assertFalse("contains should be false", createDisabledEhcacheCacheBean()
                .contains(USER_AGENT));

    }

    @Test
    public void testPutWhenCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledEhcacheCacheBean());

        final Group[] expectedGroups = new Group[] { getGroupTestData().createAppleGroup() };
        final Element element = new Element(USER_AGENT, expectedGroups);

        getMockEhcache().put(element);

        replay();

        getObjectUnderTest().put(USER_AGENT, expectedGroups);
    }

    @Test
    public void testPutWhenCacheDisabled() throws Throwable {
        setObjectUnderTest(createDisabledEhcacheCacheBean());

        final Group[] groups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, groups);

        Assert.assertArrayEquals("get returned wrong groups", null,
                getObjectUnderTest().get(USER_AGENT));

    }


    private EhcacheCacheBean<String, Group> createEnabledEhcacheCacheBean() {
        return new EhcacheCacheBean<String, Group>(getMockEhcache(), true);
    }

    private EhcacheCacheBean<String, Group> createDisabledEhcacheCacheBean() {
        return new EhcacheCacheBean<String, Group>(getMockEhcache(), false);
    }

    /**
     * @return the objectUnderTest
     */
    private EhcacheCacheBean<String, Group> getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final EhcacheCacheBean<String, Group> objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the mockEhcache
     */
    public Ehcache getMockEhcache() {
        return mockEhcache;
    }

    /**
     * @param mockEhcache the mockEhcache to set
     */
    public void setMockEhcache(final Ehcache mockEhcache) {
        this.mockEhcache = mockEhcache;
    }
}

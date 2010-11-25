package au.com.sensis.mobile.crf.util;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ConcurrentMapCacheBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class ConcurrentMapCacheBeanTestCase extends AbstractJUnit4TestCase {

    private static final String USER_AGENT = "myUserAgent";

    private ConcurrentMapCacheBean<String, Group> objectUnderTest;
    private final GroupTestData groupTestData = new GroupTestData();

    @Test
    public void testIsEnabledWhenTrue() throws Throwable {
        Assert.assertTrue("isEnabled should be true", createEnabledConcurrentMapCacheBean()
                .isEnabled());
    }

    @Test
    public void testIsEnabledWhenFalse() throws Throwable {
        Assert.assertFalse("isEnabled should be false", createDisabledConcurrentMapCacheBean()
                .isEnabled());

    }

    @Test
    public void testContainsWhenTrueAndCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledConcurrentMapCacheBean());

        getObjectUnderTest().put(USER_AGENT, new Group [] {getGroupTestData().createAppleGroup()});

        Assert.assertTrue("contains should be true", getObjectUnderTest()
                .contains(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledConcurrentMapCacheBean());

        final Group[] expectedGroups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, expectedGroups);

        Assert.assertArrayEquals("get returned wrong groups", expectedGroups,
                getObjectUnderTest().get(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheDisabled() throws Throwable {
        setObjectUnderTest(createDisabledConcurrentMapCacheBean());

        final Group[] groups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, groups);

        Assert.assertArrayEquals("get returned wrong groups", null,
                getObjectUnderTest().get(USER_AGENT));

    }

    @Test
    public void testContainsWhenFalseAndCacheEnabled() throws Throwable {
        Assert.assertFalse("contains should be false", createEnabledConcurrentMapCacheBean()
                .contains(USER_AGENT));

    }

    @Test
    public void testContainsWhenCacheDisabled() throws Throwable {

        Assert.assertFalse("contains should be false", createDisabledConcurrentMapCacheBean()
                .contains(USER_AGENT));

    }

    private ConcurrentMapCacheBean<String, Group> createEnabledConcurrentMapCacheBean() {
        return new ConcurrentMapCacheBean<String, Group>(true);
    }

    private ConcurrentMapCacheBean<String, Group> createDisabledConcurrentMapCacheBean() {
        return new ConcurrentMapCacheBean<String, Group>(false);
    }

    /**
     * @return the objectUnderTest
     */
    private ConcurrentMapCacheBean<String, Group> getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ConcurrentMapCacheBean<String, Group> objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }
}

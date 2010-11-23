package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link SimpleGroupsCacheBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class SimpleGroupsCacheBeanTestCase extends AbstractJUnit4TestCase {

    private static final String USER_AGENT = "myUserAgent";

    private SimpleGroupsCacheBean objectUnderTest;
    private final GroupTestData groupTestData = new GroupTestData();

    @Test
    public void testIsEnabledWhenTrue() throws Throwable {
        Assert.assertTrue("isEnabled should be true", createEnabledSimpleGroupsCacheBean()
                .isEnabled());
    }

    @Test
    public void testIsEnabledWhenFalse() throws Throwable {
        Assert.assertFalse("isEnabled should be false", createDisabledSimpleGroupsCacheBean()
                .isEnabled());

    }

    @Test
    public void testContainsWhenTrueAndCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledSimpleGroupsCacheBean());

        getObjectUnderTest().put(USER_AGENT, new Group [] {getGroupTestData().createAppleGroup()});

        Assert.assertTrue("contains should be true", getObjectUnderTest()
                .contains(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledSimpleGroupsCacheBean());

        final Group[] expectedGroups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, expectedGroups);

        Assert.assertArrayEquals("get returned wrong groups", expectedGroups,
                getObjectUnderTest().get(USER_AGENT));

    }

    @Test
    public void testGetWhenCacheDisabled() throws Throwable {
        setObjectUnderTest(createDisabledSimpleGroupsCacheBean());

        final Group[] groups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(USER_AGENT, groups);

        Assert.assertArrayEquals("get returned wrong groups", null,
                getObjectUnderTest().get(USER_AGENT));

    }

    @Test
    public void testContainsWhenFalseAndCacheEnabled() throws Throwable {
        Assert.assertFalse("contains should be false", createEnabledSimpleGroupsCacheBean()
                .contains(USER_AGENT));

    }

    @Test
    public void testContainsWhenCacheDisabled() throws Throwable {

        Assert.assertFalse("contains should be false", createDisabledSimpleGroupsCacheBean()
                .contains(USER_AGENT));

    }

    private SimpleGroupsCacheBean createEnabledSimpleGroupsCacheBean() {
        return new SimpleGroupsCacheBean(true);
    }

    private SimpleGroupsCacheBean createDisabledSimpleGroupsCacheBean() {
        return new SimpleGroupsCacheBean(false);
    }

    /**
     * @return the objectUnderTest
     */
    private SimpleGroupsCacheBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final SimpleGroupsCacheBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }
}

package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Before;
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
    private static final String CONFIG_PATH = "component/map";

    private SimpleGroupsCacheBean objectUnderTest;
    private final GroupTestData groupTestData = new GroupTestData();
    private GroupsCacheKey groupsCacheKey;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setGroupsCacheKey(new GroupsCacheKeyBean(USER_AGENT, CONFIG_PATH));
    }

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

        getObjectUnderTest().put(getGroupsCacheKey(),
                new Group[] { getGroupTestData().createAppleGroup() });

        Assert.assertTrue("contains should be true", getObjectUnderTest().contains(
                getGroupsCacheKey()));

    }

    @Test
    public void testGetWhenCacheEnabled() throws Throwable {
        setObjectUnderTest(createEnabledSimpleGroupsCacheBean());

        final Group[] expectedGroups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(getGroupsCacheKey(), expectedGroups);

        Assert.assertArrayEquals("get returned wrong groups", expectedGroups,
                getObjectUnderTest().get(getGroupsCacheKey()));

    }

    @Test
    public void testGetWhenCacheDisabled() throws Throwable {
        setObjectUnderTest(createDisabledSimpleGroupsCacheBean());

        final Group[] groups = new Group [] {getGroupTestData().createAppleGroup()};
        getObjectUnderTest().put(getGroupsCacheKey(), groups);

        Assert.assertArrayEquals("get returned wrong groups", null,
                getObjectUnderTest().get(getGroupsCacheKey()));

    }

    @Test
    public void testContainsWhenFalseAndCacheEnabled() throws Throwable {
        Assert.assertFalse("contains should be false", createEnabledSimpleGroupsCacheBean()
                .contains(getGroupsCacheKey()));

    }

    @Test
    public void testContainsWhenCacheDisabled() throws Throwable {

        Assert.assertFalse("contains should be false", createDisabledSimpleGroupsCacheBean()
                .contains(getGroupsCacheKey()));

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

    /**
     * @return the groupsCacheKey
     */
    private GroupsCacheKey getGroupsCacheKey() {
        return groupsCacheKey;
    }

    /**
     * @param groupsCacheKey the groupsCacheKey to set
     */
    private void setGroupsCacheKey(final GroupsCacheKey groupsCacheKey) {
        this.groupsCacheKey = groupsCacheKey;
    }
}

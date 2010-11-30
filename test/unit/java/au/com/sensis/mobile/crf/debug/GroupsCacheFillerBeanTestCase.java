package au.com.sensis.mobile.crf.debug;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupsCache;
import au.com.sensis.mobile.crf.config.GroupsCacheKeyBean;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link GroupsCacheFillerBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsCacheFillerBeanTestCase extends AbstractJUnit4TestCase {

    private GroupsCacheFillerBean objectUnderTest;
    private GroupsCache mockGroupsCache;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new GroupsCacheFillerBean(getMockGroupsCache()));

        getObjectUnderTest().setEnabled(true);
    }

    @Test
    public void testIsDisabledByDefault() throws Throwable {
        setObjectUnderTest(new GroupsCacheFillerBean(getMockGroupsCache()));

        Assert.assertFalse("isEnabled() should be false", getObjectUnderTest().isEnabled());
    }

    @Test
    public void testClearCacheWhenEnabled() throws Throwable {

        getMockGroupsCache().removeAll();

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

        getMockGroupsCache().put(EasyMock.eq(createGroupsCacheKeyBean(0, 0)),
                EasyMock.aryEq(createGroupsArray()));
        getMockGroupsCache().put(EasyMock.eq(createGroupsCacheKeyBean(0, 1)),
                EasyMock.aryEq(createGroupsArray()));
        getMockGroupsCache().put(EasyMock.eq(createGroupsCacheKeyBean(1, 0)),
                EasyMock.aryEq(createGroupsArray()));
        getMockGroupsCache().put(EasyMock.eq(createGroupsCacheKeyBean(1, 1)),
                EasyMock.aryEq(createGroupsArray()));

        replay();

        getObjectUnderTest().fillCache(2, 2, 2);
    }

    @Test
    public void testFillCacheWhenDisabled() throws Throwable {
        getObjectUnderTest().setEnabled(false);

        replay();

        getObjectUnderTest().fillCache(2, 2, 2);
    }

    private Group[] createGroupsArray() {
        return new Group[] { createGroup(0), createGroup(1) };
    }

    private Group createGroup(final int groupNumber) {
        final Group group = new Group();
        group.setName(ResourceCacheFillerBean.BASE_GROUP_NAME + groupNumber);
        group.setExpr(ResourceCacheFillerBean.BASE_EXPR + groupNumber);
        return group;
    }

    private GroupsCacheKeyBean createGroupsCacheKeyBean(final int userAgentNumber,
            final int uiConfiurationNumber) {
        return new GroupsCacheKeyBean(GroupsCacheFillerBean.BASE_USER_AGENT + userAgentNumber,
                GroupsCacheFillerBean.UI_CONFIGURATION_PATH + uiConfiurationNumber);
    }

    /**
     * @return the objectUnderTest
     */
    private GroupsCacheFillerBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final GroupsCacheFillerBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockGroupsCache
     */
    public GroupsCache getMockGroupsCache() {
        return mockGroupsCache;
    }

    /**
     * @param mockGroupsCache the mockGroupsCache to set
     */
    public void setMockGroupsCache(final GroupsCache mockGroupsCache) {
        this.mockGroupsCache = mockGroupsCache;
    }

}

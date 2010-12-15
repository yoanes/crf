package au.com.sensis.mobile.crf.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link UiConfiguration}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfigurationTestCase extends AbstractJUnit4TestCase {

    private static final String USER_AGENT = "myUserAgent";
    private static final String CONFIG_PATH = "component/map";

    private UiConfiguration objectUnderTest;
    private Groups mockGroups;
    private Device mockDevice;
    private Iterator<Group> mockGroupIterator;
    private GroupsCache mockGroupsCache;
    private List<Group> groups;
    private final GroupTestData groupTestData = new GroupTestData();
    private GroupsCacheKey groupsCacheKey;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new UiConfiguration());
        getObjectUnderTest().setConfigPath(CONFIG_PATH);
        getObjectUnderTest().setGroups(getMockGroups());
        getObjectUnderTest().setMatchingGroupsCache(getMockGroupsCache());

        setGroups(Arrays.asList(getGroupTestData().createAppleGroup(), getGroupTestData()
                .createDefaultGroup()));

        setGroupsCacheKey(new GroupsCacheKeyBean(USER_AGENT, CONFIG_PATH));
    }

    @Test
    public void testAppliesToPathWhenConfigPathIsNonEmptyAndResultIsTrue() throws Throwable {
        final String[] testValues =
                { "component/map/map.css", "/WEB-INF/view/jsp/component/map/render.jsp" };

        for (final String testValue : testValues) {
            Assert.assertTrue("appliesToPath should be true for testValue: '" + testValue + "'",
                    getObjectUnderTest().appliesToPath(testValue));
        }
    }

    @Test
    public void testAppliesToPathWhenConfigPathIsEmptyAndResultIsTrue() throws Throwable {
        final String[] testValues =
                { "component/map/map.css", "/WEB-INF/view/jsp/component/map/render.jsp" };

        getObjectUnderTest().setConfigPath(StringUtils.EMPTY);

        for (final String testValue : testValues) {
            Assert.assertTrue("appliesToPath should be true for testValue: '" + testValue + "'",
                    getObjectUnderTest().appliesToPath(testValue));
        }
    }

    @Test
    public void testAppliesToPathWhenFalse() throws Throwable {
        Assert.assertFalse("appliesToPath should be false", getObjectUnderTest().appliesToPath(
            "common/main.css"));
    }

    @Test
    public void testAppliesToPathWhenRequestedPathNull() throws Throwable {
        Assert
                .assertFalse("appliesToPath should be false", getObjectUnderTest().appliesToPath(
                        null));
    }

    @Test
    public void testHasDefaultConfigPathWhenTrue() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            getObjectUnderTest().setConfigPath(testValue);
            Assert.assertTrue("hasDefaultConfigPath() should be true for testValue: '" + testValue
                    + "'", getObjectUnderTest().hasDefaultConfigPath());
        }
    }

    @Test
    public void testHasDefaultConfigPathWhenFalse() throws Throwable {

        Assert.assertFalse("hasDefaultConfigPath() should be false", getObjectUnderTest()
                .hasDefaultConfigPath());
    }

    @Test
    public void testGroupIterator() throws Throwable {

        EasyMock.expect(getMockGroups().groupIterator()).andReturn(
                getMockGroupIterator());

        replay();

        final Iterator<Group> actualIterator =
                getObjectUnderTest().groupIterator();

        Assert.assertEquals("iterator is wrong", getMockGroupIterator(),
                actualIterator);
    }

    @Test
    public void testMatchingGroupIteratorWhenNotCached() throws Throwable {

        EasyMock.expect(getMockDevice().getUserAgent()).andReturn(USER_AGENT).atLeastOnce();

        EasyMock.expect(getMockGroupsCache().contains(getGroupsCacheKey())).andReturn(Boolean.FALSE)
                .atLeastOnce();

        EasyMock.expect(getMockGroups().matchingGroups(getMockDevice())).andReturn(
                getGroups());

        getMockGroupsCache().put(EasyMock.eq(getGroupsCacheKey()),
                EasyMock.aryEq(getGroups().toArray(new Group[] {})));

        replay();

        final Iterator<Group> actualIterator =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());

        Assert.assertNotNull("iterator should not be null", actualIterator);
        Assert.assertTrue("iterator should have a first item", actualIterator.hasNext());
        Assert.assertEquals("first item is wrong", getGroups().get(0), actualIterator.next());

        Assert.assertTrue("iterator should have a second item", actualIterator.hasNext());
        Assert.assertEquals("second item is wrong", getGroups().get(1), actualIterator.next());
        Assert.assertFalse("iterator should only have two items", actualIterator.hasNext());
    }

    @Test
    public void testMatchingGroupsWhenNotCached() throws Throwable {

        EasyMock.expect(getMockDevice().getUserAgent()).andReturn(USER_AGENT).atLeastOnce();

        EasyMock.expect(getMockGroupsCache().contains(getGroupsCacheKey())).andReturn(Boolean.FALSE)
        .atLeastOnce();

        EasyMock.expect(getMockGroups().matchingGroups(getMockDevice())).andReturn(
                getGroups());

        getMockGroupsCache().put(EasyMock.eq(getGroupsCacheKey()),
                EasyMock.aryEq(getGroups().toArray(new Group[] {})));

        replay();

        final Group [] actualGroups =
            getObjectUnderTest().matchingGroups(getMockDevice());

        Assert.assertArrayEquals("groups are wrong", getGroups().toArray(), actualGroups);
    }

    @Test
    public void testMatchingGroupIteratorWhenCached() throws Throwable {

        EasyMock.expect(getMockDevice().getUserAgent()).andReturn(USER_AGENT).atLeastOnce();

        EasyMock.expect(getMockGroupsCache().contains(getGroupsCacheKey())).andReturn(Boolean.TRUE)
                .atLeastOnce();

        EasyMock.expect(getMockGroupsCache().get(getGroupsCacheKey())).andReturn(
                getGroups().toArray(new Group[] {}));

        replay();

        final Iterator<Group> actualIterator =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());

        Assert.assertNotNull("iterator should not be null", actualIterator);
        Assert.assertTrue("iterator should have a first item", actualIterator.hasNext());
        Assert.assertEquals("first item is wrong", getGroups().get(0), actualIterator.next());

        Assert.assertTrue("iterator should have a second item", actualIterator.hasNext());
        Assert.assertEquals("second item is wrong", getGroups().get(1), actualIterator.next());
        Assert.assertFalse("iterator should only have two items", actualIterator.hasNext());
    }

    @Test
    public void testMatchingGroupsWhenCached() throws Throwable {

        EasyMock.expect(getMockDevice().getUserAgent()).andReturn(USER_AGENT).atLeastOnce();

        EasyMock.expect(getMockGroupsCache().contains(getGroupsCacheKey())).andReturn(Boolean.TRUE)
        .atLeastOnce();

        EasyMock.expect(getMockGroupsCache().get(getGroupsCacheKey())).andReturn(
                getGroups().toArray(new Group[] {}));

        replay();

        final Group [] actualGroups =
            getObjectUnderTest().matchingGroups(getMockDevice());

        Assert.assertArrayEquals("groups are wrong", getGroups().toArray(), actualGroups);
    }

    /**
     * @return the objectUnderTest
     */
    private UiConfiguration getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final UiConfiguration objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockGroups
     */
    public Groups getMockGroups() {
        return mockGroups;
    }

    /**
     * @param mockGroups the mockGroups to set
     */
    public void setMockGroups(final Groups mockGroups) {
        this.mockGroups = mockGroups;
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
     * @return the mockGroupIterator
     */
    public Iterator<Group> getMockGroupIterator() {
        return mockGroupIterator;
    }

    /**
     * @param mockGroupIterator the mockGroupIterator to set
     */
    public void setMockGroupIterator(
            final Iterator<Group> mockGroupIterator) {
        this.mockGroupIterator = mockGroupIterator;
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

    /**
     * @return the groups
     */
    private List<Group> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    private void setGroups(final List<Group> groups) {
        this.groups = groups;
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

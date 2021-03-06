package au.com.sensis.mobile.crf.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link Groups}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsTestCase extends AbstractJUnit4TestCase {

    private static final String DEFAULT_GROUP_NAME = "default";

    private static final String IPHONE_GROUP_NAME = "iphone";

    private static final String ANDROID_OS_GROUP_NAME = "androidOs";

    private Groups objectUnderTest;

    private Group mockAppleIphoneGroup;
    private Group mockAndroidOsGroup;
    private DefaultGroup mockDefaultGroup;

    private Device mockDevice;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        final Groups groups = new Groups();
        setObjectUnderTest(groups);
    }

    private void initObjectUnderTestWithMocks() {
        // setGroups invokes methods on its arguments. So each test needs to
        // record the expected
        // behaviour (recordGetGroupNames), replay, then call this
        // initObjectUnderTestWithMocks.
        getObjectUnderTest().setGroups(
                new Group[] { getMockAppleIphoneGroup(), getMockAndroidOsGroup() });
        getObjectUnderTest().setDefaultGroup(getMockDefaultGroup());
    }

    @Test
    public void testGroupIteratorWithEmptyGroups() throws Throwable {
        final Groups groups = new Groups();

        Assert.assertFalse("hasNext() should initially be false", groups
                .groupIterator().hasNext());

    }

    @Test
    public void testSetGroupsWithNull() throws Throwable {
        final Groups groups = new Groups();
        try {
            groups.setGroups(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "groups must not be null", e.getMessage());
        }
    }

    @Test
    public void testGroupIterator() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        replay();

        initObjectUnderTestWithMocks();

        final Iterator<Group> itGroups = getObjectUnderTest().groupIterator();
        Assert.assertTrue("hasNext() should initially be true", itGroups
                .hasNext());
        Assert.assertEquals("first next() is wrong", getMockAppleIphoneGroup(),
                itGroups.next());

        Assert.assertTrue("hasNext() should still be true after first next()",
                itGroups.hasNext());
        Assert.assertEquals("second next() is wrong", getMockAndroidOsGroup(),
                itGroups.next());

        Assert.assertTrue("hasNext() should still be true after second next()",
                itGroups.hasNext());
        Assert.assertEquals("third next() is wrong", getMockDefaultGroup(),
                itGroups.next());

        Assert.assertFalse("hasNext() should now be false", itGroups.hasNext());

    }

    @Test
    public void testMatchingGroupIteratorWhenNoMatches() throws Throwable {

        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice()))
        .andReturn(Boolean.FALSE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice()))
        .andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice()))
        .andReturn(Boolean.FALSE);

        replay();

        initObjectUnderTestWithMocks();

        final Iterator<Group> itGroups =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());
        Assert.assertFalse("hasNext() should be false", itGroups.hasNext());
    }

    private void recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked() {
        EasyMock.expect(getMockAppleIphoneGroup().getName()).andReturn(IPHONE_GROUP_NAME)
        .atLeastOnce();
        getMockAppleIphoneGroup().setIndex(0);
        getMockAppleIphoneGroup().setParentGroups(getObjectUnderTest());

        EasyMock.expect(getMockAndroidOsGroup().getName()).andReturn(ANDROID_OS_GROUP_NAME)
        .atLeastOnce();
        getMockAndroidOsGroup().setIndex(1);
        getMockAndroidOsGroup().setParentGroups(getObjectUnderTest());

        getMockDefaultGroup().setIndex(Integer.MAX_VALUE);
        getMockDefaultGroup().setParentGroups(getObjectUnderTest());
    }

    @Test
    public void testMatchingGroupsWhenNoMatches() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice())).andReturn(Boolean.FALSE);

        replay();

        initObjectUnderTestWithMocks();

        final List<Group> groups = getObjectUnderTest().matchingGroups(getMockDevice());
        Assert.assertEquals("groups are wrong", new ArrayList<Group>(), groups);
    }

    @Test
    public void testMatchingGroupIteratorWhenOnlyDefaultGroupMatches() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice())).andReturn(Boolean.TRUE);

        replay();

        initObjectUnderTestWithMocks();

        final Iterator<Group> itGroups =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());
        Assert.assertTrue("hasNext() should initally be true", itGroups.hasNext());
        Assert.assertEquals("next() is wrong", getMockDefaultGroup(), itGroups.next());
        Assert.assertFalse("hasNext() should now be false", itGroups.hasNext());
    }

    @Test
    public void testMatchingGroupsWhenOnlyDefaultGroupMatches() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice())).andReturn(Boolean.TRUE);

        replay();

        initObjectUnderTestWithMocks();

        final List<Group> groups = getObjectUnderTest().matchingGroups(getMockDevice());
        Assert.assertEquals("groups are wrong", Arrays.asList(getMockDefaultGroup()), groups);
    }

    @Test
    public void testMatchingGroupsDefaultGroupNullAndNoOtherGroups() throws Throwable {
        final List<Group> groups = getObjectUnderTest().matchingGroups(getMockDevice());
        Assert.assertNotNull("groups  should not be null", groups);
        Assert.assertTrue("groups should be empty", groups.isEmpty());
    }

    @Test
    public void testMatchingGroupIteratorWhenMultipleMatches() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice()))
        .andReturn(Boolean.TRUE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice()))
        .andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice()))
        .andReturn(Boolean.TRUE);

        replay();

        initObjectUnderTestWithMocks();

        final Iterator<Group> itGroups =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());
        Assert.assertTrue("hasNext() should initially be true", itGroups
                .hasNext());
        Assert.assertEquals("first next() is wrong", getMockAppleIphoneGroup(),
                itGroups.next());
        Assert.assertTrue("hasNext() should still be true after first next()",
                itGroups.hasNext());
        Assert.assertEquals("second next() is wrong", getMockDefaultGroup(),
                itGroups.next());
        Assert.assertFalse("hasNext() should now be false", itGroups.hasNext());
    }

    @Test
    public void testMatchingGroupsWhenMultipleMatches() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice())).andReturn(Boolean.TRUE);
        EasyMock.expect(getMockAndroidOsGroup().match(getMockDevice())).andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice())).andReturn(Boolean.TRUE);

        replay();

        initObjectUnderTestWithMocks();

        final List<Group> groups = getObjectUnderTest().matchingGroups(getMockDevice());
        Assert.assertEquals("groups are wrong", Arrays.asList(getMockAppleIphoneGroup(),
                getMockDefaultGroup()), groups);
    }

    @Test
    public void testGetNumGroups() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        replay();

        initObjectUnderTestWithMocks();

        final int expectedNumGroups = 3;
        Assert.assertEquals("getNumGroups() is wrong", expectedNumGroups, getObjectUnderTest()
                .getNumGroups());
    }

    @Test
    public void testGetGroupWhenFoundAndNotDefault() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        replay();

        initObjectUnderTestWithMocks();

        Assert.assertEquals("Group should have been found", getMockAppleIphoneGroup(),
                getObjectUnderTest().getGroupByName(IPHONE_GROUP_NAME));
    }

    @Test
    public void testGetGroupWhenFoundAndDefault() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();

        EasyMock.expect(getMockDefaultGroup().getName()).andReturn(DEFAULT_GROUP_NAME);

        replay();

        initObjectUnderTestWithMocks();

        Assert.assertEquals("Group should have been found", getMockDefaultGroup(),
                getObjectUnderTest().getGroupByName(DEFAULT_GROUP_NAME));
    }

    @Test
    public void testGetGroupWhenNotFound() throws Throwable {
        recordBehaviourWhenSetGroupsAndSetDefaultGroupInvoked();
        EasyMock.expect(getMockDefaultGroup().getName()).andReturn(DEFAULT_GROUP_NAME);

        replay();

        initObjectUnderTestWithMocks();

        Assert.assertNull("Group should have been found", getObjectUnderTest().getGroupByName(
                "I cannot be found"));
    }

    /**
     * @return the objectUnderTest
     */
    private Groups getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final Groups objectUnderTest) {
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
     * @return the mockAppleIphoneGroup
     */
    public Group getMockAppleIphoneGroup() {
        return mockAppleIphoneGroup;
    }

    /**
     * @param mockAppleIphoneGroup the mockAppleIphoneGroup to set
     */
    public void setMockAppleIphoneGroup(final Group mockAppleIphoneGroup) {
        this.mockAppleIphoneGroup = mockAppleIphoneGroup;
    }

    /**
     * @return the mockDefaultGroup
     */
    public DefaultGroup getMockDefaultGroup() {
        return mockDefaultGroup;
    }

    /**
     * @param mockDefaultGroup the mockDefaultGroup to set
     */
    public void setMockDefaultGroup(final DefaultGroup mockDefaultGroup) {
        this.mockDefaultGroup = mockDefaultGroup;
    }

    /**
     * @return the mockAndroidOsGroup
     */
    public Group getMockAndroidOsGroup() {
        return mockAndroidOsGroup;
    }

    /**
     * @param mockAndroidOsGroup the mockAndroidOsGroup to set
     */
    public void setMockAndroidOsGroup(final Group mockAndroidOsGroup) {
        this.mockAndroidOsGroup = mockAndroidOsGroup;
    }
}

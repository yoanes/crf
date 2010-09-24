package au.com.sensis.mobile.crf.config;

import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link Groups}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsTestCase extends AbstractJUnit4TestCase {

    private Groups objectUnderTest;

    private Group mockAppleIphoneGroup;
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
        groups.setGroups(new Group[] {getMockAppleIphoneGroup()});
        groups.setDefaultGroup(getMockDefaultGroup());
        setObjectUnderTest(groups);
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

        replay();

        final Iterator<Group> itGroups = getObjectUnderTest().groupIterator();
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
    public void testMatchingGroupIteratorWhenNoMatches() throws Throwable {
        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice()))
                .andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice()))
                .andReturn(Boolean.FALSE);

        replay();

        final Iterator<Group> itGroups =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());
        Assert.assertFalse("hasNext() should be false", itGroups.hasNext());
    }

    @Test
    public void testMatchingGroupIteratorWhenOnlyDefaultGroupMatches() throws Throwable {
        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice()))
            .andReturn(Boolean.FALSE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice()))
            .andReturn(Boolean.TRUE);

        replay();

        final Iterator<Group> itGroups =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());
        Assert.assertTrue("hasNext() should initally be true", itGroups.hasNext());
        Assert.assertEquals("next() is wrong", getMockDefaultGroup(), itGroups.next());
        Assert.assertFalse("hasNext() should now be false", itGroups.hasNext());
    }

    @Test
    public void testMatchingGroupIteratorWhenMultipleMatches() throws Throwable {
        EasyMock.expect(getMockAppleIphoneGroup().match(getMockDevice()))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(getMockDefaultGroup().match(getMockDevice()))
                .andReturn(Boolean.TRUE);

        replay();

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


}

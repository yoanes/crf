package au.com.sensis.mobile.crf.config;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JexlGroupFunctions}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JexlGroupFunctionsTestCase extends AbstractJUnit4TestCase {

    private JexlGroupFunctions objectUnderTest;
    private Device mockDevice;
    private Group mockGroup;


    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new JexlGroupFunctions(getMockDevice(), getMockGroup()));
    }

    @Test
    public void testInAllGroups() throws Throwable {
        final String groupName1 = "group1";
        final String groupName2 = "group2";

        EasyMock.expect(getMockGroup().inAllGroups(getMockDevice(), groupName1, groupName2))
        .andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("inAllGroups is wrong", getObjectUnderTest().inAllGroups(groupName1,
                groupName2));

    }

    @Test
    public void testInAnyGroups() throws Throwable {
        final String groupName1 = "group1";
        final String groupName2 = "group2";

        EasyMock.expect(getMockGroup().inAnyGroup(getMockDevice(), groupName1, groupName2))
        .andReturn(Boolean.TRUE);

        replay();

        Assert.assertTrue("inAnyGroup is wrong", getObjectUnderTest().inAnyGroup(groupName1,
                groupName2));

    }

    /**
     * @return the objectUnderTest
     */
    private JexlGroupFunctions getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JexlGroupFunctions objectUnderTest) {
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
     * @return the mockGroup
     */
    public Group getMockGroup() {
        return mockGroup;
    }

    /**
     * @param mockGroup the mockGroup to set
     */
    public void setMockGroup(final Group mockGroup) {
        this.mockGroup = mockGroup;
    }
}

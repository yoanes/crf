package au.com.sensis.mobile.crf.config;

import java.util.Iterator;

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

    private UiConfiguration objectUnderTest;
    private Groups mockGroups;
    private Device mockDevice;
    private Iterator<Group> mockGroupIterator;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new UiConfiguration());
        getObjectUnderTest().setConfigPath("component/map");
        getObjectUnderTest().setGroups(getMockGroups());
    }

    @Test
    public void testAppliesToPathWhenTrue() throws Throwable {
        Assert.assertTrue("appliesToPath should be true", getObjectUnderTest().appliesToPath(
                "component/map/map.css"));
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
    public void testMatchingGroupIterator() throws Throwable {

        EasyMock.expect(getMockGroups().matchingGroupIterator(getMockDevice()))
                .andReturn(getMockGroupIterator());

        replay();

        final Iterator<Group> actualIterator =
                getObjectUnderTest().matchingGroupIterator(getMockDevice());

        Assert.assertEquals("iterator is wrong",
                getMockGroupIterator(), actualIterator);
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
}

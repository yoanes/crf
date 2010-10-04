package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagData;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link DevicePropertyTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DevicePropertyTagTestCase extends AbstractJUnit4TestCase {

    private static final String DEFAULT_PROPERTY = "myIdPrefix";
    private static final String DEFAULT_VAR = "myIdVarName";
    private static final String PROPERTY_VALUE = "my property value";

    private DevicePropertyTag objectUnderTest;

    private JspContext mockJspContext;
    private TagData mockTagData;
    private Device mockDevice;
    private JspWriter mockJspWriter;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new DevicePropertyTag());
        getObjectUnderTest().setJspContext(getMockJspContext());
        getObjectUnderTest().setProperty(DEFAULT_PROPERTY);
        getObjectUnderTest().setDevice(getMockDevice());
    }

    @Test
    public void testDoTagWhenVarNotSet() throws Throwable {

        EasyMock.expect(getMockDevice().getPropertyAsString(DEFAULT_PROPERTY)).andReturn(
                PROPERTY_VALUE);

        EasyMock.expect(getMockJspContext().getOut()).andReturn(getMockJspWriter());

        getMockJspWriter().write(PROPERTY_VALUE);

        replay();

        getObjectUnderTest().doTag();
    }

    @Test
    public void testDoTagWhenVarSet() throws Throwable {
        getObjectUnderTest().setVar(DEFAULT_VAR);

        EasyMock.expect(getMockDevice().getPropertyAsString(DEFAULT_PROPERTY)).andReturn(
                PROPERTY_VALUE);

        getMockJspContext().setAttribute(DEFAULT_VAR, PROPERTY_VALUE);

        replay();

        getObjectUnderTest().doTag();
    }

    @Test
    public void testDoTagWhenPropertyIsBlank() throws Throwable {
        final String[] testValues = new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            getObjectUnderTest().setProperty(testValue);
            try {
                getObjectUnderTest().doTag();

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert
                        .assertEquals("IllegalArgumentException has wrong message",
                                "property attribute must not be blank: '" + testValue + "'", e
                                        .getMessage());
            }
        }
    }

    @Test
    public void testDoTagWhenDeviceIsNull() throws Throwable {
        getObjectUnderTest().setDevice(null);
        try {
            getObjectUnderTest().doTag();

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "device attribute must not be null", e.getMessage());
        }
    }

    @Test
    public void testDoTagWhenVarIsSetToNonNullBlank() throws Throwable {
        final String[] testValues = new String[] { StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            getObjectUnderTest().setVar(testValue);
            try {
                getObjectUnderTest().doTag();

                Assert.fail("IllegalArgumentException expected");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals("IllegalArgumentException has wrong message",
                        "var attribute must be either null or non-blank: '" + testValue + "'", e
                                .getMessage());
            }
        }

    }

    /**
     * @return the objectUnderTest
     */
    public DevicePropertyTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest
     *            the objectUnderTest to set
     */
    public void setObjectUnderTest(final DevicePropertyTag objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockJspContext
     */
    public JspContext getMockJspContext() {
        return mockJspContext;
    }

    /**
     * @param mockJspContext
     *            the mockJspContext to set
     */
    public void setMockJspContext(final JspContext mockJspContext) {
        this.mockJspContext = mockJspContext;
    }

    /**
     * @return the mockTagData
     */
    public TagData getMockTagData() {
        return mockTagData;
    }

    /**
     * @param mockTagData the mockTagData to set
     */
    public void setMockTagData(final TagData mockTagData) {
        this.mockTagData = mockTagData;
    }

    public Device getMockDevice() {
        return mockDevice;
    }

    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }

    public JspWriter getMockJspWriter() {
        return mockJspWriter;
    }

    public void setMockJspWriter(final JspWriter mockJspWriter) {
        this.mockJspWriter = mockJspWriter;
    }
}

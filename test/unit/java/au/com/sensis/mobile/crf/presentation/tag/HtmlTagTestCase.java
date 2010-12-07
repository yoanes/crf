package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link HtmlTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class HtmlTagTestCase extends AbstractJUnit4TestCase {

    private static final String CONTENT_TYPE = "text/html";
    private static final String CHARSET = "UTF-8";
    private static final String DOCTYPE = "<!DOCTYPE html>";
    private static final String HTML_ROOT = "<html>";
    private static final String HTML_CLOSE_TAG = "</html>";

    private HtmlTag objectUnderTest;

    private Device mockDevice;
    private MockHttpServletResponse httpServletResponse;
    private JspFragment mockJspBody;
    private MockPageContext pageContext;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setHttpServletResponse(new MockHttpServletResponse());
        setPageContext(new MockPageContext(null, null, getHttpServletResponse()));

        setObjectUnderTest(new HtmlTag());
        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getPageContext());
        getObjectUnderTest().setJspBody(getMockJspBody());

    }

    @Test
    public void testDoTag() throws Throwable {

        EasyMock.expect(getMockDevice().getPropertyAsString("custom.crf.contentType")).andReturn(
                CONTENT_TYPE);
        EasyMock.expect(getMockDevice().getPropertyAsString("custom.crf.charset")).andReturn(
                CHARSET);
        EasyMock.expect(getMockDevice().getPropertyAsString("custom.crf.docType")).andReturn(
                DOCTYPE);
        EasyMock.expect(getMockDevice().getPropertyAsString("custom.crf.htmlRoot")).andReturn(
                HTML_ROOT);

        getMockJspBody().invoke(getPageContext().getOut());

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("contentType is wrong", CONTENT_TYPE, getHttpServletResponse()
                .getContentType());
        Assert.assertEquals("charEncoding is wrong", CHARSET, getHttpServletResponse()
                .getCharacterEncoding());

        Assert.assertEquals("response content is wrong", DOCTYPE + IOUtils.LINE_SEPARATOR
                + HTML_ROOT + IOUtils.LINE_SEPARATOR + HTML_CLOSE_TAG, getHttpServletResponse()
                .getContentAsString());

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
     * @return the httpServletResponse
     */
    private MockHttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    /**
     * @param httpServletResponse the httpServletResponse to set
     */
    private void setHttpServletResponse(final MockHttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * @return the objectUnderTest
     */
    private HtmlTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final HtmlTag objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockJspBody
     */
    public JspFragment getMockJspBody() {
        return mockJspBody;
    }

    /**
     * @param mockJspBody the mockJspBody to set
     */
    public void setMockJspBody(final JspFragment mockJspBody) {
        this.mockJspBody = mockJspBody;
    }

    /**
     * @return the pageContext
     */
    private MockPageContext getPageContext() {
        return pageContext;
    }

    /**
     * @param pageContext the pageContext to set
     */
    private void setPageContext(final MockPageContext pageContext) {
        this.pageContext = pageContext;
    }
}

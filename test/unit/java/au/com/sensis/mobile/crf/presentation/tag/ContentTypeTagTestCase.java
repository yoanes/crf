package au.com.sensis.mobile.crf.presentation.tag;

import java.util.Properties;

import javax.servlet.jsp.PageContext;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.service.PropertiesLoader;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ContentTypeTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ContentTypeTagTestCase extends AbstractJUnit4TestCase {

    private ContentTypeTag objectUnderTest;

    private PageContext mockPageContext;

    private MockServletContext servletContext;
    private WebApplicationContext mockWebApplicationContext;
    private Device mockDevice;
    private PropertiesLoader mockPropertiesLoader;
    private MockHttpServletResponse httpServletResponse;


    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setHttpServletResponse(new MockHttpServletResponse());
        setServletContext(new MockServletContext());

        setObjectUnderTest(new ContentTypeTag());
        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setResponse(getHttpServletResponse());
        getObjectUnderTest().setJspContext(getMockPageContext());

    }

    @Test
    public void testDoTagWhenNonBlankPropertiesFound() throws Throwable {
        recordGetPropertiesLoader();

        final Properties properties = new Properties();
        properties.setProperty("contentType", "application/xhtml+xml");
        properties.setProperty("charEncoding", "UTF-8");

        EasyMock.expect(
                getMockPropertiesLoader().loadProperties(getMockDevice(),
                        "responseHeader.properties")).andReturn(
                properties);

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("contentType is wrong", getHttpServletResponse()
                .getContentType(), "application/xhtml+xml");
        Assert.assertEquals("charEncoding is wrong", getHttpServletResponse()
                .getCharacterEncoding(), "UTF-8");

    }

    @Test
    public void testDoTagWhenBlankPropertiesFound() throws Throwable {
        recordGetPropertiesLoader();

        final Properties properties = new Properties();

        EasyMock.expect(
                getMockPropertiesLoader().loadProperties(getMockDevice(),
                "responseHeader.properties")).andReturn(
                        properties);

        replay();

        getHttpServletResponse().setContentType("existingContentType");
        getHttpServletResponse().setCharacterEncoding("existingCharacterEncoding");

        getObjectUnderTest().doTag();

        Assert.assertEquals("contentType is wrong", getHttpServletResponse()
                .getContentType(), "existingContentType");
        Assert.assertEquals("charEncoding is wrong", getHttpServletResponse()
                .getCharacterEncoding(), "existingCharacterEncoding");

    }

    private void recordGetPropertiesLoader() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(getServletContext())
                .atLeastOnce();

        getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(getMockWebApplicationContext().getBean("crf.propertiesLoader")).andReturn(
                getMockPropertiesLoader()).atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    private ContentTypeTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ContentTypeTag objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockPageContext
     */
    public PageContext getMockPageContext() {
        return mockPageContext;
    }

    /**
     * @param mockPageContext the mockPageContext to set
     */
    public void setMockPageContext(final PageContext mockPageContext) {
        this.mockPageContext = mockPageContext;
    }

    /**
     * @return the mockWebApplicationContext
     */
    public WebApplicationContext getMockWebApplicationContext() {
        return mockWebApplicationContext;
    }

    /**
     * @param mockWebApplicationContext
     *            the mockWebApplicationContext to set
     */
    public void setMockWebApplicationContext(
            final WebApplicationContext mockWebApplicationContext) {

        this.mockWebApplicationContext = mockWebApplicationContext;
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
     * @return the servletContext
     */
    private MockServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @param servletContext the servletContext to set
     */
    private void setServletContext(final MockServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @return the mockPropertiesLoader
     */
    public PropertiesLoader getMockPropertiesLoader() {
        return mockPropertiesLoader;
    }

    /**
     * @param mockPropertiesLoader the mockPropertiesLoader to set
     */
    public void setMockPropertiesLoader(final PropertiesLoader mockPropertiesLoader) {
        this.mockPropertiesLoader = mockPropertiesLoader;
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
}

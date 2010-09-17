package au.com.sensis.mobile.crf.presentation;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import au.com.sensis.mobile.crf.presentation.HttpServletRequestInterrogator;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link HttpServletRequestInterrogator}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class HttpServletRequestInterrogatorTestCase extends
        AbstractJUnit4TestCase {

    private static final String INCLUDE_URI = "/mywebapp/WEB-INF/view/common/logo.jsp";
    private static final String FORWARD_URI = "/WEB-INF/view/home/home.jsp";
    private HttpServletRequestInterrogator objectUnderTest;
    private MockHttpServletRequest springMockHttpServletRequest;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setSpringMockHttpServletRequest(new MockHttpServletRequest());
        setObjectUnderTest(new HttpServletRequestInterrogator(
                getSpringMockHttpServletRequest()));
    }

    @Test
    public void testGetRequestTypeWhenForward() throws Throwable {
        Assert.assertTrue("isForward is wrong", getObjectUnderTest().isForward());
        Assert.assertFalse("isInclude is wrong", getObjectUnderTest().isInclude());
    }

    @Test
    public void testGetRequestUriWhenForward() throws Throwable {
        getSpringMockHttpServletRequest().setServletPath(FORWARD_URI);

        Assert.assertEquals("requestUri Is wrong", FORWARD_URI,
                getObjectUnderTest().getRequestUri());
    }

    @Test
    public void testGetRequestTypeWhenInclude() throws Throwable {
        getSpringMockHttpServletRequest().setAttribute(
                "javax.servlet.include.request_uri", INCLUDE_URI);
        Assert.assertFalse("isForward is wrong", getObjectUnderTest()
                .isForward());
        Assert.assertTrue("isInclude is wrong", getObjectUnderTest()
                .isInclude());

    }

    @Test
    public void testGetRequestUriWhenInclude() throws Throwable {
        getSpringMockHttpServletRequest().setContextPath("/mywebapp");
        getSpringMockHttpServletRequest().setServletPath(FORWARD_URI);

        final String[] includeUris =
                { "/mywebapp/WEB-INF/view/common/logo.jsp",
                        "../common/header.jsp", "advert.jsp", "/index.jsp" };
        final String[] expectedRequestUris =
                { "/WEB-INF/view/common/logo.jsp",
                        "/WEB-INF/view/home/../common/header.jsp",
                        "/WEB-INF/view/home/advert.jsp", "/index.jsp" };
        for (int i = 0; i < includeUris.length; i++) {
            getSpringMockHttpServletRequest().setAttribute(
                    "javax.servlet.include.request_uri", includeUris[i]);
            Assert.assertEquals("requestUri Is wrong for test item " + i,
                    expectedRequestUris[i], getObjectUnderTest()
                            .getRequestUri());
        }
    }

    @Test
    public void testToString() throws Throwable {
        getSpringMockHttpServletRequest().setServletPath(FORWARD_URI);

        ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);

        Assert.assertEquals("toString is wrong",
                "HttpServletRequestInterrogator[requestUri=" + FORWARD_URI
                        + ",isForward=true,isInclude=false]",
                getObjectUnderTest().toString());
    }

    /**
     * @return the objectUnderTest
     */
    private HttpServletRequestInterrogator getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final HttpServletRequestInterrogator objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the springMockHttpServletRequest
     */
    private MockHttpServletRequest getSpringMockHttpServletRequest() {
        return springMockHttpServletRequest;
    }

    /**
     * @param springMockHttpServletRequest the springMockHttpServletRequest to set
     */
    private void setSpringMockHttpServletRequest(
            final MockHttpServletRequest springMockHttpServletRequest) {
        this.springMockHttpServletRequest = springMockHttpServletRequest;
    }
}

package au.com.sensis.mobile.crf.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceResolverDebugFilter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverDebugFilterTestCase extends AbstractJUnit4TestCase {

    private static final String ORIGINAL_RESPONSE = "<?xml version='1.0'?>"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>"
        + "<script src=\"blah.js\" type=\"text/javascript\"></script></head>"
        + "<body><p>blah</p></body></html>";

    private static final String RESOLVED_RESOURCE_PATH
        = "/WEB-INF/view/jsp/iphone-ipod/detail/bdp.jsp";

    private static final String EXPECTED_MODIFIED_RESPONSE = "<?xml version='1.0'?>"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><!--\n1. "
        + RESOLVED_RESOURCE_PATH
        + "\n--><head>"
        + "<script src=\"blah.js\" type=\"text/javascript\"></script></head>"
        + "<body><p>blah</p></body></html>";

    private ResourceResolverDebugFilter objectUnderTest;
    private HttpServletRequest mockHttpServletRequest;
    private HttpServletResponse mockHttpServletResponse;
    private FilterChain mockFilterChain;
    private StubbedFilterChain stubbedFilterChain;

    private Resource mockResource;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ResourceResolverDebugFilter());

        getObjectUnderTest().setEnabled(true);

        setStubbedFilterChain(new StubbedFilterChain());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceResolutionTreeHolder.removeResourceResolutionTree();
    }

    @Test
    public void testDoFilterWhenEnabled() throws Throwable {

        final StringWriter stringWriter = new StringWriter();
        EasyMock.expect(getMockHttpServletResponse().getWriter()).andReturn(
                new PrintWriter(stringWriter));

        EasyMock.expect(getMockResource().getNewPath()).andReturn(RESOLVED_RESOURCE_PATH);

        replay();

        getObjectUnderTest().doFilter(getMockHttpServletRequest(), getMockHttpServletResponse(),
                getStubbedFilterChain());

        Assert.assertNotNull(
                "ResourceResolutionTree should not have been null during filter chain invocation",
                getStubbedFilterChain().getResourceResolutionTree());

        Assert.assertTrue(
                "ResourceResolutionTree should have been enabled during filter chain invocation",
                getStubbedFilterChain().getResourceResolutionTree().isEnabled());

        Assert.assertEquals("Response is incorrect", EXPECTED_MODIFIED_RESPONSE, stringWriter
                .toString());
    }

    @Test
    public void testDoFilterWhenDisabled() throws Throwable {

        getObjectUnderTest().setEnabled(false);

        final StringWriter stringWriter = new StringWriter();
        EasyMock.expect(getMockHttpServletResponse().getWriter()).andReturn(
                new PrintWriter(stringWriter));

        replay();

        getObjectUnderTest().doFilter(getMockHttpServletRequest(), getMockHttpServletResponse(),
                getStubbedFilterChain());

        Assert.assertNotNull(
                "ResourceResolutionTree should not have been null during filter chain invocation",
                getStubbedFilterChain().getResourceResolutionTree());

        Assert.assertFalse(
                "ResourceResolutionTree should have been disabled during filter chain invocation",
                getStubbedFilterChain().getResourceResolutionTree().isEnabled());

        Assert.assertEquals("Response is incorrect", ORIGINAL_RESPONSE, stringWriter
                .toString());
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceResolverDebugFilter getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ResourceResolverDebugFilter objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockHttpServletRequest
     */
    public HttpServletRequest getMockHttpServletRequest() {
        return mockHttpServletRequest;
    }

    /**
     * @param mockHttpServletRequest the mockHttpServletRequest to set
     */
    public void setMockHttpServletRequest(final HttpServletRequest mockHttpServletRequest) {
        this.mockHttpServletRequest = mockHttpServletRequest;
    }

    /**
     * @return the mockHttpServletResponse
     */
    public HttpServletResponse getMockHttpServletResponse() {
        return mockHttpServletResponse;
    }

    /**
     * @param mockHttpServletResponse the mockHttpServletResponse to set
     */
    public void setMockHttpServletResponse(final HttpServletResponse mockHttpServletResponse) {
        this.mockHttpServletResponse = mockHttpServletResponse;
    }

    /**
     * @return the mockFilterChain
     */
    public FilterChain getMockFilterChain() {
        return mockFilterChain;
    }

    /**
     * @param mockFilterChain the mockFilterChain to set
     */
    public void setMockFilterChain(final FilterChain mockFilterChain) {
        this.mockFilterChain = mockFilterChain;
    }

    /**
     * @return the mockResource
     */
    public Resource getMockResource() {
        return mockResource;
    }

    /**
     * @param mockResource the mockResource to set
     */
    public void setMockResource(final Resource mockResource) {
        this.mockResource = mockResource;
    }

    private class StubbedFilterChain implements FilterChain {

        private ResourceResolutionTree resourceResolutionTree;

        @Override
        public void doFilter(final ServletRequest servletRequest,
                final ServletResponse servletResponse) throws IOException, ServletException {

            final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            httpServletResponse.getWriter().print(ORIGINAL_RESPONSE);

            ResourceResolutionTreeHolder.getResourceResolutionTree()
                    .addChildToCurrentNodeAndPromoteToCurrent(
                            new ResourceTreeNodeBean(getMockResource()));

            setResourceResolutionTree(ResourceResolutionTreeHolder.getResourceResolutionTree());
        }

        /**
         * @return the resourceResolutionTree
         */
        private ResourceResolutionTree getResourceResolutionTree() {
            return resourceResolutionTree;
        }

        /**
         * @param resourceResolutionTree
         *            the resourceResolutionTree to set
         */
        private void setResourceResolutionTree(
                final ResourceResolutionTree resourceResolutionTree) {
            this.resourceResolutionTree = resourceResolutionTree;
        }

    }

    /**
     * @return the stubbedFilterChain
     */
    private StubbedFilterChain getStubbedFilterChain() {
        return stubbedFilterChain;
    }

    /**
     * @param stubbedFilterChain the stubbedFilterChain to set
     */
    private void setStubbedFilterChain(final StubbedFilterChain stubbedFilterChain) {
        this.stubbedFilterChain = stubbedFilterChain;
    }
}

package au.com.sensis.mobile.crf.presentation;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.presentation.tag.BundleTagCache;
import au.com.sensis.mobile.crf.service.ResourceCache;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link AdminServlet}.
 *
 * @author w12495
 */
public class AdminServletTestCase extends AbstractJUnit4TestCase {

    private static final String RESOURCE_CACHE_BEAN_NAME = "crf.resourceCache";
    private static final String BUNDLE_LINKS_TAG_CACHE_BEAN_NAME = "crf.bundleLinksTagCache";
    private static final String BUNDLE_SCRIPTS_TAG_CACHE_BEAN_NAME = "crf.bundleScriptsTagCache";
    private static final String EMPTY_RESOURCE_CACHE_ACTION = "/resources/empty";

    private AdminServlet objectUnderTest;

    private WebApplicationContext mockWebApplicationContext;
    private MockServletContext springMockServletContext;
    private MockServletConfig springMockServletConfig;

    private ResourceCache mockResourceCache;
    private BundleTagCache mockBundleScriptsTagCache;
    private BundleTagCache mockBundleLinksTagCache;

    private MockHttpServletRequest springHttpServletRequest;
    private MockHttpServletResponse springHttpServletResponse;


    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new AdminServlet());

        getObjectUnderTest().setResourceCacheBeanName(RESOURCE_CACHE_BEAN_NAME);
        getObjectUnderTest().setBundleLinksTagCacheBeanName(BUNDLE_LINKS_TAG_CACHE_BEAN_NAME);
        getObjectUnderTest().setBundleScriptsTagCacheBeanName(BUNDLE_SCRIPTS_TAG_CACHE_BEAN_NAME);
        getObjectUnderTest().setEmptyResourceCacheAction(EMPTY_RESOURCE_CACHE_ACTION);

        setSpringMockServletContext(new MockServletContext());
        setSpringMockServletConfig(new MockServletConfig(getSpringMockServletContext()));
        setSpringHttpServletRequest(new MockHttpServletRequest());
        setSpringHttpServletResponse(new MockHttpServletResponse());
    }

    @Test
    public void testEmptyResources() throws Exception {

        recordGetBeans();

        recordEmptyCaches();

        replay();

        getObjectUnderTest().init(getSpringMockServletConfig());

        getSpringHttpServletRequest().setPathInfo(EMPTY_RESOURCE_CACHE_ACTION);
        getObjectUnderTest().doGet(getSpringHttpServletRequest(), getSpringHttpServletResponse());

        Assert.assertEquals("Response has wrong status", HttpServletResponse.SC_OK,
                getSpringHttpServletResponse().getStatus());
        Assert.assertEquals("Response has wrong character encoding", "UTF-8",
                getSpringHttpServletResponse().getCharacterEncoding());
        Assert.assertEquals("Response has wrong content",
                "Successfuly emptied the Resource caches.", getSpringHttpServletResponse()
                        .getContentAsString().trim());
    }

    @Test
    public void testInvalidAdminTaskResources() throws Exception {

        recordGetBeans();

        replay();

        getObjectUnderTest().init(getSpringMockServletConfig());

        getSpringHttpServletRequest().setPathInfo("unknown");
        getObjectUnderTest().doGet(getSpringHttpServletRequest(), getSpringHttpServletResponse());

        Assert.assertEquals("Response has wrong status", HttpServletResponse.SC_BAD_REQUEST,
                getSpringHttpServletResponse().getStatus());
    }

    private void recordEmptyCaches() {
        getMockResourceCache().removeAll();
        getMockBundleScriptsTagCache().removeAll();
        getMockBundleLinksTagCache().removeAll();
    }

    private void recordGetBeans() {

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(getMockWebApplicationContext().getBean(RESOURCE_CACHE_BEAN_NAME))
                .andReturn(getMockResourceCache()).atLeastOnce();
        EasyMock.expect(getMockWebApplicationContext().getBean(BUNDLE_LINKS_TAG_CACHE_BEAN_NAME))
                .andReturn(getMockBundleLinksTagCache()).atLeastOnce();
        EasyMock.expect(getMockWebApplicationContext().getBean(BUNDLE_SCRIPTS_TAG_CACHE_BEAN_NAME))
                .andReturn(getMockBundleScriptsTagCache()).atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    private AdminServlet getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final AdminServlet objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockWebApplicationContext
     */
    public WebApplicationContext getMockWebApplicationContext() {
        return mockWebApplicationContext;
    }

    /**
     * @param mockWebApplicationContext the mockWebApplicationContext to set
     */
    public void setMockWebApplicationContext(final WebApplicationContext mockWebApplicationContext) {
        this.mockWebApplicationContext = mockWebApplicationContext;
    }

    /**
     * @return the springMockServletContext
     */
    private MockServletContext getSpringMockServletContext() {
        return springMockServletContext;
    }

    /**
     * @param springMockServletContext the springMockServletContext to set
     */
    private void setSpringMockServletContext(final MockServletContext springMockServletContext) {
        this.springMockServletContext = springMockServletContext;
    }

    /**
     * @return the springMockServletConfig
     */
    private MockServletConfig getSpringMockServletConfig() {
        return springMockServletConfig;
    }

    /**
     * @param springMockServletConfig the springMockServletConfig to set
     */
    private void setSpringMockServletConfig(final MockServletConfig springMockServletConfig) {
        this.springMockServletConfig = springMockServletConfig;
    }

    /**
     * @return the mockResourceCache
     */
    public ResourceCache getMockResourceCache() {
        return mockResourceCache;
    }

    /**
     * @param mockResourceCache the mockResourceCache to set
     */
    public void setMockResourceCache(final ResourceCache mockResourceCache) {
        this.mockResourceCache = mockResourceCache;
    }

    /**
     * @return the mockBundleScriptsTagCache
     */
    public BundleTagCache getMockBundleScriptsTagCache() {
        return mockBundleScriptsTagCache;
    }

    /**
     * @param mockBundleScriptsTagCache the mockBundleScriptsTagCache to set
     */
    public void setMockBundleScriptsTagCache(final BundleTagCache mockBundleScriptsTagCache) {
        this.mockBundleScriptsTagCache = mockBundleScriptsTagCache;
    }

    /**
     * @return the mockBundleLinksTagCache
     */
    public BundleTagCache getMockBundleLinksTagCache() {
        return mockBundleLinksTagCache;
    }

    /**
     * @param mockBundleLinksTagCache the mockBundleLinksTagCache to set
     */
    public void setMockBundleLinksTagCache(final BundleTagCache mockBundleLinksTagCache) {
        this.mockBundleLinksTagCache = mockBundleLinksTagCache;
    }

    /**
     * @return the springHttpServletRequest
     */
    private MockHttpServletRequest getSpringHttpServletRequest() {
        return springHttpServletRequest;
    }

    /**
     * @param springHttpServletRequest the springHttpServletRequest to set
     */
    private void setSpringHttpServletRequest(final MockHttpServletRequest springHttpServletRequest) {
        this.springHttpServletRequest = springHttpServletRequest;
    }

    /**
     * @return the springHttpServletResponse
     */
    private MockHttpServletResponse getSpringHttpServletResponse() {
        return springHttpServletResponse;
    }

    /**
     * @param springHttpServletResponse the springHttpServletResponse to set
     */
    private void setSpringHttpServletResponse(final MockHttpServletResponse springHttpServletResponse) {
        this.springHttpServletResponse = springHttpServletResponse;
    }

}

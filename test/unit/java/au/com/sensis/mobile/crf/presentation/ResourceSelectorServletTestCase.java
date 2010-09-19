package au.com.sensis.mobile.crf.presentation;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.service.FileIoFacade;
import au.com.sensis.mobile.crf.service.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.NullMappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourceSelector;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Unit test {@link ResourceSelectorServlet}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorServletTestCase extends
        AbstractJUnit4TestCase {

    private static final String BEAN_NAME = "resourceSelector";

    private ResourceSelectorServlet objectUnderTest;
    private ResourceSelector
        mockResourceSelector;
    private MockServletContext springMockServletContext;
    private HttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse springMockHttpServletResponse;
    private MockServletConfig springMockServletConfig;
    private MockHttpSession sprngMockHttpSession;
    private MobileContext mockMobileContext;
    private RequestDispatcher mockRequestDispatcher;
    private WebApplicationContext mockWebApplicationContext;
    private Device mockDevice;
    private FileIoFacade mockFileIoFacade;
    private MappedResourcePath mockMappedResourcePath;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new ResourceSelectorServlet());
        getObjectUnderTest().setResourceSelectorBeanName(
                BEAN_NAME);

        setSpringMockServletContext(new MockServletContext());
        setSpringMockHttpServletResponse(new MockHttpServletResponse());
        setSpringMockServletConfig(new MockServletConfig(getSpringMockServletContext()));
        setSprngMockHttpSession(new MockHttpSession());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();
    }

    @Test
    public void testInitWhenResourceSelectorBeanNameIsBlank()
            throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            getObjectUnderTest()
                    .setResourceSelectorBeanName(
                            testValue);
            try {
                getObjectUnderTest().init(getSpringMockServletConfig());

                Assert.fail("IllegalStateException expected for testValue: '"
                        + testValue + "'");
            } catch (final IllegalStateException e) {

                Assert.assertEquals(
                    "IllegalStateException has wrong message",
                    "resourceSelectorBeanName must be set "
                            + "as a non-blank Servlet init-param in your web.xml. Was: '"
                            + testValue + "'",
                    e.getMessage());
            }
        }

    }

    @Test
    public void testDoGetWhenNoMappedResourcePathFound() throws Throwable {
        recordGetResourceSelector();

        recordGetDevice();

        recordIsRequestViaIncludeReturnsFalse();

        final String requestedResourceServletPath =
                "/WEB-INF/view/home/home.jsp";
        recordGetRequestServletPath(requestedResourceServletPath);

        recordGetNullResourcePathFromResourceSelector(
                requestedResourceServletPath);

        replay();

        getObjectUnderTest().init(getSpringMockServletConfig());

        try {
            getObjectUnderTest().doGet(getMockHttpServletRequest(),
                    getSpringMockHttpServletResponse());

            Assert.fail("IOException expected");
        } catch (final IOException e) {

            Assert.assertEquals("IOException has wrong message",
                    "No concrete resource found for requested path: '"
                        + requestedResourceServletPath + "'", e.getMessage());
        }

    }

    private void recordGetNullResourcePathFromResourceSelector(
            final String requestedResourceServletPath) {
        final NullMappedResourcePath nullMappedResourcePath
            = new NullMappedResourcePath(requestedResourceServletPath);
        EasyMock.expect(
                getMockResourceSelector()
                        .getResourcePath(getMockDevice(),
                                requestedResourceServletPath)).andReturn(
                nullMappedResourcePath).atLeastOnce();

    }

    @Test
    public void testDoGetWhenRequestIsViaAForward() throws Throwable {
        setupForRequestViaForward();
        getObjectUnderTest().doGet(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());

    }

    @Test
    public void testDoPostWhenRequestIsViaAForward() throws Throwable {
        setupForRequestViaForward();
        getObjectUnderTest().doPost(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());
    }

    private void setupForRequestViaForward() throws ServletException,
            IOException {
        recordGetResourceSelector();

        recordGetDevice();

        recordIsRequestViaIncludeReturnsFalse();

        final String requestedResourceServletPath =
                "/WEB-INF/view/home/home.jsp";
        recordGetRequestServletPath(requestedResourceServletPath);

        recordGetResourcePathFromResourceSelector(
                requestedResourceServletPath);

        final String actualResourceServletPath =
            "/WEB-INF/view/master/home/home.jsp";
        recordCheckIfMappedResourcePathExists(actualResourceServletPath, Boolean.TRUE);

        recordGetRequestDispatcher(actualResourceServletPath);

        recordRequestDispatcherForward();

        replay();

        getObjectUnderTest().init(getSpringMockServletConfig());
    }

    private void recordCheckIfMappedResourcePathExists(
            final String actualResourceServletPath, final boolean exists) {

        EasyMock.expect(getMockMappedResourcePath().exists()).andReturn(exists);

        if (exists) {
            EasyMock.expect(getMockMappedResourcePath().getNewResourcePath())
                    .andReturn(actualResourceServletPath);
        }
    }

    @Test
    public void testDoGetWhenRequestIsViaInclude() throws Throwable {
        setupForRequestViaInclude();
        getObjectUnderTest().doGet(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());

    }

    @Test
    public void testDoPostWhenRequestIsViaInclude() throws Throwable {
        setupForRequestViaInclude();
        getObjectUnderTest().doPost(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());

    }

    private void setupForRequestViaInclude() throws ServletException,
            IOException {
        recordGetResourceSelector();

        recordGetDevice();

        final String webappContextPath = "/webappContext";
        final String includedResourceServletPathWithoutContext =
            "/WEB-INF/view/common/logo.jsp";
        final String includedResourceServletPath =
                webappContextPath + includedResourceServletPathWithoutContext;
        recordIsRequestViaIncludeReturnsTrue(includedResourceServletPath);

        recordGetRequestContextPath(webappContextPath);

        recordGetResourcePathFromResourceSelector(
                includedResourceServletPathWithoutContext);

        final String actualResourceServletPath =
            "/WEB-INF/view/master/common/logo.jsp";
        recordCheckIfMappedResourcePathExists(actualResourceServletPath, Boolean.TRUE);

        recordGetRequestDispatcher(actualResourceServletPath);

        recordRequestDispatcherInclude();

        replay();

        getObjectUnderTest().init(getSpringMockServletConfig());
    }

    private void recordGetRequestContextPath(final String webappContextPath) {

        EasyMock.expect(getMockHttpServletRequest().getContextPath())
                .andReturn(webappContextPath).atLeastOnce();
    }

    private void recordIsRequestViaIncludeReturnsTrue(
            final String requestedResourceServletPath) {
        EasyMock.expect(
                getMockHttpServletRequest().getAttribute(
                        "javax.servlet.include.request_uri")).andReturn(
                requestedResourceServletPath).atLeastOnce();
    }

    private void recordIsRequestViaIncludeReturnsFalse() {

        EasyMock.expect(
                getMockHttpServletRequest().getAttribute(
                        "javax.servlet.include.request_uri")).andReturn(null)
                .atLeastOnce();
    }

    /**
     * @throws ServletException
     * @throws IOException
     */
    private void recordRequestDispatcherForward() throws ServletException,
            IOException {
        getMockRequestDispatcher().forward(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());
    }

    /**
     * @throws ServletException
     * @throws IOException
     */
    private void recordRequestDispatcherInclude() throws ServletException,
            IOException {
        getMockRequestDispatcher().include(getMockHttpServletRequest(),
                getSpringMockHttpServletResponse());
    }

    /**
     * @param actualResourceServletPath
     */
    private void recordGetRequestDispatcher(
            final String actualResourceServletPath) {
        EasyMock.expect(
                getMockHttpServletRequest().getRequestDispatcher(
                        actualResourceServletPath)).andReturn(
                getMockRequestDispatcher()).atLeastOnce();
    }

    private void recordGetResourcePathFromResourceSelector(
            final String requestedResourceServletPath) {
        EasyMock.expect(
                getMockResourceSelector()
                        .getResourcePath(getMockDevice(),
                                requestedResourceServletPath)).andReturn(
                getMockMappedResourcePath()).atLeastOnce();
    }

    private void recordGetRequestServletPath(
            final String requestedResourceServletPath) {
        EasyMock.expect(getMockHttpServletRequest().getServletPath())
                .andReturn(requestedResourceServletPath).atLeastOnce();
    }

    private void recordGetDevice() {

        EasyMock.expect(getMockHttpServletRequest().getSession()).andReturn(
                getSpringMockHttpSession()).atLeastOnce();
        getSpringMockHttpSession().setAttribute(
                MobileContext.MOBILE_CONTEXT_KEY, getMockMobileContext());

        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(
                getMockDevice()).atLeastOnce();
    }

    private void recordGetResourceSelector() {
        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(BEAN_NAME))
                .andReturn(getMockResourceSelector())
                .atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceSelectorServlet getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final ResourceSelectorServlet objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockResourceSelector
     */
    public ResourceSelector
        getMockResourceSelector() {
        return mockResourceSelector;
    }

    /**
     * @param mockResourceSelector
     *            the mockResourceSelector to set
     */
    public void setMockResourceSelector(
            final ResourceSelector mockResourceSelector) {
        this.mockResourceSelector = mockResourceSelector;
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
    private void setSpringMockServletContext(
            final MockServletContext springMockServletContext) {
        this.springMockServletContext = springMockServletContext;
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
    public void setMockHttpServletRequest(
            final HttpServletRequest mockHttpServletRequest) {
        this.mockHttpServletRequest = mockHttpServletRequest;
    }

    /**
     * @return the springMockHttpServletResponse
     */
    private MockHttpServletResponse getSpringMockHttpServletResponse() {
        return springMockHttpServletResponse;
    }

    /**
     * @param springMockHttpServletResponse the springMockHttpServletResponse to set
     */
    private void setSpringMockHttpServletResponse(
            final MockHttpServletResponse springMockHttpServletResponse) {
        this.springMockHttpServletResponse = springMockHttpServletResponse;
    }

    /**
     * @return the mockMobileContext
     */
    public MobileContext getMockMobileContext() {
        return mockMobileContext;
    }

    /**
     * @param mockMobileContext the mockMobileContext to set
     */
    public void setMockMobileContext(final MobileContext mockMobileContext) {
        this.mockMobileContext = mockMobileContext;
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
     * @return the mockWebApplicationContext
     */
    public WebApplicationContext getMockWebApplicationContext() {
        return mockWebApplicationContext;
    }

    /**
     * @param mockWebApplicationContext the mockWebApplicationContext to set
     */
    public void setMockWebApplicationContext(
            final WebApplicationContext mockWebApplicationContext) {
        this.mockWebApplicationContext = mockWebApplicationContext;
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
    private void setSpringMockServletConfig(
            final MockServletConfig springMockServletConfig) {
        this.springMockServletConfig = springMockServletConfig;
    }

    /**
     * @return the mockRequestDispatcher
     */
    public RequestDispatcher getMockRequestDispatcher() {
        return mockRequestDispatcher;
    }

    /**
     * @param mockRequestDispatcher the mockRequestDispatcher to set
     */
    public void setMockRequestDispatcher(final RequestDispatcher mockRequestDispatcher) {
        this.mockRequestDispatcher = mockRequestDispatcher;
    }

    /**
     * @return the sprngMockHttpSession
     */
    private MockHttpSession getSpringMockHttpSession() {
        return sprngMockHttpSession;
    }

    /**
     * @param sprngMockHttpSession the sprngMockHttpSession to set
     */
    private void setSprngMockHttpSession(final MockHttpSession sprngMockHttpSession) {
        this.sprngMockHttpSession = sprngMockHttpSession;
    }

    /**
     * @return the mockFileIoFacade
     */
    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    /**
     * @param mockFileIoFacade the mockFileIoFacade to set
     */
    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }

    /**
     * @return the mockMappedResourcePath
     */
    public MappedResourcePath getMockMappedResourcePath() {
        return mockMappedResourcePath;
    }

    /**
     * @param mockMappedResourcePath the mockMappedResourcePath to set
     */
    public void setMockMappedResourcePath(final MappedResourcePath mockMappedResourcePath) {
        this.mockMappedResourcePath = mockMappedResourcePath;
    }
}
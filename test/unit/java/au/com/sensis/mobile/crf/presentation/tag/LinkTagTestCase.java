package au.com.sensis.mobile.crf.presentation.tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link LinkTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagTestCase extends AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private LinkTag objectUnderTest;
    private PageContext mockPageContext;
    private JspFragment mockJspFragment;
    private JspWriter mockJspWriter;
    private Map<String, TagWriter> linkTagWriterMap;

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();
    private LinkTagDependencies linkTagDependencies;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private LinkTagWriter mockLinkTagWriter;
    private LinkTagWriterFactory mockLinkTagWriterFactory;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private JspContextBundleTagStack mockBundleTagStack;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        LinkTagWriterFactory
        .changeDefaultLinkTagWriterFactorySingleton(
                getMockLinkTagWriterFactory());

        setTagDepedencies(createTagDependencies());
        setObjectUnderTest(new LinkTag());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setJspBody(getMockJspFragment());
        getObjectUnderTest().setHref(getRequestedCssResourcePath());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createRelDynamicAttribute().getLocalName(),
                createRelDynamicAttribute().getValue());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createTypeDynamicAttribute().getLocalName(),
                createTypeDynamicAttribute().getValue());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createArbitraryDynamicAttribute().getLocalName(),
                createArbitraryDynamicAttribute().getValue());

        setSpringMockServletContext(new MockServletContext());

        setLinkTagWriterMap(new HashMap<String, TagWriter>());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        LinkTagWriterFactory
        .restoreDefaultLinkTagWriterFactorySingleton();
    }

    private LinkTagDependencies createTagDependencies() {
        return new LinkTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getCssClientPathPrefix(),
                getMockResolutionWarnLogger(),
                getMockBundleTagStack());
    }

    private void recordGetTagDependencies() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(LinkTagDependencies.BEAN_NAME))
                .andReturn(getTagDependencies())
                .atLeastOnce();
    }

    @Test
    public void testDoTagWithInvalidHref() throws Throwable {
        final String[] testHrefs =
            { "../some/where", "/somewhere", null, StringUtils.EMPTY, " ",
            "  " };

        for (final String testHref : testHrefs) {
            try {
                getObjectUnderTest().setHref(testHref);
                getObjectUnderTest().doTag();

                Assert.fail("IllegalArgumentException expected for testHref: '"
                        + testHref + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testHref: '"
                                + testHref + "'",
                                "path must not start with '..' or '/'. Was: '" + testHref + "'",
                                e.getMessage());
            }
        }

    }

    @Test
    public void testDoTagWhenTagIsWritten() throws Throwable {
        recordGetTagDependencies();

        recordGetNewLinkWriterMap();

        recordConstructNewLinkTagWriter();

        recordCheckIfLinkTagWriterSeenBefore();

        recordGetJspWriter();

        recordLinkTagWriterDelegation();

        replay();

        getObjectUnderTest().doTag();
    }

    private void recordConstructNewLinkTagWriter() {

        EasyMock.expect(getMockBundleTagStack().getBundleTag(getMockPageContext())).andReturn(null);

        EasyMock.expect(
                getMockLinkTagWriterFactory().createLinkTagWriter(
                        getMockDevice(),
                        Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute(),
                                createArbitraryDynamicAttribute()),
                                getRequestedCssResourcePath(), getTagDependencies(), null))
                                .andReturn(getMockLinkTagWriter());
    }

    private void recordCheckIfLinkTagWriterSeenBefore() {

        EasyMock.expect(getMockLinkTagWriter().getId()).andReturn(
                getRequestedCssResourcePath()).atLeastOnce();
    }

    private void recordLinkTagWriterDelegation() throws Exception {

        getMockLinkTagWriter().writeTag(getMockJspWriter(), getMockJspFragment());
    }

    private void recordGetJspWriter() {
        EasyMock.expect(getMockPageContext().getOut()).andReturn(
                getMockJspWriter());
    }

    private void recordGetNewLinkWriterMap() {
        EasyMock.expect(
                getMockPageContext().getAttribute(
                        LinkTag.LINK_WRITER_MAP_ATTRIBUTE_NAME,
                        PageContext.REQUEST_SCOPE)).andReturn(null);
        getMockPageContext().setAttribute(
                LinkTag.LINK_WRITER_MAP_ATTRIBUTE_NAME, getLinkTagWriterMap(),
                PageContext.REQUEST_SCOPE);
        recordGetExistingLinkWriterMap();
    }

    @Test
    public void testDoTagWhenDuplicate() throws Throwable {
        setupExistingSimpleLinkTagWriter();

        recordGetTagDependencies();

        recordGetExistingLinkWriterMap();

        recordConstructNewLinkTagWriter();

        recordCheckIfLinkTagWriterSeenBefore();

        replay();

        getObjectUnderTest().doTag();
    }

    private void setupExistingSimpleLinkTagWriter() {
        getLinkTagWriterMap().put(getRequestedCssResourcePath(),
                new LinkTagWriter(getMockDevice(),
                        null, getRequestedCssResourcePath(), createTagDependencies(), null));
    }

    /**
     * @return
     */
    private String getRequestedCssResourcePath() {
        return getResourcePathTestData().getRequestedCssResourcePath();
    }

    private void recordGetExistingLinkWriterMap() {
        EasyMock.expect(
                getMockPageContext().getAttribute(
                        LinkTag.LINK_WRITER_MAP_ATTRIBUTE_NAME,
                        PageContext.REQUEST_SCOPE)).andReturn(getLinkTagWriterMap())
                        .atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    public LinkTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final LinkTag objectUnderTest) {
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
     * @return the mockJspWriter
     */
    public JspWriter getMockJspWriter() {
        return mockJspWriter;
    }

    /**
     * @param mockJspWriter the mockJspWriter to set
     */
    public void setMockJspWriter(final JspWriter mockJspWriter) {
        this.mockJspWriter = mockJspWriter;
    }

    /**
     * @return the mockJspFragment
     */
    public JspFragment getMockJspFragment() {
        return mockJspFragment;
    }

    /**
     * @param mockJspFragment the mockJspFragment to set
     */
    public void setMockJspFragment(final JspFragment mockJspFragment) {
        this.mockJspFragment = mockJspFragment;
    }

    /**
     * @return the linkTagWriterMap
     */
    public Map<String, TagWriter> getLinkTagWriterMap() {
        return linkTagWriterMap;
    }

    /**
     * @param linkTagWriterMap the linkTagWriterMap to set
     */
    public void setLinkTagWriterMap(final Map<String, TagWriter> linkTagWriterMap) {
        this.linkTagWriterMap = linkTagWriterMap;
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
     * @return the mockResourceResolverEngine
     */
    public ResourceResolverEngine
    getMockResourceResolverEngine() {
        return mockResourceResolverEngine;
    }

    /**
     * @param mockResourceResolverEngine
     *            the mockResourceResolverEngine to set
     */
    public void setMockResourceResolverEngine(
            final ResourceResolverEngine mockResourceResolverEngine) {
        this.mockResourceResolverEngine = mockResourceResolverEngine;
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
     * @return the deploymentMetadataTestData
     */
    private DeploymentMetadataTestData getDeploymentMetadataTestData() {
        return deploymentMetadataTestData;
    }

    /**
     * @return the linkTagDependencies
     */
    private LinkTagDependencies getTagDependencies() {
        return linkTagDependencies;
    }

    /**
     * @param linkTagDependencies
     *            the linkTagDependencies to set
     */
    private void setTagDepedencies(
            final LinkTagDependencies linkTagDependencies) {
        this.linkTagDependencies = linkTagDependencies;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockLinkTagWriter
     */
    public LinkTagWriter
    getMockLinkTagWriter() {
        return mockLinkTagWriter;
    }

    /**
     * @param mockLinkTagWriter
     *            the mockLinkTagWriter to set
     */
    public void setMockLinkTagWriter(
            final LinkTagWriter mockLinkTagWriter) {
        this.mockLinkTagWriter =
                mockLinkTagWriter;
    }

    /**
     * @return the mockLinkTagWriterFactory
     */
    public LinkTagWriterFactory getMockLinkTagWriterFactory() {
        return mockLinkTagWriterFactory;
    }

    /**
     * @param mockLinkTagWriterFactory
     *            the mockLinkTagWriterFactory to
     *            set
     */
    public void setMockLinkTagWriterFactory(
            final LinkTagWriterFactory mockLinkTagWriterFactory) {
        this.mockLinkTagWriterFactory = mockLinkTagWriterFactory;
    }

    public DynamicTagAttribute createRelDynamicAttribute() {
        return new DynamicTagAttribute("rel",
                "stylesheet");
    }

    public DynamicTagAttribute createTypeDynamicAttribute() {
        return new DynamicTagAttribute("type",
                "text/css");
    }

    public DynamicTagAttribute createArbitraryDynamicAttribute() {
        return new DynamicTagAttribute("arbitraryName",
                "arbitraryValue");
    }

    /**
     * @return the mockResolutionWarnLogger
     */
    public ResourceResolutionWarnLogger getMockResolutionWarnLogger() {
        return mockResolutionWarnLogger;
    }

    /**
     * @param mockResolutionWarnLogger the mockResolutionWarnLogger to set
     */
    public void setMockResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResolutionWarnLogger) {
        this.mockResolutionWarnLogger = mockResolutionWarnLogger;
    }

    /**
     * @return the mockBundleTagStack
     */
    public JspContextBundleTagStack getMockBundleTagStack() {
        return mockBundleTagStack;
    }

    /**
     * @param mockBundleTagStack the mockBundleTagStack to set
     */
    public void setMockBundleTagStack(final JspContextBundleTagStack mockBundleTagStack) {
        this.mockBundleTagStack = mockBundleTagStack;
    }
}


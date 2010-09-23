package au.com.sensis.mobile.crf.presentation.tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.config.DeploymentVersionTestData;
import au.com.sensis.mobile.crf.service.CssBundleFactory;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
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
    private JspWriter mockJspWriter;
    private Map<String, ResourceSelectorTagWriter> linkTagWriterMap;

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private CssBundleFactory mockCssBundleFactory;
    private final DeploymentVersionTestData deploymentVersionTestData
        = new DeploymentVersionTestData();
    private LinkTagCollaboratorsMemento linkTagCollaboratorsMemento;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private ResourceSelectorLinkTagWriter mockResourceSelectorLinkTagWriter;
    private ResourceSelectorLinkTagWriterFactory
        mockResourceSelectorLinkTagWriterFactory;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        ResourceSelectorLinkTagWriterFactory
                .changeDefaultResourceResolverLinkTagWriterFactorySingleton(
                        getMockResourceSelectorLinkTagWriterFactory());

        setCollaboratorsMemento(createCollaboratorsMemento());
        setObjectUnderTest(new LinkTag());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
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

        setLinkTagWriterMap(new HashMap<String, ResourceSelectorTagWriter>());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceSelectorLinkTagWriterFactory
            .restoreDefaultResourceResolverLinkTagWriterFactorySingleton();
    }

    private LinkTagCollaboratorsMemento createCollaboratorsMemento() {
        return new LinkTagCollaboratorsMemento(
                getMockResourceResolverEngine(),
                getDeploymentVersionTestData()
                    .createDevDeploymentVersion(),
                getMockCssBundleFactory(),
                getResourcePathTestData().getCssClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetCollaboratorsMemento() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(LinkTagCollaboratorsMemento.BEAN_NAME))
                .andReturn(getCollaboratorsMemento())
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
                        "href must not start with '..' or '/'. Was: '" + testHref + "'",
                        e.getMessage());
            }
        }

    }

    @Test
    public void testDoTagWhenTagIsWritten() throws Throwable {
        recordGetCollaboratorsMemento();

        recordGetNewLinkWriterMap();

        recordConstructNewLinkTagWriter();

        recordCheckIfLinkTagWriterSeenBefore();

        recordGetJspWriter();

        recordLinkTagWriterDelegation();

        replay();

        getObjectUnderTest().doTag();
    }

    private void recordConstructNewLinkTagWriter() {

        EasyMock.expect(getMockResourceSelectorLinkTagWriterFactory()
                .createResourceSelectorLinkTagWriter(getMockDevice(),
                        Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute(),
                                createArbitraryDynamicAttribute()),
                        getRequestedCssResourcePath(),
                        getCollaboratorsMemento()))
                .andReturn(getMockResourceSelectorLinkTagWriter());
    }

    private void recordCheckIfLinkTagWriterSeenBefore() {

        EasyMock.expect(getMockResourceSelectorLinkTagWriter().getId()).andReturn(
                getRequestedCssResourcePath()).atLeastOnce();
    }

    private void recordLinkTagWriterDelegation() throws Exception {

        getMockResourceSelectorLinkTagWriter().writeTag(getMockJspWriter());
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

        recordGetCollaboratorsMemento();

        recordGetExistingLinkWriterMap();

        recordConstructNewLinkTagWriter();

        recordCheckIfLinkTagWriterSeenBefore();

        replay();

        getObjectUnderTest().doTag();
    }

    private void setupExistingSimpleLinkTagWriter() {
        getLinkTagWriterMap().put(getRequestedCssResourcePath(),
                new ResourceSelectorLinkTagWriter(getMockDevice(),
                    null, getRequestedCssResourcePath(), createCollaboratorsMemento()));
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
     * @return the linkTagWriterMap
     */
    public Map<String, ResourceSelectorTagWriter> getLinkTagWriterMap() {
        return linkTagWriterMap;
    }

    /**
     * @param linkTagWriterMap the linkTagWriterMap to set
     */
    public void setLinkTagWriterMap(final Map<String, ResourceSelectorTagWriter> linkTagWriterMap) {
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
     * @return the deploymentVersionTestData
     */
    private DeploymentVersionTestData getDeploymentVersionTestData() {
        return deploymentVersionTestData;
    }

    /**
     * @return the linkTagCollaboratorsMemento
     */
    private LinkTagCollaboratorsMemento getCollaboratorsMemento() {
        return linkTagCollaboratorsMemento;
    }

    /**
     * @param linkTagCollaboratorsMemento
     *            the linkTagCollaboratorsMemento to set
     */
    private void setCollaboratorsMemento(
            final LinkTagCollaboratorsMemento linkTagCollaboratorsMemento) {
        this.linkTagCollaboratorsMemento = linkTagCollaboratorsMemento;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockCssBundleFactory
     */
    public CssBundleFactory getMockCssBundleFactory() {
        return mockCssBundleFactory;
    }

    /**
     * @param mockCssBundleFactory the mockCssBundleFactory to set
     */
    public void setMockCssBundleFactory(final CssBundleFactory mockCssBundleFactory) {
        this.mockCssBundleFactory = mockCssBundleFactory;
    }

    /**
     * @return the mockResourceSelectorLinkTagWriter
     */
    public ResourceSelectorLinkTagWriter
        getMockResourceSelectorLinkTagWriter() {
        return mockResourceSelectorLinkTagWriter;
    }

    /**
     * @param mockResourceSelectorLinkTagWriter
     *            the mockResourceSelectorLinkTagWriter to set
     */
    public void setMockResourceSelectorLinkTagWriter(
            final ResourceSelectorLinkTagWriter mockResourceSelectorLinkTagWriter) {
        this.mockResourceSelectorLinkTagWriter =
                mockResourceSelectorLinkTagWriter;
    }

    /**
     * @return the mockResourceSelectorLinkTagWriterFactory
     */
    public ResourceSelectorLinkTagWriterFactory getMockResourceSelectorLinkTagWriterFactory() {
        return mockResourceSelectorLinkTagWriterFactory;
    }

    /**
     * @param mockResourceSelectorLinkTagWriterFactory
     *            the mockResourceSelectorLinkTagWriterFactory to
     *            set
     */
    public void setMockResourceSelectorLinkTagWriterFactory(
            final ResourceSelectorLinkTagWriterFactory mockResourceSelectorLinkTagWriterFactory) {
        this.mockResourceSelectorLinkTagWriterFactory = mockResourceSelectorLinkTagWriterFactory;
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
}


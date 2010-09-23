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
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.service.ScriptBundleFactory;
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@script ScriptTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagTestCase extends AbstractJUnit4TestCase {
    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private ScriptTag objectUnderTest;
    private PageContext mockPageContext;
    private JspWriter mockJspWriter;
    private Map<String, ResourceSelectorTagWriter> scriptTagWriterMap;

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private ScriptBundleFactory mockScriptBundleFactory;
    private final DeploymentVersionTestData deploymentVersionTestData
        = new DeploymentVersionTestData();
    private ScriptTagDependencies scriptTagDependencies;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private ResourceSelectorScriptTagWriter mockResourceSelectorScriptTagWriter;
    private ResourceSelectorScriptTagWriterFactory
        mockResourceSelectorScriptTagWriterFactory;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        ResourceSelectorScriptTagWriterFactory
                .changeDefaultResourceSelectorScriptTagWriterFactorySingleton(
                        getMockResourceSelectorScriptTagWriterFactory());

        setCollaboratorsMemento(createCollaboratorsMemento());
        setObjectUnderTest(new ScriptTag());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setHref(getRequestedJavaScriptResourcePath());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createTitleDynamicAttribute().getLocalName(),
                createTitleDynamicAttribute().getValue());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createTypeDynamicAttribute().getLocalName(),
                createTypeDynamicAttribute().getValue());
        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI,
                createArbitraryDynamicAttribute().getLocalName(),
                createArbitraryDynamicAttribute().getValue());

        setSpringMockServletContext(new MockServletContext());

        setResourceSelectorTagWriterMap(new HashMap<String, ResourceSelectorTagWriter>());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceSelectorScriptTagWriterFactory
            .restoreDefaultResourceSelectorScriptTagWriterFactorySingleton();
    }

    private ScriptTagDependencies createCollaboratorsMemento() {
        return new ScriptTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentVersionTestData()
                    .createDevDeploymentVersion(),
                getMockScriptBundleFactory(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetCollaboratorsMemento() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(ScriptTagDependencies.BEAN_NAME))
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

        recordGetNewScriptWriterMap();

        recordConstructNewScriptTagWriter();

        recordCheckIfScriptTagWriterSeenBefore();

        recordGetJspWriter();

        recordScriptTagWriterDelegation();

        replay();

        getObjectUnderTest().doTag();
    }

    private void recordConstructNewScriptTagWriter() {

        EasyMock.expect(getMockResourceSelectorScriptTagWriterFactory()
                .createResourceSelectorScriptTagWriter(getMockDevice(),
                        Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute(),
                                createArbitraryDynamicAttribute()),
                        getRequestedJavaScriptResourcePath(),
                        getCollaboratorsMemento()))
                .andReturn(getMockResourceSelectorScriptTagWriter());
    }

    private void recordCheckIfScriptTagWriterSeenBefore() {

        EasyMock.expect(getMockResourceSelectorScriptTagWriter().getId()).andReturn(
                getRequestedJavaScriptResourcePath()).atLeastOnce();
    }

    private void recordScriptTagWriterDelegation() throws Exception {

        getMockResourceSelectorScriptTagWriter().writeTag(getMockJspWriter());
    }

    private void recordGetJspWriter() {
        EasyMock.expect(getMockPageContext().getOut()).andReturn(
                getMockJspWriter());
    }

    private void recordGetNewScriptWriterMap() {
        EasyMock.expect(
                getMockPageContext().getAttribute(
                        ScriptTag.SCRIPT_WRITER_MAP_ATTRIBUTE_NAME,
                        PageContext.REQUEST_SCOPE)).andReturn(null);
        getMockPageContext().setAttribute(
                ScriptTag.SCRIPT_WRITER_MAP_ATTRIBUTE_NAME, getResourceSelectorTagWriterMap(),
                PageContext.REQUEST_SCOPE);
        recordGetExistingScriptWriterMap();
    }

    @Test
    public void testDoTagWhenDuplicate() throws Throwable {
        setupExistingTagWriter();

        recordGetCollaboratorsMemento();

        recordGetExistingScriptWriterMap();

        recordConstructNewScriptTagWriter();

        recordCheckIfScriptTagWriterSeenBefore();

        replay();

        getObjectUnderTest().doTag();
    }

    private void setupExistingTagWriter() {
        getResourceSelectorTagWriterMap().put(
                getRequestedJavaScriptResourcePath(),
                new ResourceSelectorScriptTagWriter(getMockDevice(), null,
                        getRequestedJavaScriptResourcePath(),
                        createCollaboratorsMemento()));
    }

    /**
     * @return
     */
    private String getRequestedJavaScriptResourcePath() {
        return getResourcePathTestData().getRequestedNamedScriptResourcePath();
    }

    private void recordGetExistingScriptWriterMap() {
        EasyMock.expect(
                getMockPageContext().getAttribute(
                        ScriptTag.SCRIPT_WRITER_MAP_ATTRIBUTE_NAME,
                        PageContext.REQUEST_SCOPE)).andReturn(getResourceSelectorTagWriterMap())
                .atLeastOnce();
    }

    /**
     * @return the objectUnderTest
     */
    public ScriptTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final ScriptTag objectUnderTest) {
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
     * @return the scriptTagWriterMap
     */
    public Map<String, ResourceSelectorTagWriter> getResourceSelectorTagWriterMap() {
        return scriptTagWriterMap;
    }

    /**
     * @param scriptTagWriterMap
     *            the scriptTagWriterMap to set
     */
    public void setResourceSelectorTagWriterMap(
            final Map<String, ResourceSelectorTagWriter> scriptTagWriterMap) {
        this.scriptTagWriterMap = scriptTagWriterMap;
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
    public ResourceResolverEngine getMockResourceResolverEngine() {
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
     * @return the scriptTagDependencies
     */
    private ScriptTagDependencies getCollaboratorsMemento() {
        return scriptTagDependencies;
    }

    /**
     * @param scriptTagDependencies
     *            the scriptTagDependencies to set
     */
    private void setCollaboratorsMemento(
            final ScriptTagDependencies scriptTagDependencies) {
        this.scriptTagDependencies = scriptTagDependencies;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the mockScriptBundleFactory
     */
    public ScriptBundleFactory getMockScriptBundleFactory() {
        return mockScriptBundleFactory;
    }

    /**
     * @param mockScriptBundleFactory the mockScriptBundleFactory to set
     */
    public void setMockScriptBundleFactory(final ScriptBundleFactory mockScriptBundleFactory) {
        this.mockScriptBundleFactory = mockScriptBundleFactory;
    }

    /**
     * @return the mockResourceSelectorScriptTagWriter
     */
    public ResourceSelectorScriptTagWriter
        getMockResourceSelectorScriptTagWriter() {
        return mockResourceSelectorScriptTagWriter;
    }

    /**
     * @param mockResourceSelectorScriptTagWriter
     *            the mockResourceSelectorScriptTagWriter to set
     */
    public void setMockResourceSelectorScriptTagWriter(
            final ResourceSelectorScriptTagWriter mockResourceSelectorScriptTagWriter) {
        this.mockResourceSelectorScriptTagWriter =
                mockResourceSelectorScriptTagWriter;
    }

    /**
     * @return the mockResourceSelectorScriptTagWriterFactory
     */
    public ResourceSelectorScriptTagWriterFactory getMockResourceSelectorScriptTagWriterFactory() {
        return mockResourceSelectorScriptTagWriterFactory;
    }

    /**
     * @param mockResourceSelectorScriptTagWriterFactory
     *            the mockResourceSelectorScriptTagWriterFactory to set
     */
    public void setMockResourceSelectorScriptTagWriterFactory(
            final ResourceSelectorScriptTagWriterFactory
                mockResourceSelectorScriptTagWriterFactory) {
        this.mockResourceSelectorScriptTagWriterFactory =
                mockResourceSelectorScriptTagWriterFactory;
    }

    public DynamicTagAttribute createTitleDynamicAttribute() {
        return new DynamicTagAttribute("title",
                "My Image");
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


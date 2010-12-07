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

import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
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
    private JspFragment mockJspFragment;
    private JspWriter mockJspWriter;
    private Map<String, TagWriter> scriptTagWriterMap;

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();
    private ScriptTagDependencies scriptTagDependencies;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private ScriptTagWriter mockScriptTagWriter;
    private ScriptTagWriterFactory
    mockScriptTagWriterFactory;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        ScriptTagWriterFactory
        .changeDefaultScriptTagWriterFactorySingleton(
                getMockScriptTagWriterFactory());

        setTagDependencies(createTagDependencies());
        setObjectUnderTest(new ScriptTag());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setJspBody(getMockJspFragment());
        getObjectUnderTest().setSrc(getRequestedJavaScriptResourcePath());
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

        setTagWriterMap(new HashMap<String, TagWriter>());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ScriptTagWriterFactory
        .restoreDefaultScriptTagWriterFactorySingleton();
    }

    private ScriptTagDependencies createTagDependencies() {
        return new ScriptTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetTagDependencies() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(ScriptTagDependencies.BEAN_NAME))
                .andReturn(getTagDependencies())
                .atLeastOnce();
    }

    @Test
    public void testDoTagWhenSrcAndNameAreBlank() throws Throwable {
        final String[] testValues = new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            getObjectUnderTest().setSrc(testValue);
            getObjectUnderTest().setName(testValue);

            try {
                getObjectUnderTest().doTag();

                Assert.fail("IllegalArgumentException expected for testValue: '" + testValue + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals("IllegalArgumentException has wrong message",
                        "You must set either the src or name attributes but not both. src='"
                        + testValue + "'; name='" + testValue + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testDoTagWhenSrcAndNameAreNotBlank() throws Throwable {
        getObjectUnderTest().setSrc("common/main.js");
        getObjectUnderTest().setName("myName");

        try {
            getObjectUnderTest().doTag();

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "You must set either the src or name attributes but not both. src='"
                    + "common/main.js" + "'; name='" + "myName" + "'", e.getMessage());
        }
    }

    @Test
    public void testDoTagWithInvalidSrc() throws Throwable {
        final String[] testSrcs =
        { "../some/where", "/somewhere" };

        for (final String testSrc : testSrcs) {
            try {
                getObjectUnderTest().setSrc(testSrc);
                getObjectUnderTest().doTag();

                Assert.fail("IllegalArgumentException expected for testSrc: '"
                        + testSrc + "'");
            } catch (final IllegalArgumentException e) {

                Assert.assertEquals(
                        "IllegalArgumentException has wrong message for testSrc: '"
                        + testSrc + "'",
                        "src must not start with '..' or '/'. Was: '" + testSrc + "'",
                        e.getMessage());
            }
        }

    }

    @Test
    public void testDoTagWhenTagIsWritten() throws Throwable {
        recordGetTagDependencies();

        recordGetNewScriptWriterMap();

        recordConstructNewScriptTagWriter();

        recordCheckIfScriptTagWriterSeenBefore();

        recordGetJspWriter();

        recordScriptTagWriterDelegation();

        replay();

        getObjectUnderTest().doTag();
    }

    private void recordConstructNewScriptTagWriter() {

        EasyMock.expect(getMockScriptTagWriterFactory()
                .createScriptTagWriter(getMockDevice(),
                        Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute(),
                                createArbitraryDynamicAttribute()),
                                getRequestedJavaScriptResourcePath(),
                                null,
                                getTagDependencies()))
                                .andReturn(getMockScriptTagWriter());
    }

    private void recordCheckIfScriptTagWriterSeenBefore() {

        EasyMock.expect(getMockScriptTagWriter().getId()).andReturn(
                getRequestedJavaScriptResourcePath()).atLeastOnce();
    }

    private void recordScriptTagWriterDelegation() throws Exception {

        getMockScriptTagWriter().writeTag(getMockJspWriter(), getMockJspFragment());
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
                ScriptTag.SCRIPT_WRITER_MAP_ATTRIBUTE_NAME, getTagWriterMap(),
                PageContext.REQUEST_SCOPE);
        recordGetExistingScriptWriterMap();
    }

    @Test
    public void testDoTagWhenDuplicate() throws Throwable {
        setupExistingTagWriter();

        recordGetTagDependencies();

        recordGetExistingScriptWriterMap();

        recordConstructNewScriptTagWriter();

        recordCheckIfScriptTagWriterSeenBefore();

        replay();

        getObjectUnderTest().doTag();
    }

    private void setupExistingTagWriter() {
        getTagWriterMap().put(
                getRequestedJavaScriptResourcePath(),
                new ScriptTagWriter(getMockDevice(), null,
                        getRequestedJavaScriptResourcePath(), null,
                        createTagDependencies()));
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
                        PageContext.REQUEST_SCOPE)).andReturn(getTagWriterMap())
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
     * @return the scriptTagWriterMap
     */
    public Map<String, TagWriter> getTagWriterMap() {
        return scriptTagWriterMap;
    }

    /**
     * @param scriptTagWriterMap
     *            the scriptTagWriterMap to set
     */
    public void setTagWriterMap(
            final Map<String, TagWriter> scriptTagWriterMap) {
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
     * @return the deploymentMetadataTestData
     */
    private DeploymentMetadataTestData getDeploymentMetadataTestData() {
        return deploymentMetadataTestData;
    }

    /**
     * @return the scriptTagDependencies
     */
    private ScriptTagDependencies getTagDependencies() {
        return scriptTagDependencies;
    }

    /**
     * @param scriptTagDependencies
     *            the scriptTagDependencies to set
     */
    private void setTagDependencies(
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
     * @return the mockScriptTagWriter
     */
    public ScriptTagWriter getMockScriptTagWriter() {
        return mockScriptTagWriter;
    }

    /**
     * @param mockScriptTagWriter
     *            the mockScriptTagWriter to set
     */
    public void setMockScriptTagWriter(
            final ScriptTagWriter mockScriptTagWriter) {
        this.mockScriptTagWriter =
            mockScriptTagWriter;
    }

    /**
     * @return the mockScriptTagWriterFactory
     */
    public ScriptTagWriterFactory getMockScriptTagWriterFactory() {
        return mockScriptTagWriterFactory;
    }

    /**
     * @param mockScriptTagWriterFactory
     *            the mockScriptTagWriterFactory to set
     */
    public void setMockScriptTagWriterFactory(
            final ScriptTagWriterFactory
            mockScriptTagWriterFactory) {
        this.mockScriptTagWriterFactory =
            mockScriptTagWriterFactory;
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


package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
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
 * Unit test {@link ScriptSrcPrefixTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptSrcPrefixTagTestCase extends AbstractJUnit4TestCase {

    private ScriptSrcPrefixTag objectUnderTest;

    private Device mockDevice;
    private JspWriter mockJspWriter;
    private ResourceResolverEngine mockResourceResolverEngine;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private PageContext mockPageContext;
    private WebApplicationContext mockWebApplicationContext;

    private MockServletContext springMockServletContext;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();

    private ScriptTagDependencies scriptTagDependencies;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ScriptSrcPrefixTag());

        getObjectUnderTest().setJspContext(getMockPageContext());

        setScriptTagDependencies(createScriptTagDependencies());
        setSpringMockServletContext(new MockServletContext());
    }

    @Test
    public void testDoTagWhenVarNotBlank() throws Throwable {
        getObjectUnderTest().setVar("myVar");

        recordGetScriptTagDependencies();

        getMockPageContext().setAttribute(
                "myVar",
                getResourcePathTestData().getScriptClientPathPrefix()
                + getDeploymentMetadataTestData().createDevDeploymentMetadata()
                .getVersion() + "/javascript/");

        replay();

        getObjectUnderTest().doTag();
    }

    @Test
    public void testDoTagWhenVarBlank() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };

        for (final String testValue : testValues) {
            getObjectUnderTest().setVar(testValue);

            recordGetScriptTagDependencies();

            EasyMock.expect(getMockPageContext().getOut()).andReturn(getMockJspWriter());

            getMockJspWriter().write(
                    getResourcePathTestData().getScriptClientPathPrefix()
                    + getDeploymentMetadataTestData().createDevDeploymentMetadata()
                    .getVersion() + "/javascript/");

            replay();

            getObjectUnderTest().doTag();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }
    }

    private void recordGetScriptTagDependencies() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(ScriptTagDependencies.BEAN_NAME))
                .andReturn(getScriptTagDependencies())
                .atLeastOnce();
    }

    private ScriptTagDependencies createScriptTagDependencies() {
        return new ScriptTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger(), null);
    }


    /**
     * @return the objectUnderTest
     */
    private ScriptSrcPrefixTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ScriptSrcPrefixTag objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
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
     * @return the scriptTagDependencies
     */
    private ScriptTagDependencies getScriptTagDependencies() {
        return scriptTagDependencies;
    }

    /**
     * @param scriptTagDependencies the scriptTagDependencies to set
     */
    private void setScriptTagDependencies(final ScriptTagDependencies scriptTagDependencies) {
        this.scriptTagDependencies = scriptTagDependencies;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the deploymentMetadataTestData
     */
    private DeploymentMetadataTestData getDeploymentMetadataTestData() {
        return deploymentMetadataTestData;
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

}

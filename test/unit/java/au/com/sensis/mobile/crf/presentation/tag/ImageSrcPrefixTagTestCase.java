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
 * Unit test {@link ImageSrcPrefixTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageSrcPrefixTagTestCase extends AbstractJUnit4TestCase {

    private ImageSrcPrefixTag objectUnderTest;

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

    private ImageTagDependencies imageTagDependencies;



    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ImageSrcPrefixTag());

        getObjectUnderTest().setJspContext(getMockPageContext());

        setImageTagDependencies(createImageTagDependencies());
        setSpringMockServletContext(new MockServletContext());
    }

    @Test
    public void testDoTagWhenVarNotBlank() throws Throwable {
        getObjectUnderTest().setVar("myVar");

        recordGetImageTagDependencies();

        getMockPageContext().setAttribute(
                "myVar",
                getResourcePathTestData().getImageClientPathPrefix()
                + getDeploymentMetadataTestData().createDevDeploymentMetadata()
                .getVersion() + "/images/");

        replay();

        getObjectUnderTest().doTag();
    }

    @Test
    public void testDoTagWhenVarBlank() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "  " };

        for (final String testValue : testValues) {
            getObjectUnderTest().setVar(testValue);

            recordGetImageTagDependencies();

            EasyMock.expect(getMockPageContext().getOut()).andReturn(getMockJspWriter());

            getMockJspWriter().write(
                    getResourcePathTestData().getImageClientPathPrefix()
                    + getDeploymentMetadataTestData().createDevDeploymentMetadata()
                    .getVersion() + "/images/");

            replay();

            getObjectUnderTest().doTag();

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }
    }

    private void recordGetImageTagDependencies() {

        EasyMock.expect(getMockPageContext().getServletContext()).andReturn(
                getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());

        EasyMock.expect(
                getMockWebApplicationContext().getBean(ImageTagDependencies.BEAN_NAME))
                .andReturn(getImageTagDependencies())
                .atLeastOnce();
    }

    private ImageTagDependencies createImageTagDependencies() {
        return new ImageTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getImageClientPathPrefix(),
                getMockResolutionWarnLogger());
    }


    /**
     * @return the objectUnderTest
     */
    private ImageSrcPrefixTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageSrcPrefixTag objectUnderTest) {
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
     * @return the imageTagDependencies
     */
    private ImageTagDependencies getImageTagDependencies() {
        return imageTagDependencies;
    }

    /**
     * @param imageTagDependencies the imageTagDependencies to set
     */
    private void setImageTagDependencies(final ImageTagDependencies imageTagDependencies) {
        this.imageTagDependencies = imageTagDependencies;
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

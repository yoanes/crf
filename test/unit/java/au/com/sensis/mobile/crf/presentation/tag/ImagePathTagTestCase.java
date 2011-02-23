package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.service.ImageResourceBean;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImagePathTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImagePathTagTestCase extends AbstractJUnit4TestCase {

    private ImagePathTag objectUnderTest;
    private Device mockDevice;

    private PageContext mockPageContext;
    private JspWriter mockJspWriter;
    private StringWriter stringWriter;

    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private ImageTagDependencies imageTagDependencies;
    private FileIoFacade mockFileIoFacade;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private ImageResourceBean mockResource;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private final GroupTestData groupTestData = new GroupTestData();


    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new ImagePathTag());

        setImageTagDependencies(createImageTagDependencies());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setSrc(getResourcePathTestData()
                .getRequestedImageResourcePath());

        setSpringMockServletContext(new MockServletContext());

        setStringWriter(new StringWriter());
        setMockJspWriter(new MockJspWriter(getStringWriter()));

        resetMocksAndTestData();
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

    private ImageTagDependencies createImageTagDependencies() {
        return new ImageTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getImageClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    @Test
    public void testDoTagWithInvalidHref() throws Throwable {
        final String[] testSrcs =
        { "../some/where", "/somewhere", null, StringUtils.EMPTY, " ",
        "  " };

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
                        "path must not start with '..' or '/'. Was: '"
                        + testSrc + "'", e.getMessage());
            }
        }

    }

    @Test
    public void testDoTag() throws Throwable {
        final TestData[] testDataArray = getTestData();
        for (int i = 0; i < testDataArray.length; i++) {
            try {

                initObjectUnderTest(testDataArray[i]);

                recordGetImageTagDependencies();

                recordLookupRequestedResourceWhenFound(testDataArray[i].getResource());

                if (testDataArray[i].getResource() != null) {
                    recordResourceEndsWithDotNull(Boolean.FALSE);
                    recordGetResourceNewPath();
                    recordGetJspWriter();
                } else {
                    recordGetJspWriter();
                }

                replay();

                getObjectUnderTest().doTag();

                Assert.assertEquals("incorrect output for testData at index " + i + ": '",
                        testDataArray[i].getOutputString(), getStringWriter().getBuffer()
                                .toString());

                // Explicit verify since we are in a loop.
                verify();
                resetMocksAndTestData();
            } catch (final Exception e) {
                throw new RuntimeException("Error for testData at index " + i + ": '"
                        + testDataArray[i] + "'", e);
            }
        }
    }

    @Test
    public void testDoTagWhenDotNullResource() throws Throwable {

        recordGetImageTagDependencies();

        recordLookupRequestedResourceWhenFound(getMockResource());

        recordResourceEndsWithDotNull(Boolean.TRUE);

        recordGetJspWriter();

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("incorrect output string", StringUtils.EMPTY,
                getStringWriter().getBuffer().toString());

    }

    @Test
    public void testDoTagWithRealImageResourceBean() throws Throwable {

        final int width = 222;
        final int height = 111;

        final Resource toGetValuesFrom =
                getResourcePathTestData().getMappedDefaultGroupPngImageResourcePath();

        final ImageResourceBean imageResource =
                new ImageResourceBean(toGetValuesFrom.getOriginalPath(), toGetValuesFrom
                        .getNewPath(), toGetValuesFrom.getRootResourceDir(), getGroupTestData()
                        .createDefaultGroup());

        imageResource.setImageHeight(height);
        imageResource.setImageWidth(width);

        recordGetImageTagDependencies();

        recordLookupRequestedResourceWhenFound(imageResource);

        recordGetJspWriter();

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("incorrect output,", getMappedDefaultGroupPngImageResourceHref(),
                getStringWriter().getBuffer().toString());

    }

    private void recordResourceEndsWithDotNull(final Boolean endsWithDotNull) {
        EasyMock.expect(getMockResource().newPathEndsWithDotNull())
        .andReturn(endsWithDotNull);
    }

    private void initObjectUnderTest(final TestData testData) throws Exception {
        setObjectUnderTest(new ImagePathTag());

        setImageTagDependencies(createImageTagDependencies());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setSrc(
                getResourcePathTestData().getRequestedImageResourcePath());

    }

    private void resetMocksAndTestData() {
        reset();
        setStringWriter(new StringWriter());
        setMockJspWriter(new MockJspWriter(getStringWriter()));
    }

    private void recordGetResourceNewPath() {

        EasyMock.expect(getMockResource().getNewPath())
        .andReturn(
                getMappedDefaultGroupPngImageResourcePath()
                .getNewPath()).atLeastOnce();
    }

    private Resource getMappedDefaultGroupPngImageResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupPngImageResourcePath();
    }

    private void recordLookupRequestedResourceWhenFound(
            final Resource resource) throws IOException {

        EasyMock.expect(
                getMockResourceResolverEngine().getResource(
                        getMockDevice(),
                        getResourcePathTestData()
                        .getRequestedImageResourcePath())).andReturn(
                                resource);
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

    private void recordGetJspWriter() {
        EasyMock.expect(getMockPageContext().getOut()).andReturn(
                getMockJspWriter()).atLeastOnce();
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
     * @return the objectUnderTest
     */
    private ImagePathTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImagePathTag objectUnderTest) {
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
     * @return the springMockServletContext
     */
    public MockServletContext getSpringMockServletContext() {
        return springMockServletContext;
    }

    /**
     * @param springMockServletContext the springMockServletContext to set
     */
    public void setSpringMockServletContext(
            final MockServletContext springMockServletContext) {
        this.springMockServletContext = springMockServletContext;
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
     * @return the imageTagDependencies
     */
    public ImageTagDependencies getImageTagDependencies() {
        return imageTagDependencies;
    }

    /**
     * @param imageTagDependencies the imageTagDependencies to set
     */
    public void setImageTagDependencies(
            final ImageTagDependencies imageTagDependencies) {
        this.imageTagDependencies = imageTagDependencies;
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
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
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
     * @return the stringWriter
     */
    private StringWriter getStringWriter() {
        return stringWriter;
    }

    /**
     * @param stringWriter the stringWriter to set
     */
    private void setStringWriter(final StringWriter stringWriter) {
        this.stringWriter = stringWriter;
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
     * @return the deploymentMetadataTestData
     */
    private DeploymentMetadataTestData getDeploymentMetadataTestData() {
        return deploymentMetadataTestData;
    }

    private TestData[] getTestData() {
        return new TestData [] {
                createTestDataSingleMappedResource(),

                createTestDataNoMappedResource()
        };
    }

    private TestData createTestDataSingleMappedResource() {
        return new TestData(
                getMockResource(),
                getMappedDefaultGroupPngImageResourceHref());
    }

    private TestData createTestDataNoMappedResource() {
        return new TestData(null, getResourcePathTestData().getImageClientPathPrefix()
                + getResourcePathTestData().getDeploymentVersion() + "/"
                + getResourcePathTestData().getRequestedImageResourcePath());
    }

    private String getMappedDefaultGroupPngImageResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupPngImageResourceHref();
    }

    /**
     * @return the mockResource
     */
    public ImageResourceBean getMockResource() {
        return mockResource;
    }

    /**
     * @param mockResource the mockResource to set
     */
    public void setMockResource(final ImageResourceBean mockResource) {
        this.mockResource = mockResource;
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
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }


    private static class TestData {
        private final Resource resource;
        private final String outputString;

        public TestData(final Resource resource,
                final String outputString) {
            super();
            this.resource = resource;
            this.outputString = outputString;
        }

        /**
         * @return the outputString
         */
        private String getOutputString() {
            return outputString;
        }

        /**
         * @return the resource
         */
        private Resource getResource() {
            return resource;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
            toStringBuilder.append("resource", getResource());
            toStringBuilder.append("outputString", getOutputString());
            return toStringBuilder.toString();
        }
    }
}



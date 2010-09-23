package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import au.com.sensis.mobile.crf.config.DeploymentVersionTestData;
import au.com.sensis.mobile.crf.service.FileIoFacade;
import au.com.sensis.mobile.crf.service.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImageTag}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTagTestCase extends AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private ImageTag objectUnderTest;
    private Device mockDevice;

    private PageContext mockPageContext;
    private JspWriter mockJspWriter;
    private StringWriter stringWriter;

    private final DeploymentVersionTestData deploymentVersionTestData
        = new DeploymentVersionTestData();

    private MockServletContext springMockServletContext;
    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private ImageTagDependencies imageTagDependencies;
    private FileIoFacade mockFileIoFacade;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private MappedResourcePath mockMappedResourcePath;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;


    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new ImageTag());

        setImageTagDependencies(createImageTagDependencies());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setHref(getResourcePathTestData()
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
                getDeploymentVersionTestData().createDevDeploymentVersion(),
                getResourcePathTestData().getImageClientPathPrefix(),
                getMockResolutionWarnLogger());
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
                        "href must not start with '..' or '/'. Was: '"
                                + testHref + "'", e.getMessage());
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

                recordLookupRequestedResourceWhenFound(testDataArray[i]
                        .getMappedResourcePath());

                if (StringUtils.isNotEmpty(testDataArray[i].getOutputString())) {
                    recordMappedResourcePathEndsWithDotNull(Boolean.FALSE);
                    recordGetMappedResourcePathNewPath();
                    recordGetJspWriter();
                } else {
                    recordLogResourceNotFoundWarning();
                }

                replay();

                getObjectUnderTest().doTag();

                Assert.assertEquals("incorrect output for testData at index "
                        + i + ": '" + testDataArray[i] + "'", testDataArray[i]
                        .getOutputString(), getStringWriter().getBuffer()
                        .toString());

                // Explicit verify since we are in a loop.
                verify();
                resetMocksAndTestData();
            } catch (final Exception e) {
                throw new RuntimeException("Error for testData at index " + i
                        + ": '" + testDataArray[i] + "'", e);
            }
        }
    }

    private void recordLogResourceNotFoundWarning() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled())
            .andReturn(Boolean.TRUE);
        getMockResolutionWarnLogger().warn(
                "No resource was found for requested resource '"
                        + getResourcePathTestData().getRequestedImageResourcePath()
                        + "' and device " + getMockDevice() + "");
    }

    @Test
    public void testDoTagWhenDotNullMappedResourcePath() throws Throwable {

        recordGetImageTagDependencies();

        recordLookupRequestedResourceWhenFound(getMockMappedResourcePath());

        recordMappedResourcePathEndsWithDotNull(Boolean.TRUE);

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("incorrect output string", StringUtils.EMPTY,
                getStringWriter().getBuffer().toString());

    }

    private void recordMappedResourcePathEndsWithDotNull(final Boolean endsWithDotNull) {
        EasyMock.expect(getMockMappedResourcePath().endsWithDotNull())
                .andReturn(endsWithDotNull);
    }

    private void initObjectUnderTest(final TestData testData) throws Exception {
        setObjectUnderTest(new ImageTag());

        setImageTagDependencies(createImageTagDependencies());

        getObjectUnderTest().setDevice(getMockDevice());
        getObjectUnderTest().setJspContext(getMockPageContext());
        getObjectUnderTest().setHref(
                getResourcePathTestData().getRequestedImageResourcePath());

        for (final DynamicTagAttribute dynamicTagAttribute : testData
                .getDynamicAttributes()) {
            getObjectUnderTest().setDynamicAttribute(
                    dynamicTagAttribute.getNamespaceUri(),
                    dynamicTagAttribute.getLocalName(),
                    dynamicTagAttribute.getValue());
        }

    }

    private void resetMocksAndTestData() {
        reset();
        setStringWriter(new StringWriter());
        setMockJspWriter(new MockJspWriter(getStringWriter()));
    }

    private void recordGetMappedResourcePathNewPath() {

        EasyMock.expect(getMockMappedResourcePath().getNewResourcePath())
                .andReturn(
                        getMappedDefaultGroupPngImageResourcePath()
                                .getNewResourcePath()).atLeastOnce();
    }

    private MappedResourcePath getMappedDefaultGroupPngImageResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupPngImageResourcePath();
    }

    private void recordLookupRequestedResourceWhenFound(
            final MappedResourcePath mappedResourcePath) throws IOException {

        EasyMock.expect(
                getMockResourceResolverEngine().getResourcePath(
                        getMockDevice(),
                        getResourcePathTestData()
                                .getRequestedImageResourcePath())).andReturn(
                mappedResourcePath);
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
                getMockJspWriter());
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
    private ImageTag getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageTag objectUnderTest) {
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
     * @return the deploymentVersionTestData
     */
    private DeploymentVersionTestData getDeploymentVersionTestData() {
        return deploymentVersionTestData;
    }

    private TestData[] getTestData() {
        return new TestData [] {
            createTestDataNoDynamicAttributesSingleMappedResource(),
            createTestDataOneDynamicAttributeSingleMappedResource(),
            createTestDataTwoDynamicAttributesSingleMappedResource(),

            createTestDataNoDynamicAttributesNoMappedResource(),
        };
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResource() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                getMockMappedResourcePath(),
                "<img src=\"" + getMappedDefaultGroupPngImageResourceHref()
                    + "\" " + "/>\n");
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResource() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                getMockMappedResourcePath(),
                "<img src=\""
                + getMappedDefaultGroupPngImageResourceHref()
                + "\" title=\"unmetered usage\" />\n");
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResource() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createAltDynamicAttribute()),
                getMockMappedResourcePath(),
                "<img src=\""
                + getMappedDefaultGroupPngImageResourceHref()
                + "\" title=\"unmetered usage\" alt=\"unmetered\" />\n");
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResource() {
        return new TestData(new ArrayList<DynamicTagAttribute>(), null,
                StringUtils.EMPTY);
    }

    private DynamicTagAttribute createTitleDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "title",
                "unmetered usage");
    }

    private DynamicTagAttribute createAltDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "alt",
            "unmetered");
    }

    private String getMappedDefaultGroupPngImageResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupPngImageResourceHref();
    }

    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final MappedResourcePath mappedResourcePath;
        private final String outputString;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final MappedResourcePath mappedResourcePath,
                final String outputString) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.mappedResourcePath = mappedResourcePath;
            this.outputString = outputString;
        }

        /**
         * @return the dynamicAttributes
         */
        private List<DynamicTagAttribute> getDynamicAttributes() {
            return dynamicAttributes;
        }

        /**
         * @return the outputString
         */
        private String getOutputString() {
            return outputString;
        }

        /**
         * @return the mappedResourcePath
         */
        private MappedResourcePath getMappedResourcePath() {
            return mappedResourcePath;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
            toStringBuilder.append("dynamicAttributes", getDynamicAttributes());
            toStringBuilder.append("mappedResourcePath", getMappedResourcePath());
            toStringBuilder.append("outputString", getOutputString());
            return toStringBuilder.toString();
        }
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



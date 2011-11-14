package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link LinkTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagWriterTestCase extends AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private LinkTagWriter objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();
    private JspFragment mockJspFragment;
    private JspWriter mockJspWriter;
    private StringWriter stringWriter;

    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private FileIoFacade mockFileIoFacade;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private BundleLinksTag mockBundleLinksTag;

    /**
     * Test setup.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

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

    private DynamicTagAttribute createTypeDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "type",
        "text/css");
    }

    private DynamicTagAttribute createRelDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "rel",
        "stylesheet");
    }

    private void resetMocksAndTestData() {
        reset();
        setStringWriter(new StringWriter());
        setMockJspWriter(new MockJspWriter(getStringWriter()));
    }

    @Test
    public void testDoTag() throws Throwable {
        final TestData[] testDataArray = getTestData();
        for (int i = 0; i < testDataArray.length; i++) {
            try {
                setObjectUnderTest(createObjectUnderTest(testDataArray[i]));

                recordGetResource(testDataArray[i].getResources());

                replay();

                getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

                Assert.assertEquals("incorrect output for testData at index " + i,
                        testDataArray[i].getOutputString(),
                        getStringWriter().getBuffer().toString());

                // Explicit verify since we are in a loop.
                verify();

                resetMocksAndTestData();
            } catch (final Exception e) {
                throw new RuntimeException("Error for testData at index " + i
                        + ": '" + testDataArray[i] + "'", e);
            }
        }
    }

    @Test
    public void testDoTagWhenParentBundleLinksTag() throws Throwable {
        setObjectUnderTest(new LinkTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(),
                getRequestedCssResourcePath(),
                createTagDependencies(),
                getMockBundleLinksTag()));

        final List<Resource> foundResources = Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                getMappediPhoneGroupCssResourcePath());
        recordGetResource(foundResources);

        recordBundlingEnabled();

        getMockBundleLinksTag().addResourcesToBundle(foundResources);

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("output to page should be empty",
                StringUtils.EMPTY, getStringWriter().getBuffer().toString());
    }

    @Test
    public void testBundlingDisabledByConfiguration() {

        setObjectUnderTest(new LinkTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(),
                getRequestedCssResourcePath(),
                createTagDependencies(),
                getMockBundleLinksTag()));


        recordBundlingDisabled();

        replay();

        final boolean result = getObjectUnderTest().bundlingEnabled();

        Assert.assertFalse("Bunding should not be enabled", result);
    }

    private LinkTagWriter createObjectUnderTest(
            final TestData testData) {
        return new LinkTagWriter(getMockDevice(),
                testData.getDynamicAttributes(),
                getRequestedCssResourcePath(),
                createTagDependencies(),
                null);
    }

    private LinkTagDependencies createTagDependencies() {
        return new LinkTagDependencies(
                getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getCssClientPathPrefix(),
                getMockResolutionWarnLogger(),
                null);
    }

    private void recordGetResource(
            final List<Resource> expectedResources) throws IOException {
        EasyMock.expect(
                getMockResourceResolverEngine()
                .getAllResources(getMockDevice(), getRequestedCssResourcePath()))
                .andReturn(expectedResources).atLeastOnce();
    }

    private Resource getMappedDefaultGroupCssResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupCssResourcePath();
    }

    private String getMappedDefaultGroupCssResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupCssResourceHref();
    }

    private Resource getMappediPhoneGroupCssResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupCssResourcePath();
    }

    private String getMappediPhoneGroupCssResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupCssResourceHref();
    }

    private String getRequestedCssResourcePath() {
        return getResourcePathTestData().getRequestedCssResourcePath();
    }

    private void recordBundlingEnabled() {
        EasyMock.expect(getMockBundleLinksTag().hasBundlingEnabled()).andStubReturn(true);
    }

    private void recordBundlingDisabled() {
        EasyMock.expect(getMockBundleLinksTag().hasBundlingEnabled()).andStubReturn(false);
    }

    /**
     * @return the objectUnderTest
     */
    public LinkTagWriter getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(
            final LinkTagWriter objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
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
     * @param stringWriter the stringWriter to set
     */
    private void setStringWriter(final StringWriter stringWriter) {
        this.stringWriter = stringWriter;
    }

    /**
     * @return the stringWriter
     */
    private StringWriter getStringWriter() {
        return stringWriter;
    }

    private TestData[] getTestData() {
        return new TestData [] {
                createTestDataNoDynamicAttributesSingleMappedResourceDevMode(),
                createTestDataOneDynamicAttributeSingleMappedResourceDevMode(),
                createTestDataTwoDynamicAttributesSingleMappedResourceDevMode(),

                createTestDataNoDynamicAttributesMultipleMappedResourcesDevMode(),
                createTestDataOneDynamicAttributeMultipleMappedResourcesDevMode(),
                createTestDataTwoDynamicAttributesMultipleMappedResourcesDevMode(),

                createTestDataNoDynamicAttributesNoMappedResourceDevMode(),
        };
    }

    private TestData createTestDataTwoDynamicAttributesMultipleMappedResourcesDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                        "<link href=\""
                        + getMappedDefaultGroupCssResourceHref()
                        + "\" rel=\"stylesheet\" type=\"text/css\" />"
                        + "<link href=\""
                        + getMappediPhoneGroupCssResourceHref()
                        + "\" rel=\"stylesheet\" type=\"text/css\" />");
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResourcesDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                        "<link href=\""
                        + getMappedDefaultGroupCssResourceHref()
                        + "\" rel=\"stylesheet\" />"
                        + "<link href=\""
                        + getMappediPhoneGroupCssResourceHref()
                        + "\" rel=\"stylesheet\" />");
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                        "<link href=\"" + getMappedDefaultGroupCssResourceHref()
                        + "\" " + "/>"
                        + "<link href=\"" + getMappediPhoneGroupCssResourceHref()
                        + "\" " + "/>");
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" type=\"text/css\" />");
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" />");
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                StringUtils.EMPTY);
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                "<link href=\"" + getMappedDefaultGroupCssResourceHref()
                + "\" " + "/>");
    }

    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final List<Resource> resources;
        private final String outputString;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final List<Resource> resources,
                final String outputString) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.resources = resources;
            this.outputString = outputString;
        }

        /**
         * @return the dynamicAttributes
         */
        private List<DynamicTagAttribute> getDynamicAttributes() {
            return dynamicAttributes;
        }

        /**
         * @return the resources
         */
        private List<Resource> getResources() {
            return resources;
        }

        /**
         * @return the outputString
         */
        private String getOutputString() {
            return outputString;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
            toStringBuilder.append("dynamicAttributes", getDynamicAttributes());
            toStringBuilder.append("resources", getResources());
            toStringBuilder.append("outputString", getOutputString());
            return toStringBuilder.toString();
        }



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
     * @return the mockBundleLinksTag
     */
    public BundleLinksTag getMockBundleLinksTag() {
        return mockBundleLinksTag;
    }

    /**
     * @param mockBundleLinksTag the mockBundleLinksTag to set
     */
    public void setMockBundleLinksTag(final BundleLinksTag mockBundleLinksTag) {
        this.mockBundleLinksTag = mockBundleLinksTag;
    }
}

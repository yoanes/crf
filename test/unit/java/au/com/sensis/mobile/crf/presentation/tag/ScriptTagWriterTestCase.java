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

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
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
 * Unit test {@link ScriptTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagWriterTestCase extends AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private static final String SCRIPT_NAME = "myScript";

    private static final String ABSOLUTE_HREF = "http://some.external.script.js";

    private ScriptTagWriter objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final DeploymentMetadataTestData deploymentMetadataTestData
    = new DeploymentMetadataTestData();
    private JspWriter mockJspWriter;
    private JspFragment mockJspFragment;
    private StringWriter stringWriter;

    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private FileIoFacade mockFileIoFacade;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private BundleScriptsTag mockBundleScriptsTag;

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

    private DynamicTagAttribute createIdDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "id",
        "myJavaScript");
    }

    private DynamicTagAttribute createTitleDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "title",
        "My Image");
    }

    private DynamicTagAttribute createTypeDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "type",
        "text/perl");
    }

    private DynamicTagAttribute createCharsetDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "charset", "latin1");
    }

    private void resetMocksAndTestData() {
        reset();
        setStringWriter(new StringWriter());
        setMockJspWriter(new MockJspWriter(getStringWriter()));
    }

    @Test
    public void testGetIdWhenSrcNotBlank() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(), getRequestedScriptResourcePath(), null,
                createTagDependencies(), null));

        Assert.assertEquals("id is wrong", getRequestedScriptResourcePath(), getObjectUnderTest()
                .getId());

    }

    @Test
    public void testGetIdWhenSrcBlank() throws Throwable {

        final String[] testValues = new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                    new ArrayList<DynamicTagAttribute>(), testValue, "myName",
                    createTagDependencies(), null));

            Assert.assertEquals("id is wrong", "myName", getObjectUnderTest().getId());
        }

    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndNoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(), null, SCRIPT_NAME, createTagDependencies(),
                null));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output",
                "<script charset=\"utf-8\" type=\"text/javascript\" ></script>", getStringWriter()
                .getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndOneDynamicAttribute() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays
                .asList(createTitleDynamicAttribute()), null, SCRIPT_NAME,
                createTagDependencies(), null));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output",
                "<script title=\"My Image\" charset=\"utf-8\" type=\"text/javascript\" ></script>",
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndTwoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createIdDynamicAttribute()), null, SCRIPT_NAME,
                createTagDependencies(), null));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output",
                "<script title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer()
                .toString());
    }

    @Test
    public void testWriteTagWhenHrefIsAbsoluteAndNoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(), ABSOLUTE_HREF, null,
                createTagDependencies(), null));

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script src=\"" + ABSOLUTE_HREF
                + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>", getStringWriter()
                .getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsAbsoluteAndOneDynamicAttribute() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays
                .asList(createTitleDynamicAttribute()), ABSOLUTE_HREF, null,
                createTagDependencies(), null));

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script src=\"" + ABSOLUTE_HREF
                + "\" title=\"My Image\" charset=\"utf-8\" type=\"text/javascript\" ></script>",
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsAbsoluteAndTwoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createIdDynamicAttribute()), ABSOLUTE_HREF, null,
                createTagDependencies(), null));

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script src=\"" + ABSOLUTE_HREF
                + "\" title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsAbsoluteAndTwoDynamicAttributesAndParentBundleScriptsTag()
    throws Throwable {

        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createIdDynamicAttribute()), ABSOLUTE_HREF, null,
                createTagDependencies(), getMockBundleScriptsTag()));

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script src=\"" + ABSOLUTE_HREF
                + "\" title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefNotBlankAndNotAbsolute() throws Throwable {
        final TestData[] testDataArray = getTestDataForHrefNotBlank();
        for (int i = 0; i < testDataArray.length; i++) {
            try {
                setObjectUnderTest(createObjectUnderTestWhenSrcNotBlank(testDataArray[i]));

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
    public void testWriteTagWhenHrefNotBlankAndNotAbsoluteAndParentBundleScriptsTag()
    throws Throwable {

        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createIdDynamicAttribute()),
                getRequestedScriptResourcePath(), null, createTagDependencies(),
                getMockBundleScriptsTag()));

        final List<Resource> foundResources = Arrays
        .asList(getMappedDefaultGroupScriptResourcePath(),
                getMappediPhoneGroupScriptResourcePath());
        recordGetResource(foundResources);

        recordBundlingEnabled();

        getMockBundleScriptsTag().addResourcesToBundle(foundResources);

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("output to page should be empty",
                StringUtils.EMPTY, getStringWriter().getBuffer().toString());
    }

    @Test
    public void testBundlingDisabledByConfiguration() {

        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createIdDynamicAttribute()),
                getRequestedScriptResourcePath(), null, createTagDependencies(),
                getMockBundleScriptsTag()));


        recordBundlingDisabled();

        replay();

        final boolean result = getObjectUnderTest().bundlingEnabled();

        Assert.assertFalse("Bunding should not be enabled", result);
    }

    private ScriptTagWriter createObjectUnderTestWhenSrcNotBlank(
            final TestData testData) {
        return new ScriptTagWriter(getMockDevice(),
                testData.getDynamicAttributes(),
                getRequestedScriptResourcePath(), null,
                createTagDependencies(testData), null);
    }

    private ScriptTagDependencies createTagDependencies(
            final TestData testData) {
        return new ScriptTagDependencies(getMockResourceResolverEngine(),
                testData.getDeploymentMetadata(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger(), null);
    }

    private ScriptTagDependencies createTagDependencies() {
        return new ScriptTagDependencies(getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createProdDeploymentMetadata(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger(), null);
    }

    private void recordGetResource(
            final List<Resource> expectedResources) throws IOException {
        EasyMock.expect(
                getMockResourceResolverEngine()
                .getAllResources(getMockDevice(),
                        getRequestedScriptResourcePath()))
                        .andReturn(expectedResources).atLeastOnce();
    }

    private Resource getMappedDefaultGroupScriptResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath();
    }

    private String getMappedDefaultGroupScriptResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourceHref();
    }

    private Resource getMappediPhoneGroupScriptResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptResource();
    }

    private String getMappediPhoneGroupScriptResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptResourceHref();
    }

    private String getRequestedScriptResourcePath() {
        return getResourcePathTestData().getRequestedNamedScriptResourcePath();
    }

    private void recordBundlingEnabled() {
        EasyMock.expect(getMockBundleScriptsTag().hasBundlingEnabled()).andStubReturn(true);
    }

    private void recordBundlingDisabled() {
        EasyMock.expect(getMockBundleScriptsTag().hasBundlingEnabled()).andStubReturn(false);
    }

    /**
     * @return the objectUnderTest
     */
    public ScriptTagWriter getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(
            final ScriptTagWriter objectUnderTest) {
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

    private TestData[] getTestDataForHrefNotBlank() {
        return new TestData [] {
                createTestDataNoDynamicAttributesSingleMappedResource(),
                createTestDataOneDynamicAttributeSingleMappedResource(),
                createTestDataTwoDynamicAttributesSingleMappedResource(),

                createTestDataNoDynamicAttributesMultipleMappedResources(),
                createTestDataOneDynamicAttributeMultipleMappedResources(),
                createTestDataTwoDynamicAttributesMultipleMappedResources(),

                createTestDataNoDynamicAttributesNoMappedResource(),

                createTestDataCharsetAndTypeDynamicAttributesMultipleMappedResources(),
                createTestDataCharsetAndTypeDynamicAttributesSingleMappedResources()

        };
    }

    private TestData createTestDataTwoDynamicAttributesMultipleMappedResources() {
        return new TestData(Arrays.asList(createTitleDynamicAttribute(), createIdDynamicAttribute()), Arrays
                .asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()), "<script src=\""
                        + getMappedDefaultGroupScriptResourceHref()
                        + "\" title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                        + "type=\"text/javascript\" ></script>" + "<script src=\""
                        + getMappediPhoneGroupScriptResourceHref()
                        + "\" title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                        + "type=\"text/javascript\" ></script>", getDeploymentMetadataTestData()
                        .createDevDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResources() {
        return new TestData(Arrays.asList(createTitleDynamicAttribute()), Arrays
                .asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()), "<script src=\""
                        + getMappedDefaultGroupScriptResourceHref()
                        + "\" title=\"My Image\" charset=\"utf-8\" "
                        + "type=\"text/javascript\" ></script>" + "<script src=\""
                        + getMappediPhoneGroupScriptResourceHref()
                        + "\" title=\"My Image\" charset=\"utf-8\" "
                        + "type=\"text/javascript\" ></script>", getDeploymentMetadataTestData()
                        .createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResources() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(), Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                        "<script src=\"" + getMappedDefaultGroupScriptResourceHref()
                        + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>"
                        + "<script src=\"" + getMappediPhoneGroupScriptResourceHref()
                        + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>",
                        getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResource() {
        return new TestData(Arrays
                .asList(createTitleDynamicAttribute(), createIdDynamicAttribute()), Arrays
                .asList(getMappedDefaultGroupScriptResourcePath()), "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" id=\"myJavaScript\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getDeploymentMetadataTestData()
                .createDevDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResource() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" charset=\"utf-8\" type=\"text/javascript\" ></script>",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResource() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                StringUtils.EMPTY,
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResource() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                "<script src=\"" + getMappedDefaultGroupScriptResourceHref()
                + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataCharsetAndTypeDynamicAttributesMultipleMappedResources() {
        return new TestData(Arrays.asList(createCharsetDynamicAttribute(),
                createTypeDynamicAttribute()), Arrays
                .asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()), "<script src=\""
                        + getMappedDefaultGroupScriptResourceHref()
                        + "\" charset=\"latin1\" type=\"text/perl\" ></script>" + "<script src=\""
                        + getMappediPhoneGroupScriptResourceHref()
                        + "\" charset=\"latin1\" type=\"text/perl\" ></script>",
                        getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataCharsetAndTypeDynamicAttributesSingleMappedResources() {
        return new TestData(Arrays.asList(createCharsetDynamicAttribute(),
                createTypeDynamicAttribute()), Arrays
                .asList(getMappedDefaultGroupScriptResourcePath()), "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" charset=\"latin1\" type=\"text/perl\" ></script>",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final List<Resource> resources;
        private final String outputString;
        private final DeploymentMetadata deploymentMetadata;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final List<Resource> resources,
                final String outputString,
                final DeploymentMetadata deploymentMetadata) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.resources = resources;
            this.outputString = outputString;
            this.deploymentMetadata = deploymentMetadata;
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
         * @return the deploymentMetadata
         */
        public DeploymentMetadata getDeploymentMetadata() {
            return deploymentMetadata;
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
            toStringBuilder.append("deploymentMetadata", getDeploymentMetadata());
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
     * @return the mockBundleScriptsTag
     */
    public BundleScriptsTag getMockBundleScriptsTag() {
        return mockBundleScriptsTag;
    }

    /**
     * @param mockBundleScriptsTag the mockBundleScriptsTag to set
     */
    public void setMockBundleScriptsTag(final BundleScriptsTag mockBundleScriptsTag) {
        this.mockBundleScriptsTag = mockBundleScriptsTag;
    }
}

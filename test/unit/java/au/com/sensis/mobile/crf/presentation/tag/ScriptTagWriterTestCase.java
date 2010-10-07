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
import au.com.sensis.mobile.crf.service.ScriptBundleFactory;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ScriptTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagWriterTestCase extends
        AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private static final String SCRIPT_NAME = "myScript";

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
    private ScriptBundleFactory mockScriptBundleFactory;
    private FileIoFacade mockFileIoFacade;
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
                "text/javascript");
    }

    private DynamicTagAttribute createTitleDynamicAttribute() {
        return new DynamicTagAttribute(DYN_ATTR_URI, "title",
                "My Image");
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
                createTagDependencies()));

        Assert.assertEquals("id is wrong", getRequestedScriptResourcePath(), getObjectUnderTest()
                .getId());

    }

    @Test
    public void testGetIdWhenSrcBlank() throws Throwable {

        final String[] testValues = new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                    new ArrayList<DynamicTagAttribute>(), testValue, "myName",
                    createTagDependencies()));

            Assert.assertEquals("id is wrong", "myName", getObjectUnderTest().getId());
        }

    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndNoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(),
                new ArrayList<DynamicTagAttribute>(), null, SCRIPT_NAME, createTagDependencies()));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script ></script>\n", getStringWriter()
                .getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndOneDynamicAttribute() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays
                .asList(createTitleDynamicAttribute()), null, SCRIPT_NAME,
                createTagDependencies()));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output", "<script title=\"My Image\" ></script>\n",
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTagWhenHrefIsBlankAndTwoDynamicAttributes() throws Throwable {
        setObjectUnderTest(new ScriptTagWriter(getMockDevice(), Arrays.asList(
                createTitleDynamicAttribute(), createTypeDynamicAttribute()), null, SCRIPT_NAME,
                createTagDependencies()));

        getMockJspFragment().invoke(getMockJspWriter());

        replay();

        getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

        Assert.assertEquals("incorrect output",
                "<script title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testWriteTag() throws Throwable {
        final TestData[] testDataArray = getTestDataForHrefNotBlank();
        for (int i = 0; i < testDataArray.length; i++) {
            try {
                setObjectUnderTest(createObjectUnderTestWhenSrcNotBlank(testDataArray[i]));

                recordGetResource(testDataArray[i].getResources());

                if (testDataArray[i].getDeploymentMetadata().isDevPlatform()
                        && StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                    recordLogResourceNotFoundWarning();
                }

                if (testDataArray[i].getDeploymentMetadata().isProdPlatform()) {
                    if (StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                        recordLogResourceNotFoundWarning();
                    } else {
                        EasyMock.expect(getMockScriptBundleFactory().getBundle(
                                testDataArray[i].getResources())).andReturn(
                                        testDataArray[i].getBundleResource())
                                .atLeastOnce();
                    }
                }

                replay();

                getObjectUnderTest().writeTag(getMockJspWriter(), getMockJspFragment());

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

    private ScriptTagWriter createObjectUnderTestWhenSrcNotBlank(
            final TestData testData) {
        return new ScriptTagWriter(getMockDevice(),
                testData.getDynamicAttributes(),
                getRequestedScriptResourcePath(), null,
                createTagDependencies(testData));
    }

    private ScriptTagDependencies createTagDependencies(
            final TestData testData) {
        return new ScriptTagDependencies(getMockResourceResolverEngine(),
                testData.getDeploymentMetadata(), getMockScriptBundleFactory(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private ScriptTagDependencies createTagDependencies() {
        return new ScriptTagDependencies(getMockResourceResolverEngine(),
                getDeploymentMetadataTestData().createProdDeploymentMetadata(),
                getMockScriptBundleFactory(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetResource(
            final List<Resource> expectedResources) throws IOException {
        EasyMock.expect(
                getMockResourceResolverEngine()
                        .getAllResources(getMockDevice(),
                                getRequestedScriptResourcePath()))
                        .andReturn(expectedResources).atLeastOnce();
    }

    private void recordLogResourceNotFoundWarning() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled())
            .andReturn(Boolean.TRUE);
        getMockResolutionWarnLogger().warn(
                "No resource was found for requested resource '"
                        + getResourcePathTestData().getRequestedNamedScriptResourcePath()
                        + "' and device " + getMockDevice());
    }

    private Resource getMappedDefaultGroupScriptResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath();
    }

    private String getMappedDefaultGroupScriptResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourceHref();
    }

    private Resource getMappediPhoneGroupScriptResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptResourcePath();
    }

    private String getMappediPhoneGroupScriptResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptResourceHref();
    }

    private String getRequestedScriptResourcePath() {
        return getResourcePathTestData().getRequestedNamedScriptResourcePath();
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
            createTestDataNoDynamicAttributesSingleMappedResourceDevMode(),
            createTestDataOneDynamicAttributeSingleMappedResourceDevMode(),
            createTestDataTwoDynamicAttributesSingleMappedResourceDevMode(),

            createTestDataNoDynamicAttributesMultipleMappedResourcesDevMode(),
            createTestDataOneDynamicAttributeMultipleMappedResourcesDevMode(),
            createTestDataTwoDynamicAttributesMultipleMappedResourcesDevMode(),

            createTestDataNoDynamicAttributesNoMappedResourceDevMode(),

            createTestDataNoDynamicAttributesSingleMappedResourceProdMode(),
            createTestDataOneDynamicAttributeSingleMappedResourceProdMode(),
            createTestDataTwoDynamicAttributesSingleMappedResourceProdMode(),

            createTestDataNoDynamicAttributesMultipleMappedResourcesProdMode(),
            createTestDataOneDynamicAttributeMultipleMappedResourcesProdMode(),
            createTestDataTwoDynamicAttributesMultipleMappedResourcesProdMode(),

            createTestDataNoDynamicAttributesNoMappedResourceProdMode(),
        };
    }

    private TestData createTestDataTwoDynamicAttributesMultipleMappedResourcesDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n"
                + "<script src=\""
                + getMappediPhoneGroupScriptResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataTwoDynamicAttributesMultipleMappedResourcesProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                getMappedIphoneGroupScriptBundleResourcePath(),
                        "<script src=\""
                        + getMappedIphoneGroupScriptBundleResourceHref()
                        + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                        getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResourcesDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" ></script>\n"
                + "<script src=\""
                + getMappediPhoneGroupScriptResourceHref()
                + "\" title=\"My Image\" ></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResourcesProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                getMappedIphoneGroupScriptBundleResourcePath(),
                        "<script src=\""
                        + getMappedIphoneGroupScriptBundleResourceHref()
                        + "\" title=\"My Image\" ></script>\n",
                        getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                null,
                "<script src=\"" + getMappedDefaultGroupScriptResourceHref()
                + "\" " + "></script>\n"
                + "<script src=\"" + getMappediPhoneGroupScriptResourceHref()
                    + "\" " + "></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                getMappedIphoneGroupScriptBundleResourcePath(),
                "<script src=\"" + getMappedIphoneGroupScriptBundleResourceHref()
                + "\" " + "></script>\n",
                getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\""
                + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\""
                + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" title=\"My Image\" ></script>\n",
                getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" ></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\"" + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" " + "></script>\n",
                getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                null,
                StringUtils.EMPTY,
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                null,
                StringUtils.EMPTY,
                getDeploymentMetadataTestData().createProdDeploymentMetadata());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\"" + getMappedDefaultGroupScriptResourceHref()
                    + "\" " + "></script>\n",
                getDeploymentMetadataTestData().createDevDeploymentMetadata());
    }

    private Resource getMappedDefaultGroupScriptBundleResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptBundleResourcePath();
    }

    private String getMappedDefaultGroupScriptBundleResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptBundleResourceHref();
    }

    private Resource getMappedIphoneGroupScriptBundleResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptBundleResourcePath();
    }

    private String getMappedIphoneGroupScriptBundleResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptBundleResourceHref();
    }


    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final List<Resource> resources;
        private final Resource bundleResource;
        private final String outputString;
        private final DeploymentMetadata deploymentMetadata;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final List<Resource> resources,
                final Resource bundlePath,
                final String outputString,
                final DeploymentMetadata deploymentMetadata) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.resources = resources;
            bundleResource = bundlePath;
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
         * @return the bundleResource
         */
        public Resource getBundleResource() {
            return bundleResource;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
            toStringBuilder.append("dynamicAttributes", getDynamicAttributes());
            toStringBuilder.append("resources", getResources());
            toStringBuilder.append("bundleResource", getBundleResource());
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
}

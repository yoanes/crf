package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.config.DeploymentVersionTestData;
import au.com.sensis.mobile.crf.service.CssBundleFactory;
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
    private final DeploymentVersionTestData deploymentVersionTestData
        = new DeploymentVersionTestData();
    private JspWriter mockJspWriter;
    private StringWriter stringWriter;

    private WebApplicationContext mockWebApplicationContext;
    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;
    private CssBundleFactory mockCssBundleFactory;
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

                if (testDataArray[i].getDeploymentVersion().isDevPlatform()
                        && StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                    recordLogResourceNotFoundWarning();
                }

                if (testDataArray[i].getDeploymentVersion().isProdPlatform()) {
                    if (StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                        recordLogResourceNotFoundWarning();
                    } else {
                        EasyMock.expect(getMockCssBundleFactory().getBundle(
                              testDataArray[i].getResources())).andReturn(
                                      testDataArray[i].getBundleResource()).atLeastOnce();
                    }
                }

                replay();

                getObjectUnderTest().writeTag(getMockJspWriter());

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

    private LinkTagWriter createObjectUnderTest(
            final TestData testData) {
        return new LinkTagWriter(getMockDevice(),
                testData.getDynamicAttributes(),
                getRequestedCssResourcePath(),
                createTagDependencies(testData));
    }

    private LinkTagDependencies createTagDependencies(
            final TestData testData) {
        return new LinkTagDependencies(
                getMockResourceResolverEngine(),
                testData.getDeploymentVersion(),
                getMockCssBundleFactory(),
                getResourcePathTestData().getCssClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetResource(
            final List<Resource> expectedResources) throws IOException {
        EasyMock.expect(
                getMockResourceResolverEngine()
                        .getAllResources(getMockDevice(), getRequestedCssResourcePath()))
                        .andReturn(expectedResources).atLeastOnce();
    }

    private void recordLogResourceNotFoundWarning() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled())
            .andReturn(Boolean.TRUE);
        getMockResolutionWarnLogger().warn(
                "No resource was found for requested resource '"
                        + getResourcePathTestData().getRequestedCssResourcePath()
                        + "' and device " + getMockDevice());
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
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    /**
     * @return the deploymentVersionTestData
     */
    private DeploymentVersionTestData getDeploymentVersionTestData() {
        return deploymentVersionTestData;
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
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                null,
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" type=\"text/css\" />\n"
                + "<link href=\""
                + getMappediPhoneGroupCssResourceHref()
                + "\" rel=\"stylesheet\" type=\"text/css\" />\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataTwoDynamicAttributesMultipleMappedResourcesProdMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                getMappedIphoneGroupCssBundleResourcePath(),
                        "<link href=\""
                        + getMappedIphoneGroupCssBundleResourceHref()
                        + "\" rel=\"stylesheet\" type=\"text/css\" />\n",
                        getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResourcesDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                null,
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" />\n"
                + "<link href=\""
                + getMappediPhoneGroupCssResourceHref()
                + "\" rel=\"stylesheet\" />\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeMultipleMappedResourcesProdMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                getMappedIphoneGroupCssBundleResourcePath(),
                        "<link href=\""
                        + getMappedIphoneGroupCssBundleResourceHref()
                        + "\" rel=\"stylesheet\" />\n",
                        getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                null,
                "<link href=\"" + getMappedDefaultGroupCssResourceHref()
                + "\" " + "/>\n"
                + "<link href=\"" + getMappediPhoneGroupCssResourceHref()
                    + "\" " + "/>\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath(),
                        getMappediPhoneGroupCssResourcePath()),
                getMappedIphoneGroupCssBundleResourcePath(),
                "<link href=\"" + getMappedIphoneGroupCssBundleResourceHref()
                + "\" " + "/>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                null,
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" type=\"text/css\" />\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                getMappedDefaultGroupCssBundleResourcePath(),
                "<link href=\""
                + getMappedDefaultGroupCssBundleResourceHref()
                + "\" rel=\"stylesheet\" type=\"text/css\" />\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                getMappedDefaultGroupCssBundleResourcePath(),
                "<link href=\""
                + getMappedDefaultGroupCssBundleResourceHref()
                + "\" rel=\"stylesheet\" />\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createRelDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                null,
                "<link href=\""
                + getMappedDefaultGroupCssResourceHref()
                + "\" rel=\"stylesheet\" />\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                getMappedDefaultGroupCssBundleResourcePath(),
                "<link href=\"" + getMappedDefaultGroupCssBundleResourceHref()
                + "\" " + "/>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                null,
                StringUtils.EMPTY,
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<Resource>(),
                null,
                StringUtils.EMPTY,
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupCssResourcePath()),
                null,
                "<link href=\"" + getMappedDefaultGroupCssResourceHref()
                    + "\" " + "/>\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private Resource getMappedDefaultGroupCssBundleResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupCssBundleResourcePath();
    }

    private String getMappedDefaultGroupCssBundleResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupCssBundleResourceHref();
    }

    private Resource getMappedIphoneGroupCssBundleResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupCssBundleResourcePath();
    }

    private String getMappedIphoneGroupCssBundleResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupCssBundleResourceHref();
    }


    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final List<Resource> resources;
        private final Resource bundleResource;
        private final String outputString;
        private final DeploymentVersion deploymentVersion;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final List<Resource> resources,
                final Resource bundlePath,
                final String outputString,
                final DeploymentVersion deploymentVersion) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.resources = resources;
            bundleResource = bundlePath;
            this.outputString = outputString;
            this.deploymentVersion = deploymentVersion;
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
         * @return the deploymentVersion
         */
        public DeploymentVersion getDeploymentVersion() {
            return deploymentVersion;
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
            toStringBuilder.append("deploymentVersion", getDeploymentVersion());
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

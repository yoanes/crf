package au.com.sensis.mobile.crf.presentation.tag;

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
import au.com.sensis.mobile.crf.service.FileIoFacade;
import au.com.sensis.mobile.crf.service.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceSelector;
import au.com.sensis.mobile.crf.service.ScriptBundleFactory;
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceSelectorScriptTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorScriptTagWriterTestCase extends
        AbstractJUnit4TestCase {

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private ResourceSelectorScriptTagWriter objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final DeploymentVersionTestData deploymentVersionTestData
        = new DeploymentVersionTestData();
    private JspWriter mockJspWriter;
    private StringWriter stringWriter;

    private WebApplicationContext mockWebApplicationContext;
    private ResourceSelector mockResourceSelector;
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
    public void testDoTag() throws Throwable {
        final TestData[] testDataArray = getTestData();
        for (int i = 0; i < testDataArray.length; i++) {
            try {
                setObjectUnderTest(createObjectUnderTest(testDataArray[i]));

                recordGetMappedResourcePath(testDataArray[i].getMappedResourcePaths());

                if (testDataArray[i].getDeploymentVersion().isDevPlatform()
                        && StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                    recordLogResourceNotFoundWarning();
                }

                if (testDataArray[i].getDeploymentVersion().isProdPlatform()) {
                    if (StringUtils.isEmpty(testDataArray[i].getOutputString())) {
                        recordLogResourceNotFoundWarning();
                    } else {
                        EasyMock.expect(getMockScriptBundleFactory().getBundle(
                                testDataArray[i].getMappedResourcePaths())).andReturn(
                                        testDataArray[i].getBundleMappedResourcePath())
                                .atLeastOnce();
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

    private ResourceSelectorScriptTagWriter createObjectUnderTest(
            final TestData testData) {
        return new ResourceSelectorScriptTagWriter(getMockDevice(),
                testData.getDynamicAttributes(),
                getRequestedScriptResourcePath(),
                createCollaboratorsMemento(testData));
    }

    private ScriptTagCollaboratorsMemento createCollaboratorsMemento(
            final TestData testData) {
        return new ScriptTagCollaboratorsMemento(getMockResourceSelector(),
                testData.getDeploymentVersion(), getMockScriptBundleFactory(),
                getResourcePathTestData().getScriptClientPathPrefix(),
                getMockResolutionWarnLogger());
    }

    private void recordGetMappedResourcePath(
            final List<MappedResourcePath> expectedMappedResourcePaths) {
        EasyMock.expect(
                getMockResourceSelector()
                        .getAllResourcePaths(getMockDevice(),
                                getRequestedScriptResourcePath()))
                        .andReturn(expectedMappedResourcePaths).atLeastOnce();
    }

    private void recordLogResourceNotFoundWarning() {
        EasyMock.expect(getMockResolutionWarnLogger().isWarnEnabled())
            .andReturn(Boolean.TRUE);
        getMockResolutionWarnLogger().warn(
                "No resource was found for requested resource '"
                        + getResourcePathTestData().getRequestedNamedScriptResourcePath()
                        + "' and device " + getMockDevice());
    }

    private MappedResourcePath getMappedDefaultGroupScriptResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourcePath();
    }

    private String getMappedDefaultGroupScriptResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptResourceHref();
    }

    private MappedResourcePath getMappediPhoneGroupScriptResourcePath() {
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
    public ResourceSelectorScriptTagWriter getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(
            final ResourceSelectorScriptTagWriter objectUnderTest) {
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
     * @return the mockResourceSelector
     */
    public ResourceSelector
        getMockResourceSelector() {
        return mockResourceSelector;
    }

    /**
     * @param mockResourceSelector
     *            the mockResourceSelector to set
     */
    public void setMockResourceSelector(
            final ResourceSelector mockResourceSelector) {
        this.mockResourceSelector = mockResourceSelector;
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
                getDeploymentVersionTestData().createDevDeploymentVersion());
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
                        getDeploymentVersionTestData().createProdDeploymentVersion());
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
                getDeploymentVersionTestData().createDevDeploymentVersion());
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
                        getDeploymentVersionTestData().createProdDeploymentVersion());
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
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesMultipleMappedResourcesProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath(),
                        getMappediPhoneGroupScriptResourcePath()),
                getMappedIphoneGroupScriptBundleResourcePath(),
                "<script src=\"" + getMappedIphoneGroupScriptBundleResourceHref()
                + "\" " + "></script>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataTwoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute(), createTypeDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\""
                + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" title=\"My Image\" type=\"text/javascript\" ></script>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceProdMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\""
                + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" title=\"My Image\" ></script>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataOneDynamicAttributeSingleMappedResourceDevMode() {
        return new TestData(
                Arrays.asList(createTitleDynamicAttribute()),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\""
                + getMappedDefaultGroupScriptResourceHref()
                + "\" title=\"My Image\" ></script>\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                getMappedDefaultGroupScriptBundleResourcePath(),
                "<script src=\"" + getMappedDefaultGroupScriptBundleResourceHref()
                + "\" " + "></script>\n",
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<MappedResourcePath>(),
                null,
                StringUtils.EMPTY,
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesNoMappedResourceProdMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                new ArrayList<MappedResourcePath>(),
                null,
                StringUtils.EMPTY,
                getDeploymentVersionTestData().createProdDeploymentVersion());
    }

    private TestData createTestDataNoDynamicAttributesSingleMappedResourceDevMode() {
        return new TestData(
                new ArrayList<DynamicTagAttribute>(),
                Arrays.asList(getMappedDefaultGroupScriptResourcePath()),
                null,
                "<script src=\"" + getMappedDefaultGroupScriptResourceHref()
                    + "\" " + "></script>\n",
                getDeploymentVersionTestData().createDevDeploymentVersion());
    }

    private MappedResourcePath getMappedDefaultGroupScriptBundleResourcePath() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptBundleResourcePath();
    }

    private String getMappedDefaultGroupScriptBundleResourceHref() {
        return getResourcePathTestData().getMappedDefaultGroupNamedScriptBundleResourceHref();
    }

    private MappedResourcePath getMappedIphoneGroupScriptBundleResourcePath() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptBundleResourcePath();
    }

    private String getMappedIphoneGroupScriptBundleResourceHref() {
        return getResourcePathTestData().getMappedIphoneGroupNamedScriptBundleResourceHref();
    }


    private static class TestData {
        private final List<DynamicTagAttribute> dynamicAttributes;
        private final List<MappedResourcePath> mappedResourcePaths;
        private final MappedResourcePath bundleMappedResourcePath;
        private final String outputString;
        private final DeploymentVersion deploymentVersion;

        public TestData(final List<DynamicTagAttribute> dynamicAttributes,
                final List<MappedResourcePath> mappedResourcePaths,
                final MappedResourcePath bundlePath,
                final String outputString,
                final DeploymentVersion deploymentVersion) {
            super();
            this.dynamicAttributes = dynamicAttributes;
            this.mappedResourcePaths = mappedResourcePaths;
            bundleMappedResourcePath = bundlePath;
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
         * @return the mappedResourcePaths
         */
        private List<MappedResourcePath> getMappedResourcePaths() {
            return mappedResourcePaths;
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
         * @return the bundleMappedResourcePath
         */
        public MappedResourcePath getBundleMappedResourcePath() {
            return bundleMappedResourcePath;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
            toStringBuilder.append("dynamicAttributes", getDynamicAttributes());
            toStringBuilder.append("mappedResourcePaths", getMappedResourcePaths());
            toStringBuilder.append("bundleMappedResourcePath", getBundleMappedResourcePath());
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

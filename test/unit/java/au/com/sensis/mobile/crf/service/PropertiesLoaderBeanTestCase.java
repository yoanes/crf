package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link PropertiesLoaderBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesLoaderBeanTestCase extends AbstractJUnit4TestCase {

    private static final String REQUESTED_PROPERTIES_PATH = "comp/jazz/config.properties";
    private File propertiesFile1;
    private File propertiesFile2;
    private Resource mockResource1;
    private Resource mockResource2;

    private PropertiesLoaderBean objectUnderTest;

    private ResourceResolverEngine mockResourceResolverEngine;
    private Device mockDevice;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new PropertiesLoaderBean(getMockResourceResolverEngine()));

        setPropertiesFile1(new File(this.getClass().getResource(
            "/au/com/sensis/mobile/crf/service/propertiesLoaderBeanTestData/"
                + "propertiesFile1.properties").toURI()));
        setPropertiesFile2(new File(this.getClass().getResource(
            "/au/com/sensis/mobile/crf/service/propertiesLoaderBeanTestData/"
                + "propertiesFile2.properties").toURI()));
    }

    @Test
    public void testConstructorWithNullResourceResolverEngine() throws Throwable {
        try {
            new PropertiesLoaderBean(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolverEngine must not be null", e.getMessage());
        }

    }

    @Test
    public void testLoadPropertiesWhenOnlyOneFileFound() throws Throwable {

        EasyMock.expect(
                getMockResourceResolverEngine().getAllResources(getMockDevice(),
                        REQUESTED_PROPERTIES_PATH)).andReturn(Arrays.asList(getMockResource1()));

        EasyMock.expect(getMockResource1().getNewFile()).andReturn(getPropertiesFile1());

        replay();

        final Properties actualProperties =
                getObjectUnderTest().loadProperties(getMockDevice(), REQUESTED_PROPERTIES_PATH);

        Assert.assertNotNull("actualProperties should not be null", actualProperties);
        Assert.assertEquals("actualProperties does not contain propertyValueA", "propertyValueA",
                actualProperties.getProperty("propertyNameA"));
    }

    @Test
    public void testLoadPropertiesWhenMultipleFilesFound() throws Throwable {

        EasyMock.expect(
                getMockResourceResolverEngine().getAllResources(getMockDevice(),
                        REQUESTED_PROPERTIES_PATH)).andReturn(
                Arrays.asList(getMockResource1(), getMockResource2()));

        EasyMock.expect(getMockResource1().getNewFile()).andReturn(getPropertiesFile1());
        EasyMock.expect(getMockResource2().getNewFile()).andReturn(getPropertiesFile2());

        replay();

        final Properties actualProperties =
                getObjectUnderTest().loadProperties(getMockDevice(), REQUESTED_PROPERTIES_PATH);

        Assert.assertNotNull("actualProperties should not be null", actualProperties);
        Assert.assertEquals("actualProperties does not contain overridden propertyValueA",
                "overriddenPropertyValueA", actualProperties.getProperty("propertyNameA"));
        Assert.assertEquals("actualProperties does not contain propertyValueA", "propertyValueB",
                actualProperties.getProperty("propertyNameB"));
    }

    @Test
    public void testLoadPropertiesWhenIOExceptionHandled() throws Throwable {

        EasyMock.expect(
                getMockResourceResolverEngine().getAllResources(getMockDevice(),
                        REQUESTED_PROPERTIES_PATH)).andReturn(Arrays.asList(getMockResource1()));

        final File nonExistantFile = new File("i don't exist");
        EasyMock.expect(getMockResource1().getNewFile()).andReturn(nonExistantFile).atLeastOnce();

        replay();

        try {
            getObjectUnderTest().loadProperties(getMockDevice(), REQUESTED_PROPERTIES_PATH);

            Assert.fail("ResourceResolutionRuntimeException expected");
        } catch (final ResourceResolutionRuntimeException e) {
            Assert.assertEquals("ResourceResolutionRuntimeException has wrong message",
                    "Error loading properties file: '" + nonExistantFile.getPath() + "'", e
                            .getMessage());
        }
    }

    /**
     * @return the objectUnderTest
     */
    private PropertiesLoaderBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final PropertiesLoaderBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
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
     * @return the propertiesFile1
     */
    private File getPropertiesFile1() {
        return propertiesFile1;
    }

    /**
     * @param propertiesFile1 the propertiesFile1 to set
     */
    private void setPropertiesFile1(final File propertiesFile1) {
        this.propertiesFile1 = propertiesFile1;
    }

    /**
     * @return the propertiesFile2
     */
    private File getPropertiesFile2() {
        return propertiesFile2;
    }

    /**
     * @param propertiesFile2 the propertiesFile2 to set
     */
    private void setPropertiesFile2(final File propertiesFile2) {
        this.propertiesFile2 = propertiesFile2;
    }

    /**
     * @return the mockResource1
     */
    public Resource getMockResource1() {
        return mockResource1;
    }

    /**
     * @param mockResource1 the mockResource1 to set
     */
    public void setMockResource1(final Resource mockResource1) {
        this.mockResource1 = mockResource1;
    }

    /**
     * @return the mockResource2
     */
    public Resource getMockResource2() {
        return mockResource2;
    }

    /**
     * @param mockResource2 the mockResource2 to set
     */
    public void setMockResource2(final Resource mockResource2) {
        this.mockResource2 = mockResource2;
    }
}

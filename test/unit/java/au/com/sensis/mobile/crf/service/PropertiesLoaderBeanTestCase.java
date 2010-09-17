package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link PropertiesLoaderBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesLoaderBeanTestCase extends AbstractJUnit4TestCase {

    private static final String BUNDLES_PROPERTIES_FILENAME = "bundles.properties";
    private PropertiesLoaderBean objectUnderTest;
    private File testDataDir;


    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new PropertiesLoaderBean());

        setTestDataDir(new File(this.getClass().getResource(
            "/au/com/sensis/mobile/crf/service/propertiesLoaderBeanTestData").toURI()));
    }

    @Test
    public void testLoadPropertiesWhenFileFound() throws Throwable {
        final Properties properties =
            getObjectUnderTest().loadPropertiesNotNull(
                    new File(getTestDataDir(), BUNDLES_PROPERTIES_FILENAME));

        Assert.assertNotNull("properties should not be null", properties);
        Assert.assertEquals("order property is wrong", "*.js", properties.getProperty("order"));
    }

    @Test
    public void testLoadPropertiesWhenFileNotFound() throws Throwable {
        final Properties properties =
            getObjectUnderTest().loadPropertiesNotNull(
                    new File(getTestDataDir(), "wrong file name"));

        Assert.assertNotNull("properties should not be null", properties);
        Assert.assertNull("order property is wrong", properties.getProperty("order"));
    }

    private PropertiesLoaderBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final PropertiesLoaderBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private File getTestDataDir() {
        return testDataDir;
    }

    private void setTestDataDir(final File testDataDir) {
        this.testDataDir = testDataDir;
    }
}

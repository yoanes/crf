package au.com.sensis.mobile.crf.util;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.config.Groups;
import au.com.sensis.mobile.crf.config.UiConfiguration;
import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link CastorXmlBinderBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CastorXmlBinderBeanTestCase extends AbstractJUnit4TestCase {

    private static final String CRF_CONFIG_CLASSPATH
        = "/au/com/sensis/mobile/crf/util/xmlBinderTestData/crf-config.xml";

    private static final String INVALID_CRF_CONFIG_CLASSPATH
        = "/au/com/sensis/mobile/crf/util/xmlBinderTestData/crf-config-invalid.xml";

    private CastorXmlBinderBean objectUnderTest;
    private final GroupTestData groupTestData = new GroupTestData();
    private URL configUrl;
    private URL invalidConfigUrl;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new CastorXmlBinderBean());
        setConfigUrl(getClass().getResource(CRF_CONFIG_CLASSPATH));
        setInvalidConfigUrl(getClass().getResource(INVALID_CRF_CONFIG_CLASSPATH));
    }

    @Test
    public void testConfigurationLoadedSuccessfully() throws Throwable {
        final UiConfiguration expectedUiConfiguration = createExpectedUiConfiguration();

        assertComplexObjectsEqual("uiConfiguration is wrong", expectedUiConfiguration,
                getObjectUnderTest().unmarshall(getConfigUrl()));
    }

    @Test
    public void testConfigurationLoadedUnsuccessfully() throws Throwable {
        try {
            getObjectUnderTest().unmarshall(getInvalidConfigUrl());

            Assert.fail("XmlBinderRuntimeException expected");
        } catch (final XmlBinderRuntimeException e) {

            Assert.assertEquals("XmlBinderRuntimeException has wrong message",
                    "Error loading XML from URL: '" + getInvalidConfigUrl() + "'", e.getMessage());

            Assert.assertNotNull("XmlBinderRuntimeException should have a cause", e.getCause());
        }
    }

    private UiConfiguration createExpectedUiConfiguration() {
        final UiConfiguration uiConfiguration = new UiConfiguration();

        final Groups groups = new Groups();
        groups.setDefaultGroup(getGroupTestData().createDefaultGroup());

        uiConfiguration.setGroups(groups);

        return uiConfiguration;
    }

    private CastorXmlBinderBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final CastorXmlBinderBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private URL getConfigUrl() {
        return configUrl;
    }

    private void setConfigUrl(final URL configUrl) {
        this.configUrl = configUrl;
    }

    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    private URL getInvalidConfigUrl() {
        return invalidConfigUrl;
    }

    private void setInvalidConfigUrl(final URL invalidConfigUrl) {
        this.invalidConfigUrl = invalidConfigUrl;
    }


}

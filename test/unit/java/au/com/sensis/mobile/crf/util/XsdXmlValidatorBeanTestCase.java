package au.com.sensis.mobile.crf.util;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.XmlValidationRuntimeException;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link XsdXmlValidatorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class XsdXmlValidatorBeanTestCase extends AbstractJUnit4TestCase {

    private static final String CRF_CONFIG_CLASSPATH
        = "/au/com/sensis/mobile/crf/util/xmlValidatorTestData/crf-config.xml";

    private static final String INVALID_CRF_CONFIG_CLASSPATH
        = "/au/com/sensis/mobile/crf/util/xmlValidatorTestData/crf-config-invalid.xml";

    private static final String SCHEMA_CLASSPATH_LOCATION
        = "/au/com/sensis/mobile/crf/config/crf-config.xsd";


    private XsdXmlValidatorBean objectUnderTest;

    private URL configUrl;
    private URL invalidConfigUrl;
    private URL schemaUrl;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new XsdXmlValidatorBean());
        setConfigUrl(getClass().getResource(CRF_CONFIG_CLASSPATH));
        setInvalidConfigUrl(getClass().getResource(INVALID_CRF_CONFIG_CLASSPATH));
        setSchemaUrl(getClass().getResource(SCHEMA_CLASSPATH_LOCATION));
    }

    @Test
    public void testValidateWhenValid() throws Throwable {
        getObjectUnderTest().validate(getConfigUrl(), getSchemaUrl());
    }

    @Test
    public void testValidateWhenInvalid() throws Throwable {
        try {
            getObjectUnderTest().validate(getInvalidConfigUrl(), getSchemaUrl());

            Assert.fail("XmlValidationRuntimeException expected");
        } catch (final XmlValidationRuntimeException e) {

            Assert.assertEquals("XmlValidationRuntimeException has wrong message", "XML at "
                    + getInvalidConfigUrl() + " is invalid.", e.getMessage());

            Assert.assertNotNull("XmlValidationRuntimeException should have a cause", e.getCause());
        }
    }

    private XsdXmlValidatorBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final XsdXmlValidatorBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private URL getConfigUrl() {
        return configUrl;
    }

    private void setConfigUrl(final URL configUrl) {
        this.configUrl = configUrl;
    }

    private URL getInvalidConfigUrl() {
        return invalidConfigUrl;
    }

    private void setInvalidConfigUrl(final URL invalidConfigUrl) {
        this.invalidConfigUrl = invalidConfigUrl;
    }

    private URL getSchemaUrl() {
        return schemaUrl;
    }

    private void setSchemaUrl(final URL schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

}

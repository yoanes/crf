package au.com.sensis.mobile.crf.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import au.com.sensis.mobile.crf.exception.XmlValidationRuntimeException;

/**
 * {@link XmlValidator} that validates XML against an XSD.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class XsdXmlValidatorBean implements XmlValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final URL xmlToValidateUrl, final URL schemaUrl) {
        InputStream mappingConfigurationInputStream = null;
        try {

            final SchemaFactory factory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            final Schema schema = factory.newSchema(schemaUrl);

            final Validator validator = schema.newValidator();
            mappingConfigurationInputStream = xmlToValidateUrl.openStream();
            final Source configurationSourceToValidate =
                    new StreamSource(mappingConfigurationInputStream);
            validator.validate(configurationSourceToValidate);
        } catch (final Exception e) {
            throw new XmlValidationRuntimeException("XML at " + xmlToValidateUrl + " is invalid.",
                    e);
        } finally {
            closeMappingConfigurationInputStream(xmlToValidateUrl, mappingConfigurationInputStream);
        }
    }

    private void closeMappingConfigurationInputStream(final URL xmlToValidateUrl,
            final InputStream mappingConfigurationInputStream) {
        if (mappingConfigurationInputStream != null) {
            try {
                mappingConfigurationInputStream.close();
            } catch (final IOException e) {
                throw new XmlValidationRuntimeException("Error closing input stream for URL: '"
                        + xmlToValidateUrl + "'", e);
            }
        }
    }

}

package au.com.sensis.mobile.crf.util;

import java.net.URL;

import org.apache.commons.configuration.HierarchicalConfigurationXMLReader;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.UnmarshalHandler;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

import au.com.sensis.mobile.crf.config.UiConfiguration;
import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;

/**
 * {@link XmlBinder} implementation that uses Castor.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CastorXmlBinderBean implements XmlBinder {

    private static final Logger LOGGER = Logger.getLogger(CastorXmlBinderBean.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshall(final URL xml) throws XmlBinderRuntimeException {
        try {
            final XMLConfiguration config = new XMLConfiguration();
            config.load(xml);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Loaded xml from '" + xml + "'.");
            }

            final XMLReader reader = new HierarchicalConfigurationXMLReader(config);

            final XMLReaderAdapter adapter = new XMLReaderAdapter(reader);

            final Unmarshaller unmarsh = new Unmarshaller(UiConfiguration.class);
            final UnmarshalHandler handler = unmarsh.createHandler();

            adapter.setDocumentHandler(handler);
            adapter.parse(new InputSource());

            final Object unmarshalledXml = handler.getObject();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Loaded xml into Java object: " + unmarshalledXml);
            }

            return unmarshalledXml;

        } catch (final Exception e) {
            throw new XmlBinderRuntimeException("Error loading XML from URL: '" + xml + "'", e);
        }

    }


}

package au.com.sensis.mobile.crf.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.springframework.core.io.UrlResource;
import org.xml.sax.SAXException;

import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;

/**
 * {@link XmlBinder} implementation that uses Castor.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CastorXmlBinderBean implements XmlBinder {

    private static final Logger LOGGER = Logger.getLogger(CastorXmlBinderBean.class);

    private final Class<?> targetClass;

    /**
     * Default constructor.
     *
     * @param targetClass Target class that the XML should unmarshal to.
     */
    public CastorXmlBinderBean(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshall(final URL xml) throws XmlBinderRuntimeException {
        try {
            return doUnmarshall(xml);
        } catch (final Exception e) {
            throw new XmlBinderRuntimeException("Error loading XML from URL: '" + xml + "'", e);
        }
    }

    private Object doUnmarshall(final URL xml) throws ConfigurationException, IOException,
            SAXException, MarshalException, ValidationException {

        final UrlResource urlResource = new UrlResource(xml);
        final InputStreamReader reader = new InputStreamReader(urlResource.getInputStream());

        final XMLContext xmlContext = new XMLContext();
        final Unmarshaller unmarsh = xmlContext.createUnmarshaller();
        unmarsh.setClass(getTargetClass());

        final Object unmarshalledXml = unmarsh.unmarshal(reader);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Loaded xml from '" + xml + "'.");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loaded xml into Java object: " + unmarshalledXml);
        }

        return unmarshalledXml;
    }

    /**
     * @return the targetClass
     */
    private Class<?> getTargetClass() {
        return targetClass;
    }


}

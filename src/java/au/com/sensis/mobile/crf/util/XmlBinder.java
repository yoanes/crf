package au.com.sensis.mobile.crf.util;

import java.net.URL;

import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;

/**
 * Simple intefracet for binding XML to Java.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface XmlBinder {

    /**
     * Unmarshalls the given xml and returns an equivalent java object graph
     * representation of it.
     *
     * @param xml
     *            The XML source for the java objects to be bound to.
     *
     * @return An object graph representation of the xml.
     *
     * @throws XmlBinderRuntimeException
     *             If a failure occurs during the unbinding of the object.
     */
    Object unmarshall(URL xml) throws XmlBinderRuntimeException;
}

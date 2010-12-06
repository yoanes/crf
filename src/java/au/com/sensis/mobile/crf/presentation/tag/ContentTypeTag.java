package au.com.sensis.mobile.crf.presentation.tag;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.PropertiesLoader;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Facade to a script tag that uses the Content Rendering Framework to resolve
 * the resource path set into {@link #setSrc(String)}.
 *
 * <p>
 * This facade also prevents duplicate tags from being written in the current
 * HTTP request. The unique id of each tag is governed by the value of the href
 * attribute.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ContentTypeTag extends SimpleTagSupport {

    private static final String CHAR_ENCODING_PROPERTY_NAME = "charEncoding";

    private static final String CONTENT_TYPE_PROPERTY_NAME = "contentType";

    private static final String RESPONSE_HEADER_PROPERTIES_ABSTRACT_FILEPATH =
            "responseHeader.properties";

    private static final Logger LOGGER = Logger.getLogger(ContentTypeTag.class);

    private Device device;
    private HttpServletResponse httpServletResponse;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        final Properties properties =
                getPropertiesLoader().loadProperties(getDevice(),
                        RESPONSE_HEADER_PROPERTIES_ABSTRACT_FILEPATH);

        setResponseContentTypeIfNecessary(properties);
        setResponseCharEncodingIfNecessary(properties);

    }

    private void setResponseContentTypeIfNecessary(final Properties properties) {
        final String contentType = properties.getProperty(CONTENT_TYPE_PROPERTY_NAME);
        if (StringUtils.isNotBlank(contentType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting contentType: " + contentType);
            }

            getResponse().setContentType(contentType);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No contentType configured. Leaving the response untouched.");
            }
        }
    }

    private void setResponseCharEncodingIfNecessary(final Properties properties) {
        final String charEncoding = properties.getProperty(CHAR_ENCODING_PROPERTY_NAME);
        if (StringUtils.isNotBlank(charEncoding)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting charEncoding: " + charEncoding);
            }

            getResponse().setCharacterEncoding(charEncoding);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No charEncoding configured. Leaving the response untouched.");
            }
        }
    }

    private PropertiesLoader getPropertiesLoader() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
        return (PropertiesLoader) webApplicationContext.getBean("crf.propertiesLoader");
    }

    /**
     * @return the httpServletResponse
     */
    private HttpServletResponse getResponse() {
        return httpServletResponse;
    }

    /**
     * @param httpServletResponse
     *            the httpServletResponse to set
     */
    public void setResponse(final HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * @return the device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @param device
     *            the device to set
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

}


package au.com.sensis.mobile.crf.presentation.tag;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Tag to be used instead of the standard html tag. Performs the following:
 * <ol>
 * <li>Sets the content type of the response.</li>
 * <li>Writes the doc type to the response.</li>
 * <li>Writes the html element start and end to the response.</li>
 * <li>Invokes the tag body to be enclosed by the html start and end tags.</li>
 * </ol>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class HtmlTag extends SimpleTagSupport {

    private static final Logger LOGGER = Logger.getLogger(HtmlTag.class);

    private static final String CONTENT_TYPE_PROPERTY_NAME = "custom.crf.contentType";

    private static final String CHARSET_PROPERTY_NAME = "custom.crf.charset";

    private static final String DOC_TYPE_PROPERTY_NAME = "custom.crf.docType";

    private static final String HTML_ROOT_PROPERTY_NAME = "custom.crf.htmlRoot";

    private static final String HTML_CLOSING_TAG = "</html>";

    private Device device;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        debugSettingsIfNecessary();

        setContentTypeAndCharEncoding();

        writeResponseContent();
    }

    private void setContentTypeAndCharEncoding() {
        getResponse().setContentType(getContentType());
        getResponse().setCharacterEncoding(getCharset());
    }

    private void writeResponseContent() throws IOException, JspException {
        getJspContext().getOut().println(getDocType());
        getJspContext().getOut().println(getHtmlRoot());

        getJspBody().invoke(getJspContext().getOut());

        getJspContext().getOut().print(HTML_CLOSING_TAG);
    }

    private void debugSettingsIfNecessary() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deviceName: " + getDevice().getName());
            LOGGER.debug(CONTENT_TYPE_PROPERTY_NAME + getContentType());
            LOGGER.debug(CHARSET_PROPERTY_NAME + getCharset());
            LOGGER.debug(DOC_TYPE_PROPERTY_NAME + getDocType());
            LOGGER.debug(HTML_ROOT_PROPERTY_NAME + getHtmlRoot());
        }
    }

    private String getContentType() {
        return getDevice().getPropertyAsString(CONTENT_TYPE_PROPERTY_NAME);
    }

    private String getCharset() {
        return getDevice().getPropertyAsString(CHARSET_PROPERTY_NAME);
    }

    private String getDocType() {
        return getDevice().getPropertyAsString(DOC_TYPE_PROPERTY_NAME);
    }

    private String getHtmlRoot() {
        return getDevice().getPropertyAsString(HTML_ROOT_PROPERTY_NAME);
    }

    /**
     * @return the httpServletResponse
     */
    private HttpServletResponse getResponse() {
        return (HttpServletResponse) (getPageContext()).getResponse();
    }

    private PageContext getPageContext() {
        return (PageContext) getJspContext();
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


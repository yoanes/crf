package au.com.sensis.mobile.crf.presentation.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Abstract base class for all custom JSP tags that use the Content Rendering Framework to
 * resolve the resource path set into {@link #setHref(String)}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceSelectorTag extends
        SimpleTagSupport implements DynamicAttributes {

    private String href;
    private Device device;
    private final List<DynamicTagAttribute> dynamicAttributes
        = new ArrayList<DynamicTagAttribute>();

    /**
     * @return the href
     */
    public final String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public final void setHref(final String href) {
        this.href = href;
    }

    /**
     * {@inheritDoc}
     */
    public final void setDynamicAttribute(final String uri, final String localName,
            final Object value) throws JspException {
                getDynamicAttributes().add(
                        new DynamicTagAttribute(localName, value));
            }

    /**
     * @return the dynamicAttributes
     */
    protected final List<DynamicTagAttribute> getDynamicAttributes() {
        return dynamicAttributes;
    }

    /**
     * @return the device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * Validate {@link #getHref()}.
     *
     * @throws IllegalArgumentException
     *             Thrown if {@link #getHref()} is invalid.
     */
    protected final void validateHrefAttribute()
            throws IllegalArgumentException {
        // NOTE: we prefer this simple approach to validation over using the JEE
        // TagExtraInfo approach since we do not see a need for translation time
        // validation.
        if (StringUtils.isBlank(getHref()) || getHref().startsWith("..")
                || getHref().startsWith("/")) {
            throw new IllegalArgumentException(
                    "href must not start with '..' or '/'. Was: '" + getHref()
                            + "'");
        }

    }

}

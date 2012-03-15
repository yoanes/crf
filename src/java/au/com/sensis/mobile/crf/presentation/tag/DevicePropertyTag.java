package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.wireless.web.mobile.DeviceDetection;

/**
 * JSP tag handler for retrieving a named property from a {@link Device}.
 * If {@link #getVar()} is set, the property will be set into a variable with this name (in page
 * scope). Otherwise, the value of the property will be written to the JSP output stream.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DevicePropertyTag extends SimpleTagSupport {

    private String property;
    private String var;
    private Device device;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        validateAttributes();

        if (StringUtils.isNotBlank(getVar())) {
            getJspContext().setAttribute(getVar(), getDevice().getPropertyAsString(getProperty()));
        } else {
            getJspContext().getOut().write(getDevice().getPropertyAsString(getProperty()));
        }
    }

    private void validateAttributes() {
        // NOTE: we prefer this simple approach to validation over using the JEE
        // TagExtraInfo approach since we do not see a need for translation time
        // validation.
        validatePropertyAttribute();
        validateDeviceAttribute();
        validateVarAttribute();
    }

    private void validatePropertyAttribute() {
        if (StringUtils.isBlank(getProperty())) {
            throw new IllegalArgumentException("property attribute must not be blank: '"
                    + getProperty() + "'");
        }
    }

    private void validateDeviceAttribute() {
        Validate.notNull(getDevice(), "device attribute must not be null");
    }

    private void validateVarAttribute() {
        if ((getVar() != null) && StringUtils.isBlank(getVar())) {
            throw new IllegalArgumentException("var attribute must be either null or non-blank: '"
                    + getVar() + "'");
        }
    }

    /**
     * Name of the optional variable to set the result into.
     *
     * @return Name of the optional variable to set the result into.
     */
    public String getVar() {
        return var;
    }

    /**
     * Name of the optional variable to set the result into.
     *
     * @param var Name of the optional variable to set the result into.
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /**
     * The name of the property to retrieve the value for.
     *
     * @return The name of the property to retrieve the value for.
     */
    public String getProperty() {
        return property;
    }

    /**
     * The name of the property to retrieve the value for.
     *
     * @param property
     *            The name of the property to retrieve the value for.
     */
    public void setProperty(final String property) {
        this.property = property;
    }

    /**
     * @return {@link Device} for the current request.
     */
    public Device getDevice() {

        if (device == null) {
            return DeviceDetection.getDevice();
        }
        return device;
    }

    /**
     * @param device {@link Device} for the current request.
     */
    public void setDevice(final Device device) {
        this.device = device;
    }
}

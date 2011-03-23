package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

/**
 * Base class for tags that render the prefix that is used for abstract src paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractSrcPrefixTag extends SimpleTagSupport {

    private String var;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void doTag() throws JspException, IOException {
        if (StringUtils.isNotBlank(getVar())) {
            getJspContext().setAttribute(getVar(), getSrcPrefix());
        } else {
            getJspContext().getOut().write(getSrcPrefix());
        }
    }

    /**
     * @return Prefix that is used for abstract src paths.
     */
    protected abstract String getSrcPrefix();

    /**
     * Ensure that the path ends with a separator and return the result.
     *
     * @param path
     *            Path to check.
     * @return the path ending with a separator. If it already did so, the path
     *         is unchanged.
     */
    protected final String ensureEndsWithSeparator(final String path) {
        if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
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
}

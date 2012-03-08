package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Tag that renders the scripts previously bundled by a {@link BundleScriptsTag}.
 *
 * @author Brendan Doyle
 */
public class RenderBundledScriptsTag
        extends AbstractTag {

    /**
     * var to get the output from. Must be set, and must match a {@link BundleTagData} in the
     * request scope.
     */
    private String var;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag()
            throws IOException {

        if (StringUtils.isBlank(getVar())) {

            throw new IllegalArgumentException("var attribute must be set.");
        }

        // We set the attribute into and retrieve it from the request scope as this will probably be
        // what will be wanted. We could recognise a scope attribute and use page scope as the
        // default (to mimic the behaviour of c:set), but haven't bothered to at the moment.
        // We would need to change this for both AbstractBundleTag and RenderBundledScriptsTag.
        final Object attributeValue = getJspContext().getAttribute(getVar(),
                PageContext.REQUEST_SCOPE);

        if (attributeValue == null) {

            throw new IllegalArgumentException(
                    "no value: var attribute must match a previous crf:bundleScripts tag.");
        }

        if (! (attributeValue instanceof BundleTagData)) {

            throw new IllegalArgumentException(
                    "incorrect type: var attribute must match a previous crf:bundleScripts tag.");
        }

        final BundleTagData bundleTagData = (BundleTagData) attributeValue;

        final BundleScriptsTagDelegate bundleTagDelegate
                = new BundleScriptsTagDelegate(getJspContext(), bundleTagData);

        bundleTagDelegate.writeTags(getJspContext().getOut(), getDynamicAttributes());
    }

    /**
     * @return  the var.
     */
    protected String getVar() {

        return var;
    }

    /**
     * @param var   the var to set.
     */
    public void setVar(final String var) {

        this.var = var;
    }
}

package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Tag that bundles the output of any child tags that register {@link Resource}s with this
 * {@link AbstractBundleTag} via the {@link #addResourcesToBundle(List)} method.
 *
 * <p>
 * This tag has no need to inherit the complexity of {@link AbstractDuplicatePreventingTag} because
 * any child tags are assumed to already have this protection. So if this {@link AbstractBundleTag}
 * ends up with a non-empty {@link #getResourcesToBundle()}, this is because there was a child tag
 * that has not occurred in the request before.
 * </p>
 *
 * <p>
 * NOTE: the bundling performed by this tag is different to
 * {@link au.com.sensis.mobile.crf.service.BundleFactory}. The latter occurs at a lower level and
 * can be considered as built into the CRF engine. In contrast, the bundling performed by this tag
 * is at the sole discretion of page authors. Yeah, the naming is a bit confusing. Not sure what
 * naming would be less confusing though.
 * </p>
 *
 * @author w12495
 */
public abstract class AbstractBundleTag<T extends BundleTagDelegate>
extends AbstractTag
implements BundleTag {

    /**
     * Optional var to save the output to. If this is set, the tag will save it's state to an
     * attribute with the name of this var (using a {@link BundleTagData}) and will not write
     * anything to the page output. The {@link RenderBundledScriptsTag} can be used to write the
     * output of this attribute.
     */
    private String var;

    /**
     * Data that child tags have registered with this {@link AbstractBundleTag}.
     */
    private final BundleTagData bundleTagData = new BundleTagData();

    /**
     * A delegate which caters for the writing of bundled resources.
     */
    private T bundleTagDelegate;

    protected abstract T createBundleTagDelegate();

    /**
     * @param id    the id to set.
     */
    public void setId(final String id) {

        getBundleTagData().setId(id);
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

    @Override
    public void doTag() throws JspException, IOException {
        makeUsAvailableToChildTags();

        invokeBodyContent();

        if (StringUtils.isNotBlank(getVar())) {

            // We set the attribute into the request scope as this will probably be what will be
            // wanted. We could recognise a scope attribute and use page scope as the default (to
            // mimic the behaviour of c:set), but haven't bothered to at the moment.
            // We would need to change this for both AbstractBundleTag and RenderBundledScriptsTag.
            getJspContext().setAttribute(getVar(), getBundleTagData(), PageContext.REQUEST_SCOPE);

        } else {

            getBundleTagDelegate().writeTags(getJspContext().getOut(), getDynamicAttributes());
        }
    }

    private void makeUsAvailableToChildTags() {

        // We use a JspContextBundleTagStack instead of requiring child tags to rely on
        // {@link javax.servlet.jsp.tagext.SimpleTagSupport#findAncestorWithClass(
        // javax.servlet.jsp.tagext.JspTag, Class)} because the latter does not cater to the case
        // that child tags are executed via a dynamic JSP include.
        getBundleTagStack().pushBundleTag(getJspContext(), this);
    }

    private void invokeBodyContent()
            throws JspException, IOException {

        try {

            // Let any child tags do their thing. If they wish to have their external resources
            // bundled by this tag, we expect them to find us using the JspContextBundleTagStack,
            // then call addResourcesToBundle.
            getJspBody().invoke(null);

        } finally {

            cleanUpJspContextBundleTagStack();
        }
    }

    private void cleanUpJspContextBundleTagStack() {

        getBundleTagStack().removeBundleTag(getJspContext());
    }

    protected JspContextBundleTagStack getBundleTagStack() {

        return (JspContextBundleTagStack) getWebApplicationContext().getBean(getTagStackBeanName());
    }

    protected WebApplicationContext getWebApplicationContext() {

        final PageContext pc = (PageContext) getJspContext();

        return WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
    }

    /**
     * @return  the name of the {@link JspContextBundleTagStack} bean to be obtained from the Spring
     *          context.
     */
    protected abstract String getTagStackBeanName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourcesToBundle(final List<Resource> resources) {

        getBundleTagData().addResourcesToBundle(resources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rememberAbsoluteHref(final String href) {

        getBundleTagData().rememberAbsoluteHref(href);
    }

    /**
     * @return  the bundleTagData.
     */
    protected BundleTagData getBundleTagData() {

        return bundleTagData;
    }

    /**
     * @return  the bundleTagDelegate.
     */
    protected T getBundleTagDelegate() {

        if (bundleTagDelegate == null) {

            bundleTagDelegate = createBundleTagDelegate();
        }

        return bundleTagDelegate;
    }

    public boolean hasBundlingEnabled() {

        return getBundleTagDelegate().hasBundlingEnabled();
    }
}


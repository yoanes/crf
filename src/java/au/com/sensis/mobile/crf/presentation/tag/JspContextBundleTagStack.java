package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.JspContext;

/**
 * Provides access to a stack of {@link BundleTag}s stored in a {@link JspContext}.
 * We use this structure instead of relying on
 * {@link javax.servlet.jsp.tagext.SimpleTagSupport#findAncestorWithClass(
 * javax.servlet.jsp.tagext.JspTag, Class)}
 * because the latter does not cater to the case that child tags are executed via a dynamic
 * JSP include.
 *
 * <p>
 * Furthermore, we use a stack in the event that page authors nest multiple bundle tags within
 * each other.
 * </p>
 *
 * @author w12495
 *
 */
public interface JspContextBundleTagStack {

    /**
     * Push a {@link BundleTag} onto the stack.
     *
     * @param jspContext {@link JspContext} to store the stack.
     * @param bundleTag {@link BundleTag} to push onto the stack.
     */
    void pushBundleTag(JspContext jspContext, BundleTag bundleTag);

    /**
     * Returns the {@link BundleTag} stored at the top of the stack but does not remove
     * it from the stack.
     *
     * @param jspContext {@link JspContext} storing the stack.
     * @return {@link BundleTag} stored at the top of the stack. Null if there is none.
     */
    BundleTag getBundleTag(JspContext jspContext);

    /**
     * Remove the {@link BundleTag} stored at the top of the stack.
     *
     * @param jspContext {@link JspContext} storing the stack.
     */
    void removeBundleTag(JspContext jspContext);
}

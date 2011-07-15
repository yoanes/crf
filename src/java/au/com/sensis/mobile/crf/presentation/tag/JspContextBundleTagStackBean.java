package au.com.sensis.mobile.crf.presentation.tag;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

/**
 * Default {@link JspContextBundleTagStack} implementation.
 *
 * @author w12495
 */
public class JspContextBundleTagStackBean implements JspContextBundleTagStack {

    private final String bundleTagRequestAttributeName;

    /**
     * Constructor.
     *
     * @param bundleTagRequestAttributeName
     *            Attribute name to store the stack in the {@link JspContext}.
     */
    public JspContextBundleTagStackBean(final String bundleTagRequestAttributeName) {
        this.bundleTagRequestAttributeName = bundleTagRequestAttributeName;
    }

    @Override
    public BundleTag getBundleTag(final JspContext jspContext) {
        return getBundleTagDeque(jspContext).peekFirst();
    }

    @Override
    public void removeBundleTag(final JspContext jspContext) {
        getBundleTagDeque(jspContext).removeFirst();
    }

    @Override
    public void pushBundleTag(final JspContext jspContext, final BundleTag bundleTag) {
        getBundleTagDeque(jspContext).addFirst(bundleTag);
    }

    @SuppressWarnings("unchecked")
    private void setBundleTagDeque(final JspContext jspContext,
            final Deque<BundleTag> deque) {
        jspContext.setAttribute(getBundleTagRequestAttributeName(), deque,
                PageContext.REQUEST_SCOPE);

    }

    @SuppressWarnings("unchecked")
    private Deque<BundleTag> getBundleTagDeque(final JspContext jspContext) {
        Deque<BundleTag> deque = (Deque) jspContext.getAttribute(
                getBundleTagRequestAttributeName(), PageContext.REQUEST_SCOPE);
        if (deque == null) {
            deque = new ArrayDeque<BundleTag>();
            setBundleTagDeque(jspContext, deque);
        }

        return deque;
    }

    /**
     * @return the bundleTagRequestAttributeName
     */
    private String getBundleTagRequestAttributeName() {
        return bundleTagRequestAttributeName;
    }

}

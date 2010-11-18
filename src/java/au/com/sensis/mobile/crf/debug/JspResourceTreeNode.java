package au.com.sensis.mobile.crf.debug;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * {@link AbstractResourceTreeNode} for a JSP resource.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourceTreeNode extends AbstractResourceTreeNode {

    /**
     * Constructor.
     *
     * @param resource {@link Resource} that this node wraps.
     */
    public JspResourceTreeNode(final Resource resource) {
        super(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeDescription() {
        return "jsp";
    }

}

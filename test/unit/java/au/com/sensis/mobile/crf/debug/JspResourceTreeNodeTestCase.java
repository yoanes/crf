package au.com.sensis.mobile.crf.debug;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Unit test {@link JspResourceTreeNode}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourceTreeNodeTestCase extends AbstractResourceTreeNodeTestCase {

    @Override
    protected AbstractResourceTreeNode createObjectUnderTest(final Resource resource) {
        return new JspResourceTreeNode(resource);
    }

    @Override
    protected AbstractResourceTreeNode createResourceTreeNode1(final Resource resource) {
        final JspResourceTreeNode node = new JspResourceTreeNode(resource);
        return node;
    }

    @Override
    protected AbstractResourceTreeNode createResourceTreeNode2(final Resource resource) {
        final JspResourceTreeNode node = new JspResourceTreeNode(resource);
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExpectedTypeDescription() {
        return "jsp";
    }
}

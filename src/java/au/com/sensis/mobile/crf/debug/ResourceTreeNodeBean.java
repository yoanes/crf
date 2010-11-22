package au.com.sensis.mobile.crf.debug;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Standard {@link ResourceTreeNode} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceTreeNodeBean implements ResourceTreeNode {

    private final Resource resource;
    private final List<ResourceTreeNode> children = new ArrayList<ResourceTreeNode>();
    private ResourceTreeNode parent;
    private int zeroBasedDepth;

    /**
     * Constructor.
     *
     * @param resource {@link Resource} that this node wraps.
     */
    public ResourceTreeNodeBean(final Resource resource) {
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTreeNode> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource() {
        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(final ResourceTreeNode treeNode) {
        getChildren().add(treeNode);
        treeNode.setParent(this);
        treeNode.setZeroBasedDepth(getZeroBasedDepth() + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceTreeNode getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getZeroBasedDepth() {
        return zeroBasedDepth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setZeroBasedDepth(final int depth) {
        zeroBasedDepth = depth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(final ResourceTreeNode resourceTreeNode) {
        parent = resourceTreeNode;
    }

}

package au.com.sensis.mobile.crf.debug;

import java.util.List;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Node of a tree capturing information about a resolved {@link Resource}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceTreeNode {

    /**
     * @param depth Depth of this node in the tree, where the root has a depth of 0.
     */
    void setZeroBasedDepth(int depth);

    /**
     * @return depth Depth of this node in the tree, where the root has a depth of 0.
     */
    int getZeroBasedDepth();

    /**
     * @return {@link Resource} that was resolved.
     */
    Resource getResource();

    /**
     * @return Children of this node. May not be null.
     */
    List<ResourceTreeNode> getChildren();

    /**
     * @param resourceTreeNode Parent of this node.
     */
    void setParent(ResourceTreeNode resourceTreeNode);

    /**
     * @return Parent of this node.
     */
    ResourceTreeNode getParent();

    /**
     * @param treeNode Add the given node as a child.
     */
    void addChild(final ResourceTreeNode treeNode);

}

package au.com.sensis.mobile.crf.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;

/**
 * Tree based structure for storing resource resolution debugging info. The tree
 * consists of a root node, {@link #getRoot()}, plus a reference to a
 * "current node" in the tree ({@link #getCurrentNode()}). All calls to
 * {@link #addChildToCurrentNode(ResourceTreeNode)} are relative to the "current
 * node". {@link #addChildToCurrentNodeAndPromoteToCurrent(ResourceTreeNode)}
 * provides a way of adding a new node and promoting it to be the new current
 * node. {@link #promoteParentToCurrent()} provides a means of making the
 * current node's parent become the new current node. These three methods
 * provide a means of building a tree analogous to a pre-order tree traversal
 * using the following sequence:
 * <ol>
 * <li>Add a node to the tree:
 * {@link #addChildToCurrentNodeAndPromoteToCurrent(ResourceTreeNode)}</li>
 * <li>Add children to the new node:
 * {@link #addChildToCurrentNode(ResourceTreeNode)}</li>
 * </ol>
 * Repeat the above steps as many times as necessary to keep building the tree
 * downwards. Call {@link #promoteParentToCurrent()} every time you want to back
 * up the tree by one level, then repeat the above steps again to build the tree
 * down another branch.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolutionTree {

    private static final String PLAIN_TEXT_GRAPH_TAB_SPACES = "    ";

    private ResourceTreeNode root;
    private ResourceTreeNode currentNode;

    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public ResourceResolutionTree() {
    }

    /**
     * Default constructor.
     *
     * @param enabled
     *            true if this tree is enabled (ie. whether it should store
     *            added nodes).
     */
    public ResourceResolutionTree(final boolean enabled) {
        setEnabled(enabled);
    }

    /**
     * Add the given node to this tree. If {@link #getRoot()} is null, the added
     * node becomes the root. Otherwise, the added node is added as a child of
     * {@link #getCurrentNode()}.
     *
     * @param treeNode
     *            Node to be added.
     */
    public void addChildToCurrentNode(final ResourceTreeNode treeNode) {
        if (isEnabled()) {
            doAddChildToCurrentNode(treeNode);
        }
    }

    private void doAddChildToCurrentNode(final ResourceTreeNode treeNode) {
        if (getRoot() == null) {
            initRoot(treeNode);
        } else {
            getCurrentNode().addChild(treeNode);
        }
    }

    /**
     * Same as {@link #addChildToCurrentNode(ResourceTreeNode)} but the new node
     * becomes the value of {@link #getCurrentNode()}.
     *
     * @param treeNode
     *            Node to be added.
     */
    public void addChildToCurrentNodeAndPromoteToCurrent(final ResourceTreeNode treeNode) {
        if (isEnabled()) {
            doAddChildToCurrentNodeAndPromoteToCurrent(treeNode);
        }
    }

    private void doAddChildToCurrentNodeAndPromoteToCurrent(final ResourceTreeNode treeNode) {
        if (getRoot() == null) {
            initRoot(treeNode);
        } else {
            getCurrentNode().addChild(treeNode);
            setCurrentNode(treeNode);
        }
    }

    private void initRoot(final ResourceTreeNode treeNode) {
        setRoot(treeNode);
        setCurrentNode(treeNode);
    }

    /**
     * The parent of {@link #getCurrentNode()} becomes the new current node.
     */
    public void promoteParentToCurrent() {
        if (isEnabled()) {
            doPromoteParentToCurrent();
        }
    }

    private void doPromoteParentToCurrent() {
        if (getRoot() == null) {
            throw new IllegalStateException("Illegal call when root node is null");
        }

        if (getCurrentNode().getParent() != null) {
            setCurrentNode(getCurrentNode().getParent());
        }
    }

    /**
     * Graph this tree as plain text.
     *
     * @return Graph of this tree as plain text.
     */
    public String graphAsPlainText() {
        if (isEnabled()) {
            return doGraphAsPlainText();
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String doGraphAsPlainText() {
        final StringBuilder graph = new StringBuilder();
        final Iterator<ResourceTreeNode> preOrderIterator = preOrderIterator();
        while (preOrderIterator.hasNext()) {
            final ResourceTreeNode resourceTreeNode = preOrderIterator.next();
            graph.append(StringUtils.repeat(PLAIN_TEXT_GRAPH_TAB_SPACES, resourceTreeNode
                    .getZeroBasedDepth()));
            graph.append(resourceTreeNode.getZeroBasedDepth() + 1);
            graph.append(". ");
            graph.append(resourceTreeNode.getResource().getNewPath());
            graph.append("\n");
        }

        return graph.toString();
    }

    /**
     * @return Iterator for iterating through this tree using a pre-order
     *         traversal algorithm.
     */
    public Iterator<ResourceTreeNode> preOrderIterator() {
        if (isEnabled()) {
            return new PreOrderTreeIterator(getRoot());
        } else {
            return new ArrayList<ResourceTreeNode>().iterator();
        }
    }

    /**
     * @return the root
     */
    public ResourceTreeNode getRoot() {
        return root;
    }

    /**
     * @return the currentNode
     */
    public ResourceTreeNode getCurrentNode() {
        return currentNode;
    }

    /**
     * @param root
     *            the root to set
     */
    private void setRoot(final ResourceTreeNode root) {
        this.root = root;
    }

    /**
     * @param currentNode
     *            the currentNode to set
     */
    private void setCurrentNode(final ResourceTreeNode currentNode) {
        this.currentNode = currentNode;
    }

    /**
     * @return true if this tree is enabled (ie. whether it should store added nodes).
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled true if this tree is enabled (ie. whether it should store added nodes).
     */
    private void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private static class PreOrderTreeIterator implements Iterator<ResourceTreeNode> {

        private final Deque<ResourceTreeNode> stack = new ArrayDeque<ResourceTreeNode>();

        public PreOrderTreeIterator(final ResourceTreeNode root) {
            if (root != null) {
                getStack().push(root);
            }
        }

        @Override
        public boolean hasNext() {
            return !getStack().isEmpty();
        }

        @Override
        public ResourceTreeNode next() {
            if (hasNext()) {
                final ResourceTreeNode result = getStack().pop();

                final List<ResourceTreeNode> reversedChildren = new ArrayList<ResourceTreeNode>();
                reversedChildren.addAll(result.getChildren());
                Collections.reverse(reversedChildren);

                for (final ResourceTreeNode child : reversedChildren) {
                    getStack().push(child);
                }

                return result;
            } else {
                throw new NoSuchElementException("No more elements exist.");
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove method is not supported.");
        }

        private Deque<ResourceTreeNode> getStack() {
            return stack;
        }

    }
}

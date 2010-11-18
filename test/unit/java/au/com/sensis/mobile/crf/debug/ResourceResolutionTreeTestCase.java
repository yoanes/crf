package au.com.sensis.mobile.crf.debug;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceResolutionTree}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolutionTreeTestCase extends AbstractJUnit4TestCase {

    private ResourceResolutionTree objectUnderTest;
    private ResourceTreeNode resourceTreeNode1;
    private ResourceTreeNode resourceTreeNode2;
    private ResourceTreeNode resourceTreeNode3;
    private ResourceTreeNode resourceTreeNode4;
    private ResourceTreeNode resourceTreeNode5;
    private ResourceTreeNode resourceTreeNode6;
    private ResourceTreeNode resourceTreeNode7;
    private Resource mockResource1;
    private Resource mockResource2;
    private Resource mockResource3;
    private Resource mockResource4;
    private Resource mockResource5;
    private Resource mockResource6;
    private Resource mockResource7;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setResourceTreeNode1(new JspResourceTreeNode(getMockResource1()));
        setResourceTreeNode2(new JspResourceTreeNode(getMockResource2()));
        setResourceTreeNode3(new JspResourceTreeNode(getMockResource3()));
        setResourceTreeNode4(new JspResourceTreeNode(getMockResource4()));
        setResourceTreeNode5(new JspResourceTreeNode(getMockResource5()));
        setResourceTreeNode6(new JspResourceTreeNode(getMockResource6()));
        setResourceTreeNode7(new JspResourceTreeNode(getMockResource7()));

        setObjectUnderTest(new ResourceResolutionTree());
    }

    @Test
    public void testAddNodeWhenNoRoot() throws Throwable {
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode1());

        Assert
                .assertEquals("Root is wrong", getResourceTreeNode1(), getObjectUnderTest()
                        .getRoot());
        Assert.assertEquals("Root depth is wrong", 0, getObjectUnderTest().getRoot()
                .getZeroBasedDepth());
        Assert.assertEquals("CurrNode is wrong", getResourceTreeNode1(), getObjectUnderTest()
                .getCurrentNode());
    }

    @Test
    public void testAddNodeWhenRootExists() throws Throwable {
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode1());
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode2());

        Assert
                .assertEquals("Root is wrong", getResourceTreeNode1(), getObjectUnderTest()
                        .getRoot());
        Assert.assertEquals("CurrNode is wrong", getResourceTreeNode1(), getObjectUnderTest()
                .getCurrentNode());
    }

    @Test
    public void testAddChildToCurrentNodeAndPromoteToCurrentWhenNoRoot() throws Throwable {

        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode1());

        Assert
                .assertEquals("Root is wrong", getResourceTreeNode1(), getObjectUnderTest()
                        .getRoot());
        Assert.assertEquals("Root depth is wrong", 0, getObjectUnderTest().getRoot()
                .getZeroBasedDepth());
        Assert.assertEquals("CurrNode is wrong", getResourceTreeNode1(), getObjectUnderTest()
                .getCurrentNode());

    }

    @Test
    public void testAddChildToCurrentNodeAndPromoteToCurrentWhenRootExists() throws Throwable {
        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode1());

        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode2());

        Assert
                .assertEquals("Root is wrong", getResourceTreeNode1(), getObjectUnderTest()
                        .getRoot());
        Assert.assertEquals("CurrNode is wrong", getResourceTreeNode2(), getObjectUnderTest()
                .getCurrentNode());
    }

    @Test
    public void testPromoteParentToCurrentWhenNoRoot() throws Throwable {
        try {
            getObjectUnderTest().promoteParentToCurrent();

            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "Illegal call when root node is null", e.getMessage());
        }
    }

    @Test
    public void testPromoteParentToCurrentWhenCurrentNodeHasNoParent() throws Throwable {
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode1());

        getObjectUnderTest().promoteParentToCurrent();

        Assert.assertEquals("Current node is wrong", getResourceTreeNode1(), getObjectUnderTest()
                .getCurrentNode());

    }

    @Test
    public void testPromoteParentToCurrentWhenParentExists() throws Throwable {
        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode1());

        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode2());

        getObjectUnderTest().promoteParentToCurrent();

        Assert.assertEquals("CurrNode is wrong", getResourceTreeNode1(), getObjectUnderTest()
                .getCurrentNode());

    }

    @Test
    public void testPreOrderIteratorWhenTreeEmpty() throws Throwable {
        final Iterator<ResourceTreeNode> preOrderIterator = getObjectUnderTest().preOrderIterator();

        Assert.assertFalse("preOrderIterator.hasNext() should be false",
                preOrderIterator.hasNext());
    }

    @Test
    public void testPreOrderIterator() throws Throwable {

        populateTree();

        final Iterator<ResourceTreeNode> preOrderIterator = getObjectUnderTest().preOrderIterator();

        Assert.assertTrue("preOrderIterator.hasNext() should be true before first call to next()",
                preOrderIterator.hasNext());

        Assert.assertEquals("iterator element at index 0 is wrong", getResourceTreeNode1(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 1 is wrong", getResourceTreeNode2(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 2 is wrong", getResourceTreeNode3(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 3 is wrong", getResourceTreeNode4(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 4 is wrong", getResourceTreeNode5(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 5 is wrong", getResourceTreeNode6(),
                preOrderIterator.next());
        Assert.assertEquals("iterator element at index 6 is wrong", getResourceTreeNode7(),
                preOrderIterator.next());

        Assert.assertFalse("preOrderIterator.hasNext() should be false after last call to next()",
                preOrderIterator.hasNext());

        try {
            preOrderIterator.next();

            Assert.fail("NoSuchElementException expected");
        } catch (final NoSuchElementException e) {

            Assert.assertEquals("NoSuchElementException has wrong message",
                    "No more elements exist.", e.getMessage());
        }
    }

    private void populateTree() {
        // Root.
        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode1());

        // Left branch under root.
        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode2());
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode3());
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode4());
        getObjectUnderTest().promoteParentToCurrent();

        // Right branch under root.
        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode5());
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode6());
        getObjectUnderTest().addChildToCurrentNode(getResourceTreeNode7());
    }

    @Test
    public void testPreOrderIteratorRemove() throws Throwable {

        getObjectUnderTest().addChildToCurrentNodeAndPromoteToCurrent(getResourceTreeNode1());

        try {
            getObjectUnderTest().preOrderIterator().remove();

            Assert.fail("UnsupportedOperationException expected");
        } catch (final UnsupportedOperationException e) {

            Assert.assertEquals("UnsupportedOperationException has wrong message",
                    "remove method is not supported.", e.getMessage());
        }

    }

    @Test
    public void testGraphAsPlainText() throws Throwable {
        // Root.
        EasyMock.expect(getMockResource1().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/iphone-ipod/detail/bdp.jsp");

        // Left branch under root.
        EasyMock.expect(getMockResource2().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/default/detail/head.jsp");
        EasyMock.expect(getMockResource3().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/iphone-ipod/detail/styles.jsp");
        EasyMock.expect(getMockResource4().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/webkit/detail/scripts.jsp");

        // Right branch under root.
        EasyMock.expect(getMockResource5().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/default/detail/body.jsp");
        EasyMock.expect(getMockResource6().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/default/detail/header.jsp");
        EasyMock.expect(getMockResource7().getNewPath()).andReturn(
                "/WEB-INF/view/jsp/default/detail/footer.jsp");

        replay();

        populateTree();

        final String expectedGraph
            = "1. jsp: /WEB-INF/view/jsp/iphone-ipod/detail/bdp.jsp\n"
                + "    2. jsp: /WEB-INF/view/jsp/default/detail/head.jsp\n"
                + "        3. jsp: /WEB-INF/view/jsp/iphone-ipod/detail/styles.jsp\n"
                + "        3. jsp: /WEB-INF/view/jsp/webkit/detail/scripts.jsp\n"
                + "    2. jsp: /WEB-INF/view/jsp/default/detail/body.jsp\n"
                + "        3. jsp: /WEB-INF/view/jsp/default/detail/header.jsp\n"
                + "        3. jsp: /WEB-INF/view/jsp/default/detail/footer.jsp\n";
        final String actualGraph = getObjectUnderTest().graphAsPlainText();

        Assert.assertEquals("graphAsPlainText is wrong", expectedGraph, actualGraph);

    }

    /**
     * @return the objectUnderTest
     */
    private ResourceResolutionTree getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ResourceResolutionTree objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the resourceTreeNode1
     */
    private ResourceTreeNode getResourceTreeNode1() {
        return resourceTreeNode1;
    }

    /**
     * @param resourceTreeNode1 the resourceTreeNode1 to set
     */
    private void setResourceTreeNode1(final ResourceTreeNode resourceTreeNode1) {
        this.resourceTreeNode1 = resourceTreeNode1;
    }

    /**
     * @return the resourceTreeNode2
     */
    private ResourceTreeNode getResourceTreeNode2() {
        return resourceTreeNode2;
    }

    /**
     * @param resourceTreeNode2 the resourceTreeNode2 to set
     */
    private void setResourceTreeNode2(final ResourceTreeNode resourceTreeNode2) {
        this.resourceTreeNode2 = resourceTreeNode2;
    }

    /**
     * @return the mockResource1
     */
    public Resource getMockResource1() {
        return mockResource1;
    }

    /**
     * @param mockResource1 the mockResource1 to set
     */
    public void setMockResource1(final Resource mockResource1) {
        this.mockResource1 = mockResource1;
    }

    /**
     * @return the mockResource2
     */
    public Resource getMockResource2() {
        return mockResource2;
    }

    /**
     * @param mockResource2 the mockResource2 to set
     */
    public void setMockResource2(final Resource mockResource2) {
        this.mockResource2 = mockResource2;
    }

    /**
     * @return the mockResource3
     */
    public Resource getMockResource3() {
        return mockResource3;
    }

    /**
     * @param mockResource3 the mockResource3 to set
     */
    public void setMockResource3(final Resource mockResource3) {
        this.mockResource3 = mockResource3;
    }

    /**
     * @return the mockResource4
     */
    public Resource getMockResource4() {
        return mockResource4;
    }

    /**
     * @param mockResource4 the mockResource4 to set
     */
    public void setMockResource4(final Resource mockResource4) {
        this.mockResource4 = mockResource4;
    }

    /**
     * @return the mockResource5
     */
    public Resource getMockResource5() {
        return mockResource5;
    }

    /**
     * @param mockResource5 the mockResource5 to set
     */
    public void setMockResource5(final Resource mockResource5) {
        this.mockResource5 = mockResource5;
    }

    /**
     * @return the mockResource6
     */
    public Resource getMockResource6() {
        return mockResource6;
    }

    /**
     * @param mockResource6 the mockResource6 to set
     */
    public void setMockResource6(final Resource mockResource6) {
        this.mockResource6 = mockResource6;
    }

    /**
     * @return the mockResource7
     */
    public Resource getMockResource7() {
        return mockResource7;
    }

    /**
     * @param mockResource7 the mockResource7 to set
     */
    public void setMockResource7(final Resource mockResource7) {
        this.mockResource7 = mockResource7;
    }

    /**
     * @return the resourceTreeNode3
     */
    private ResourceTreeNode getResourceTreeNode3() {
        return resourceTreeNode3;
    }

    /**
     * @param resourceTreeNode3 the resourceTreeNode3 to set
     */
    private void setResourceTreeNode3(final ResourceTreeNode resourceTreeNode3) {
        this.resourceTreeNode3 = resourceTreeNode3;
    }

    /**
     * @return the resourceTreeNode4
     */
    private ResourceTreeNode getResourceTreeNode4() {
        return resourceTreeNode4;
    }

    /**
     * @param resourceTreeNode4 the resourceTreeNode4 to set
     */
    private void setResourceTreeNode4(final ResourceTreeNode resourceTreeNode4) {
        this.resourceTreeNode4 = resourceTreeNode4;
    }

    /**
     * @return the resourceTreeNode5
     */
    private ResourceTreeNode getResourceTreeNode5() {
        return resourceTreeNode5;
    }

    /**
     * @param resourceTreeNode5 the resourceTreeNode5 to set
     */
    private void setResourceTreeNode5(final ResourceTreeNode resourceTreeNode5) {
        this.resourceTreeNode5 = resourceTreeNode5;
    }

    /**
     * @return the resourceTreeNode6
     */
    private ResourceTreeNode getResourceTreeNode6() {
        return resourceTreeNode6;
    }

    /**
     * @param resourceTreeNode6 the resourceTreeNode6 to set
     */
    private void setResourceTreeNode6(final ResourceTreeNode resourceTreeNode6) {
        this.resourceTreeNode6 = resourceTreeNode6;
    }

    /**
     * @return the resourceTreeNode7
     */
    private ResourceTreeNode getResourceTreeNode7() {
        return resourceTreeNode7;
    }

    /**
     * @param resourceTreeNode7 the resourceTreeNode7 to set
     */
    private void setResourceTreeNode7(final ResourceTreeNode resourceTreeNode7) {
        this.resourceTreeNode7 = resourceTreeNode7;
    }


}

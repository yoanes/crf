package au.com.sensis.mobile.crf.debug;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Base test case for {@link AbstractResourceTreeNode}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceTreeNodeTestCase extends AbstractJUnit4TestCase {

    private AbstractResourceTreeNode objectUnderTest;
    private AbstractResourceTreeNode resourceTreeNode1;
    private AbstractResourceTreeNode resourceTreeNode2;
    private Resource mockResource;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUpBase() throws Exception {
        setObjectUnderTest(createObjectUnderTest(getMockResource()));

        setResourceTreeNode1(createResourceTreeNode1(getMockResource()));
        setResourceTreeNode2(createResourceTreeNode2(getMockResource()));
    }

    @Test
    public void testAddAndGetChildren() throws Throwable {
        getObjectUnderTest().addChild(getResourceTreeNode1());
        getObjectUnderTest().addChild(getResourceTreeNode2());

        Assert.assertEquals("Children are wrong", Arrays.asList(getResourceTreeNode1(),
                getResourceTreeNode2()), getObjectUnderTest().getChildren());

        Assert.assertEquals("First child has wrong parent", getObjectUnderTest(),
                getResourceTreeNode1().getParent());
        Assert.assertEquals("First child has wrong depth", 1, getResourceTreeNode1()
                .getZeroBasedDepth());

        Assert.assertEquals("Second child has wrong parent", getObjectUnderTest(),
                getResourceTreeNode2().getParent());
        Assert.assertEquals("Second child has wrong depth", 1, getResourceTreeNode2()
                .getZeroBasedDepth());

    }

    @Test
    public void testGetResource() throws Throwable {
        Assert.assertEquals("resource is wrong", getMockResource(), getObjectUnderTest()
                .getResource());
    }

    @Test
    public void testSetGetZeroBasedDepth() throws Throwable {
        final int depth = 19;

        getObjectUnderTest().setZeroBasedDepth(depth);

        Assert.assertEquals("zeroBasedDepth is wrong", depth, getObjectUnderTest()
                .getZeroBasedDepth());

    }

    @Test
    public void testGetTypeDescription() throws Throwable {
        Assert.assertEquals("typeDescription is wrong", getExpectedTypeDescription(),
                getObjectUnderTest().getTypeDescription());

    }

    protected abstract String getExpectedTypeDescription();

    /**
     * @return the objectUnderTest
     */
    private AbstractResourceTreeNode getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final AbstractResourceTreeNode objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    protected abstract AbstractResourceTreeNode createObjectUnderTest(Resource resource);
    protected abstract AbstractResourceTreeNode createResourceTreeNode1(Resource resource);
    protected abstract AbstractResourceTreeNode createResourceTreeNode2(Resource resource);

    /**
     * @return the resourceTreeNode1
     */
    private AbstractResourceTreeNode getResourceTreeNode1() {
        return resourceTreeNode1;
    }

    /**
     * @param resourceTreeNode1 the resourceTreeNode1 to set
     */
    private void setResourceTreeNode1(final AbstractResourceTreeNode resourceTreeNode1) {
        this.resourceTreeNode1 = resourceTreeNode1;
    }

    /**
     * @return the resourceTreeNode2
     */
    private AbstractResourceTreeNode getResourceTreeNode2() {
        return resourceTreeNode2;
    }

    /**
     * @param resourceTreeNode2 the resourceTreeNode2 to set
     */
    private void setResourceTreeNode2(final AbstractResourceTreeNode resourceTreeNode2) {
        this.resourceTreeNode2 = resourceTreeNode2;
    }

    /**
     * @return the mockResource
     */
    public Resource getMockResource() {
        return mockResource;
    }

    /**
     * @param mockResource the mockResource to set
     */
    public void setMockResource(final Resource mockResource) {
        this.mockResource = mockResource;
    }
}

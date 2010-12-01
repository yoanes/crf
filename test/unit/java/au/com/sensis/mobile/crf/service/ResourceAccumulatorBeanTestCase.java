package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;
import au.com.sensis.mobile.crf.debug.ResourceTreeNode;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;


/**
 * Test the {@link ResourceAccumulatorBean}.
 *
 * @author Tony Filipe
 */
public class ResourceAccumulatorBeanTestCase
extends AbstractJUnit4TestCase {

    private ResourceAccumulatorBean objectUnderTest;
    private List<Resource> resolvedPaths;
    private ResourcePathTestData testData;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new ResourceAccumulatorBean());

        testData = new ResourcePathTestData();
        resolvedPaths = new ArrayList<Resource>();
        resolvedPaths.add(testData.getMappedDefaultGroupNamedScriptResourcePath());
        resolvedPaths.add(testData.getMappedDefaultGroupNamedScriptResourcePath2());

        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree(true));
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree());
    }

    // Tests //

    @Test
    public void testConstructor() throws Throwable {

        Assert.assertNotNull(getObjectUnderTest().getResources());
    }

    @Test
    public void testAccumulate() throws Throwable {

        getObjectUnderTest().accumulate(resolvedPaths);

        // We expect the order to be reversed, so that most generic is output first
        Collections.reverse(resolvedPaths);

        Assert.assertEquals(resolvedPaths, getObjectUnderTest().getResources());

        assertResourceResolutionTreeUpdated();
    }

    private void assertResourceResolutionTreeUpdated() {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        Assert.assertTrue("ResourceResolutionTree treePreOrderIterator should have a next item",
                treePreOrderIterator.hasNext());
        ResourceTreeNode resourceTreeNode = treePreOrderIterator.next();
        Assert.assertNotNull("first item from preOrderIterator should not be null",
                resourceTreeNode);
        Assert.assertEquals("first item from preOrderIterator has wrong resource", resolvedPaths
                .get(0), resourceTreeNode.getResource());

        Assert.assertTrue("ResourceResolutionTree treePreOrderIterator should have a second next "
                + "item", treePreOrderIterator.hasNext());
        resourceTreeNode = treePreOrderIterator.next();
        Assert.assertNotNull("second item from preOrderIterator should not be null",
                resourceTreeNode);
        Assert.assertEquals("secnod item from preOrderIterator has wrong resource", resolvedPaths
                .get(1), resourceTreeNode.getResource());
    }


    @Test
    public void testEquals() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);

        // Create an accumulator the same as the one under test
        final ResourceAccumulatorBean accumulator = new ResourceAccumulatorBean();
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertEquals(accumulator, getObjectUnderTest());
    }

    @Test
    public void testEqualsWhenNotEqual() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);
        sameResolvedPaths.add(testData.getMappedAppleGroupNamedScriptResourcePath());

        // Create an accumulator the same as the one under test
        final ResourceAccumulatorBean accumulator = new ResourceAccumulatorBean();
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertFalse(accumulator.equals(getObjectUnderTest()));
    }

    @Test
    public void testEqualsWhenDifferentObj() throws Throwable {

        Assert.assertFalse(new BundleResourceAccumulatorBean().equals(getObjectUnderTest()));
    }

    @Test
    public void testHashCode() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);

        // Create an accumulator the same as the one under test
        final ResourceAccumulatorBean accumulator = new ResourceAccumulatorBean();
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertEquals(accumulator.hashCode(), getObjectUnderTest().hashCode());
    }

    @Test
    public void testHashCodeWithDifferentObject() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);
        sameResolvedPaths.add(testData.getMappedAppleGroupNamedScriptResourcePath());

        // Create an accumulator the same as the one under test
        final ResourceAccumulatorBean accumulator = new ResourceAccumulatorBean();
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertFalse(accumulator.hashCode() == getObjectUnderTest().hashCode());
    }

    @Test
    public void testToString() throws Throwable {

        getObjectUnderTest().accumulate(resolvedPaths);

        // We expect the order to be reversed, so that most generic is output first
        Collections.reverse(resolvedPaths);

        Assert.assertEquals(resolvedPaths.toString(), getObjectUnderTest().toString());
    }

    @Test
    public void testIsBundlingEnabledFalse() throws Throwable {

        Assert.assertFalse("isBundlingEnabled() should be false", getObjectUnderTest()
                .isBundlingEnabled());
    }

    /**
     * @return the objectUnderTest
     */
    public ResourceAccumulatorBean getObjectUnderTest() {

        return objectUnderTest;
    }

    /**
     * @param objectUnderTest  the objectUnderTest to set
     */
    public void setObjectUnderTest(final ResourceAccumulatorBean objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }

}

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
 * Test the {@link JavaScriptResourceAccumulatorBean}.
 *
 * @author Tony Filipe
 */
public class JavaScriptResourceAccumulatorBeanTestCase
extends AbstractJUnit4TestCase {

    private static final String PACKAGE_KEYWORD = "package";
    private JavaScriptResourceAccumulatorBean objectUnderTest;
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

        setObjectUnderTest(createJavaScriptResourceAccumulatorBean(false));

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
    public void testAccumulateOverridesSameFilename() throws Throwable {

        // Matched 2 different versions of the same Javascript file
        final List<Resource> passedInResources = new ArrayList<Resource>();
        passedInResources.add(testData.getMappedDefaultGroupNamedScriptResourcePath());
        passedInResources.add(testData.getMappedAppleGroupNamedScriptResourcePath());

        // Expect to only return the most specific version of the Javascript file
        final List<Resource> expectedAccumulatedResources = new ArrayList<Resource>();
        expectedAccumulatedResources.add(testData.getMappedAppleGroupNamedScriptResourcePath());

        getObjectUnderTest().accumulate(passedInResources);

        Assert.assertEquals(expectedAccumulatedResources, getObjectUnderTest().getResources());
    }

    @Test
    public void testAccumulateOverridesPackages() throws Throwable {

        final List<Resource> mostSpecificResources = new ArrayList<Resource>();
        mostSpecificResources.add(
                testData.getMappedIphoneGroupBundledScriptResourcePath1());
        mostSpecificResources.add(
                testData.getMappedIphoneGroupBundledScriptResourcePath2());

        // More generic version of same resources
        final List<Resource> lessSpecificResources = new ArrayList<Resource>();
        lessSpecificResources.add(
                testData.getMappedAppleGroupBundledScriptResourcePath1());
        lessSpecificResources.add(
                testData.getMappedAppleGroupBundledScriptResourcePath2());

        getObjectUnderTest().accumulate(mostSpecificResources);
        getObjectUnderTest().accumulate(lessSpecificResources);

        // We expect the order to be reversed, so that most generic is output first
        Collections.reverse(mostSpecificResources);

        Assert.assertEquals(mostSpecificResources, getObjectUnderTest().getResources());
    }

    @Test
    public void testAccumulateReturnsAllUnique() throws Throwable {


        // Matches 2 different versions of the same Javascript files
        final List<Resource> passedInResources = new ArrayList<Resource>();
        passedInResources.add(testData.getMappedDefaultGroupNamedScriptResourcePath());
        passedInResources.add(testData.getMappedDefaultGroupNamedScriptResourcePath2());
        passedInResources.add(testData.getMappedIphoneGroupNamedScriptResourcePath());
        passedInResources.add(testData.getMappedIphoneGroupNamedScriptResourcePath2());

        // Expect to only return the most specific version of the Javascript file
        final List<Resource> expectedAccumulatedResources = new ArrayList<Resource>();
        expectedAccumulatedResources.add(
                testData.getMappedIphoneGroupNamedScriptResourcePath());
        expectedAccumulatedResources.add(
                testData.getMappedIphoneGroupNamedScriptResourcePath2());

        getObjectUnderTest().accumulate(passedInResources);

        Assert.assertEquals(expectedAccumulatedResources, getObjectUnderTest().getResources());
    }

    @Test
    public void testEquals() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);

        // Create an accumulator the same as the one under test
        final JavaScriptResourceAccumulatorBean accumulator =
            createJavaScriptResourceAccumulatorBean(false);
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertEquals(accumulator, getObjectUnderTest());
    }

    @Test
    public void testEqualsWhenNotEqual() throws Throwable {

        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);
        sameResolvedPaths.add(testData.getMappedAppleGroupNamedScriptResourcePath());

        final JavaScriptResourceAccumulatorBean accumulator =
            createJavaScriptResourceAccumulatorBean(false);
        accumulator.accumulate(sameResolvedPaths);

        getObjectUnderTest().accumulate(resolvedPaths);

        Assert.assertFalse(accumulator.equals(getObjectUnderTest()));
    }

    @Test
    public void testEqualsWhenDifferentObj() throws Throwable {

        Assert.assertFalse(new ResourceAccumulatorBean().equals(getObjectUnderTest()));
    }

    @Test
    public void testHashCode() throws Throwable {

        // Clone the resources (so not affected by the reversing)
        final List<Resource> sameResolvedPaths = new ArrayList<Resource>(resolvedPaths);

        // Create an accumulator the same as the one under test
        final JavaScriptResourceAccumulatorBean accumulator =
            new JavaScriptResourceAccumulatorBean(PACKAGE_KEYWORD, false);
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

    private JavaScriptResourceAccumulatorBean createJavaScriptResourceAccumulatorBean(
            final boolean bundling) {

        return new JavaScriptResourceAccumulatorBean(PACKAGE_KEYWORD, bundling);
    }

    /**
     * @return the objectUnderTest
     */
    public JavaScriptResourceAccumulatorBean getObjectUnderTest() {

        return objectUnderTest;
    }

    /**
     * @param objectUnderTest  the objectUnderTest to set
     */
    public void setObjectUnderTest(final JavaScriptResourceAccumulatorBean objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }

}

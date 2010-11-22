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
 * Unit test {@link JspSingleResourceAccumulatorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspSingleResourceAccumulatorBeanTestCase extends AbstractJUnit4TestCase {

    private JspSingleResourceAccumulatorBean objectUnderTest;
    private List<Resource> resolvedPaths;
    private ResourcePathTestData testData;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new JspSingleResourceAccumulatorBean());

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

    @Test
    public void testAccumulate() throws Throwable {

        getObjectUnderTest().accumulate(resolvedPaths);

        // We expect the order to be reversed, so that most generic is output first
        Collections.reverse(resolvedPaths);

        Assert.assertEquals(resolvedPaths, getObjectUnderTest().getResources());

        assertResourceResolutionTreeNotUpdated();
    }

    private void assertResourceResolutionTreeNotUpdated() {
        final Iterator<ResourceTreeNode> treePreOrderIterator =
                ResourceResolutionTreeHolder.getResourceResolutionTree().preOrderIterator();

        Assert.assertFalse("ResourceResolutionTree treePreOrderIterator should not have any items",
                treePreOrderIterator.hasNext());
    }


    /**
     * @return the objectUnderTest
     */
    private JspSingleResourceAccumulatorBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JspSingleResourceAccumulatorBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}

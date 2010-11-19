package au.com.sensis.mobile.crf.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;


/**
 * Test the {@link ResourceAccumulatorFactory}.
 *
 * @author Tony Filipe
 */
public class ResourceAccumulatorFactoryTestCase
extends AbstractJUnit4TestCase {

    private ResourceAccumulatorFactory objectUnderTest;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(createResourceAccumulatorFactory(false));
    }

    @Test
    public void testGetJspResourceAccumulator() throws Throwable {

        final ResourceAccumulator accumulator = getObjectUnderTest().getJspResourceAccumulator();

        Assert.assertTrue(accumulator instanceof SingleResourceAccumulatorBean);
    }

    @Test
    public void testGetImageResourceAccumulator() throws Throwable {

        final ResourceAccumulator accumulator = getObjectUnderTest().getImageResourceAccumulator();

        Assert.assertTrue(accumulator instanceof SingleResourceAccumulatorBean);
    }

    @Test
    public void testGetPropertiesResourceAccumulator() throws Throwable {

        final ResourceAccumulator accumulator =
            getObjectUnderTest().getPropertiesResourceAccumulator();

        Assert.assertTrue(accumulator instanceof ResourceAccumulatorBean);
    }

    @Test
    public void testGetJavaScriptResourceAccumulator() throws Throwable {

        final ResourceAccumulator accumulator =
            getObjectUnderTest().getJavaScriptResourceAccumulator("packageKeyword");

        Assert.assertTrue(accumulator instanceof JavaScriptResourceAccumulatorBean);
        Assert.assertFalse(
                ((JavaScriptResourceAccumulatorBean) accumulator).isBundlingEnabled());
    }

    @Test
    public void testGetJavaScriptResourceAccumulatorBundlingEnabled() throws Throwable {

        setObjectUnderTest(createResourceAccumulatorFactory(true));

        final ResourceAccumulator accumulator =
            getObjectUnderTest().getJavaScriptResourceAccumulator("packageKeyword");

        Assert.assertTrue(accumulator instanceof JavaScriptResourceAccumulatorBean);
        Assert.assertTrue(
                ((JavaScriptResourceAccumulatorBean) accumulator).isBundlingEnabled());
    }

    @Test
    public void testGetCSSResourceAccumulator() throws Throwable {

        final ResourceAccumulator accumulator =
            getObjectUnderTest().getCSSResourceAccumulator();

        Assert.assertTrue(accumulator instanceof ResourceAccumulatorBean);
    }

    @Test
    public void testGetCSSResourceAccumulatorBundlingEnabled() throws Throwable {

        setObjectUnderTest(createResourceAccumulatorFactory(true));

        final ResourceAccumulator accumulator =
            getObjectUnderTest().getCSSResourceAccumulator();

        Assert.assertTrue(accumulator instanceof BundleResourceAccumulatorBean);
    }

    private ResourceAccumulatorFactory createResourceAccumulatorFactory(final boolean bundling) {

        return new ResourceAccumulatorFactory(bundling);
    }

    /**
     * @return the objectUnderTest
     */
    public ResourceAccumulatorFactory getObjectUnderTest() {

        return objectUnderTest;
    }

    /**
     * @param objectUnderTest  the objectUnderTest to set
     */
    public void setObjectUnderTest(final ResourceAccumulatorFactory objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }
}

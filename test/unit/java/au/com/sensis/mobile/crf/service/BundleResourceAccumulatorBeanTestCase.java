package au.com.sensis.mobile.crf.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link BundleResourceAccumulatorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class BundleResourceAccumulatorBeanTestCase extends AbstractJUnit4TestCase {

    private BundleResourceAccumulatorBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new BundleResourceAccumulatorBean());
    }

    @Test
    public void testIsBundlingEnabledTrue() throws Throwable {

        Assert.assertTrue("isBundlingEnabled() should be true", getObjectUnderTest()
                .isBundlingEnabled());
    }

    /**
     * @return the objectUnderTest
     */
    private BundleResourceAccumulatorBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final BundleResourceAccumulatorBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }


}

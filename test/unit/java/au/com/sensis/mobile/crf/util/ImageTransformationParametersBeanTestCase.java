package au.com.sensis.mobile.crf.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImageTransformationParametersBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTransformationParametersBeanTestCase extends AbstractJUnit4TestCase {

    private ImageTransformationParametersBean objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ImageTransformationParametersBean());

        getObjectUnderTest().setDevicePixelWidth(1);
    }

    @Test
    public void testScaleToPercentDeviceWidth() throws Throwable {
        getObjectUnderTest().setDeviceImagePercentWidth(1);

        Assert.assertTrue("scaleToPercentDeviceWidth() should be true", getObjectUnderTest()
                .scaleToPercentDeviceWidth());

    }

    @Test
    public void testScaleToAbsolutePixelWidth() throws Throwable {
        getObjectUnderTest().setDeviceImagePercentWidth(1);
        getObjectUnderTest().setAbsolutePixelWidth(1);

        Assert.assertTrue("scaleToAbsolutePixelWidth() should be true", getObjectUnderTest()
                .scaleToAbsolutePixelWidth());

    }

    @Test
    public void testPreserveOriginalDimensions() throws Throwable {

        Assert.assertTrue("preserveOriginalDimensions() should be true", getObjectUnderTest()
                .preserveOriginalDimensions());

    }

    @Test
    public void testCalculateOutputImagePixelWidth() throws Throwable {


    }

    /**
     * @return the objectUnderTest
     */
    private ImageTransformationParametersBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ImageTransformationParametersBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }


}

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

// TODO: not sure if there should be an explicit validate or whether we should have fail-fast
// validation in setters.
//    @Test
//    public void testValidateWhenDeviceImagePercentWidthInvalid() throws Throwable {
//        getObjectUnderTest().setDeviceImagePercentWidth(-1);
//
//        try {
//            getObjectUnderTest().validate();
//
//            Assert.fail("IllegalStateException expected");
//
//        } catch (final IllegalStateException e) {
//
//            Assert.assertEquals("IllegalStateException has wrong message",
//                    "deviceImagePercentWidth must be >= 0: -1", e.getMessage());
//        }
//
//    }
//
//    @Test
//    public void testValidateWhenDevicePixelWidthInvalid() throws Throwable {
//        getObjectUnderTest().setDevicePixelWidth(0);
//
//        try {
//            getObjectUnderTest().validate();
//
//            Assert.fail("IllegalStateException expected");
//
//        } catch (final IllegalStateException e) {
//
//            Assert.assertEquals("IllegalStateException has wrong message",
//                    "devicePixelWidth must be > 0: 0", e.getMessage());
//        }
//
//    }

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

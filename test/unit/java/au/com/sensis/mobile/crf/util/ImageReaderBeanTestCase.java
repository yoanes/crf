package au.com.sensis.mobile.crf.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ImageReaderBean}.
 *
 * @author w12495
 */
public class ImageReaderBeanTestCase extends AbstractJUnit4TestCase {

    private ImageReaderBean objectUnderTest;

    private File gifFileWithPngExtension;
    private File gifFileWithCorrectExtension;
    private File invalidImageFile;
    private File svgImageFile;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new ImageReaderBean());

        setGifFileWithCorrectExtension(new ClassPathResource(
                "au/com/sensis/mobile/crf/util/imageReaderBeanTestData/myplaces_ok.gif").getFile());
        setGifFileWithPngExtension(new ClassPathResource(
                "au/com/sensis/mobile/crf/util/imageReaderBeanTestData/"
                        + "gif_with_png_file_extension.png").getFile());
        setInvalidImageFile(new ClassPathResource(
                "au/com/sensis/mobile/crf/util/imageReaderBeanTestData/invalid_image.png")
                    .getFile());
        setSvgImageFile(new ClassPathResource(
                "au/com/sensis/mobile/crf/util/imageReaderBeanTestData/an_svg_image.svg")
                    .getFile());
    }

    @Test
    public void testReadImageAttributesOfGifFileHavingCorrectExtension() throws Exception {
        final ImageAttributes imageAttributes = getObjectUnderTest().readImageAttributes(
                getGifFileWithCorrectExtension());

        Assert.assertEquals("width of image incorrectly read", 42, imageAttributes.getPixelWidth());
        Assert.assertEquals("height of image incorrectly read", 32, imageAttributes
                .getPixelHeight());
    }

    @Test
    public void testReadImageAttributesOfGifFileHavingPngExtension() throws Exception {
        final ImageAttributes imageAttributes = getObjectUnderTest().readImageAttributes(
                getGifFileWithPngExtension());

        Assert.assertEquals("width of image incorrectly read", 42, imageAttributes.getPixelWidth());
        Assert.assertEquals("height of image incorrectly read", 32, imageAttributes
                .getPixelHeight());
    }

    @Test
    public void testReadImageAttributesOfInvalidImageFile() throws Exception {
        final ImageAttributes imageAttributes = getObjectUnderTest().readImageAttributes(
                getInvalidImageFile());

        Assert.assertEquals("width of invalid image should be 0 since it cannot be determined", 0,
                imageAttributes.getPixelWidth());
        Assert.assertEquals("height of invalid image should be 0 since it cannot be determined", 0,
                imageAttributes.getPixelHeight());
    }

    @Test
    public void testReadImageAttributesOfSvgImageFile() throws Exception {
        final ImageAttributes imageAttributes = getObjectUnderTest().readImageAttributes(
                getSvgImageFile());

        Assert.assertEquals("width of svg image should be 0 since it cannot be determined", 0,
                imageAttributes.getPixelWidth());
        Assert.assertEquals("height of svg image should be 0 since it cannot be determined", 0,
                imageAttributes.getPixelHeight());
    }

    private File getGifFileWithPngExtension() {
        return gifFileWithPngExtension;
    }

    private void setGifFileWithPngExtension(final File gifFileWithPngExtension) {
        this.gifFileWithPngExtension = gifFileWithPngExtension;
    }

    private File getGifFileWithCorrectExtension() {
        return gifFileWithCorrectExtension;
    }

    private void setGifFileWithCorrectExtension(final File gifFileWithCorrectExtension) {
        this.gifFileWithCorrectExtension = gifFileWithCorrectExtension;
    }

    private ImageReaderBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final ImageReaderBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private File getInvalidImageFile() {
        return invalidImageFile;
    }

    private void setInvalidImageFile(final File invalidImageFile) {
        this.invalidImageFile = invalidImageFile;
    }

    private File getSvgImageFile() {
        return svgImageFile;
    }

    private void setSvgImageFile(final File svgImageFile) {
        this.svgImageFile = svgImageFile;
    }

}

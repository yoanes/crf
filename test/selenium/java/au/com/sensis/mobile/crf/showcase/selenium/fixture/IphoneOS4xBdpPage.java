package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class IphoneOS4xBdpPage extends IphoneOS3xBdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public IphoneOS4xBdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertImg() {
        assertImgFoundInDefaultGroupPngFormat();
        assertImgFoundInIntermediateGroupPngFormat();

        assertScaledYellowPagesImage(640, 256, getScaledImageFormat());
        assertScaledYellowPagesImagePath(640, 256, getScaledImageFormat());

        assertScaledSearchImage(225, 268, getScaledImageFormat(), "default");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertScaledYellowPagesImage(final int width, final int height,
            final String extension) {
        assertImg("Yellow Pages img not found", "yellowPagesLogoImg", "Yellow Pages",
                "Yellow Pages", "default/selenium/common/w" + width + "/h" + height
                        + "/yellow-pages." + extension, width / getImageDimensionsDivisor(), height
                        / getImageDimensionsDivisor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertScaledSearchImage(final int width, final int height,
            final String extension, final String group) {
        assertImg("Search img not found", "searchImg", "Search", "Search", group
                + "/selenium/common/w" + width + "/h" + height + "/search." + extension, width
                / getImageDimensionsDivisor(), height / getImageDimensionsDivisor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertPageStructure() {
        super.doAssertPageStructure();
        assertInAllGroupsJspSelection();
    }

    private void assertInAllGroupsJspSelection() {
        assertTrue(getBrowser().isTextPresent("[clickToCallSupported-hd640] clickToCall.jsp"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getImageDimensionsDivisor() {
        // Image dimensions should be divided by 2 for iPhone 4.
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertImageCategoryText() {
        assertTrue("imageCategory is wrong",
                getBrowser().isTextPresent("'custom.imageCategory': HD640"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertImagesDifferentiatedWhenClosestWidthFolderContainsADifferentImage() {
        assertImg("32pxSourceImage img not found", "32pxSourceImage",
                "32pxSourceImage", "32pxSourceImage",
                "default/selenium/common/w32/h32/32px-source-image." + getScaledImageFormat(),
                16, 16);
        assertImg("44pxSourceImage img not found", "44pxSourceImage",
                "44pxSourceImage", "44pxSourceImage",
                "default/selenium/common/w44/h44/44px-source-image." + getScaledImageFormat(),
                22, 22);
    }
}

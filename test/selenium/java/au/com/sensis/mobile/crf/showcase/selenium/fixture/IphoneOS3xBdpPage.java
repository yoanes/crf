package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class IphoneOS3xBdpPage extends IphoneBdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public IphoneOS3xBdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertImg() {
        assertIphoneOS3xImg();

    }

    protected void assertIphoneOS3xImg() {
        assertImgFoundInDefaultGroupPngFormat();
        assertImgFoundInIntermediateGroupPngFormat();

        assertScaledYellowPagesImage(320, 128, "png");
        assertScaledYellowPagesImagePath(320, 128, "png");
        assertScaledSearchImage(55, 65, "png", "default");
    }

    protected void assertImgFoundInDefaultGroupPngFormat() {
        assertImg("unmetered img not found", "unmeteredImg", "Unmetered", "Unmetered",
                "default/selenium/common/unmetered.png", 310 / getImageDimensionsDivisor(),
                42 / getImageDimensionsDivisor());
    }

    protected void assertImgFoundInIntermediateGroupPngFormat() {
        assertImg("appStore img not found", "appStoreImg", "App Store", "App Store",
                "applewebkit/selenium/common/app_store.png", 244 / getImageDimensionsDivisor(),
                80 / getImageDimensionsDivisor());
    }

    /**
     * @return number of expected img elements.
     */
    @Override
    protected int getExpectedNumImgElements() {
        return super.getExpectedNumImgElements() + 4;
    }
}

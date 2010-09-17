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

    private void assertIphoneOS3xImg() {
        assertImg("unmetered img not found",
                "unmeteredImg", "Unmetered", "Unmetered",
                "default/common/unmetered.png");
        assertImg("appStore img not found",
                "appStoreImg", "App Store", "App Store",
                "applewebkit/common/app_store.png");
    }

    /**
     * @return number of expected img elements.
     */
    @Override
    protected int getExpectedNumImgElements() {
        return super.getExpectedNumImgElements() + 2;
    }
}

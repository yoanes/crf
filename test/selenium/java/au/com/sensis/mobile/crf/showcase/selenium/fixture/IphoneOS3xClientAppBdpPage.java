package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class IphoneOS3xClientAppBdpPage extends IphoneOS3xBdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public IphoneOS3xClientAppBdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertCssLinks() {
        assertCssLink("iphone-clientapp/selenium/common/main.css link not found",
            "iphone-clientapp/selenium/common/main.css");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getExpectedNumIphoneCssLinks() {
        return super.getExpectedNumIphoneCssLinks() + 1;
    }
}

package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class HD800BdpPage extends BdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public HD800BdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertPageStructure() {
        assertHD800Css();
        assertHD800Scripts();
        assertHD800Jsp();
        assertHD800Img();
        assertDeviceProperties();
    }

    private void assertHD800Css() {
        assertNumCssLinks(2);
        assertCssLink("main.css link not found", "default/selenium/common/main.css");
        assertCssLink("results.css link not found", "default/selenium/results/results.css");
    }

    private void assertHD800Scripts() {
        final int expectedNumHD800Scripts = 7;
        assertNumScripts(expectedNumHD800Scripts + super.getNumExpectedScripts());

        assertHD800ScriptsByNameNoBundling();
        assertHD800ScriptsByAllNoBundling();
    }

    private void assertHD800ScriptsByAllNoBundling() {
        assertScript("default/selenium/fielddecorators/decorator2.js script not found",
                "default/selenium/fielddecorators/decorator2.js");
        assertScript("default/selenium/fielddecorators/decorator1.js script not found",
                "default/selenium/fielddecorators/decorator1.js");
        assertScript("default/selenium/fielddecorators/decorator3.js script not found",
                "default/selenium/fielddecorators/decorator3.js");

        assertScript("default/selenium/component/map/map1.js script not found",
                "default/selenium/component/map/map1.js");
        assertScript("default/selenium/component/map/map2.js script not found",
                "default/selenium/component/map/map2.js");
    }

    private void assertHD800ScriptsByNameNoBundling() {
        assertScript("main.js script not found", "default/selenium/common/main.js");
        assertScript("results.js script not found", "default/selenium/results/results.js");
    }

    private void assertHD800Jsp() {
        assertTrue(getBrowser().isTextPresent("[HD800] bdp.jsp"));
    }

    private void assertHD800Img() {
        final int expectedNumImages = 3;
        assertNumImgElements(expectedNumImages);
        assertImgWhenDefaultGroupNodeImageFound();
        assertBrokenImgWhenNoImageFound();
        assertBodyContentImgWhenImgFound();
    }

    private void assertImgWhenDefaultGroupNodeImageFound() {
        assertImg("unmetered img not found",
                "unmeteredImg", "Unmetered", "Unmetered",
                "default/selenium/common/unmetered.png");
    }

    private void assertBrokenImgWhenNoImageFound() {
        assertBrokenImg("App Store (broken) img not found",
                "appStoreImg", "App Store", "App Store",
                "selenium/common/app_store.image");
    }

    private void assertBodyContentImgWhenImgFound() {
        assertImg("wm img not found",
                "wherisMobileImg", "Whereis Mobile", "Whereis Mobile",
                "HD800/selenium/common/wm.gif");
        assertFalse("WM text should not be present resulting from img body content",
                getBrowser().isTextPresent("WM"));

    }

    private void assertDeviceProperties() {
        assertTrue(getBrowser().isTextPresent("'brwsrname': Mozilla"));
        assertTrue(getBrowser().isTextPresent("'custom.imageCategory': HD800"));
    }
}

package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class Nokia7600BdpPage extends BdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public Nokia7600BdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertPageStructure() {
        assertNokia7600Css();
        assertNokia7600Script();
        assertNokia7600Jsp();
        assertNokia7600Img();
        assertDeviceProperties();
    }

    private void assertNokia7600Css() {
        final int expectedNumNokia7600CssLinks = 2;
        assertNumCssLinks(expectedNumNokia7600CssLinks);
        assertOnlyDefaultGroupCssResolved();
    }

    private void assertOnlyDefaultGroupCssResolved() {
        assertCssLink("main.css link not found", "default/selenium/common/main.css");
        assertCssLink("results.css link not found", "default/selenium/results/results.css");

        /* TODO: implementation of assertCssLinkNotPresent just does not work. */
/*
        assertCssLinkNotPresent("jazz.css link should not be found", "jazz\\.css$");
        assertCssLinkNotPresent("decorations.css link should not be found", "decorations\\.css$");
        assertCssLinkNotPresent("columns.css link should not be found", "columns\\.css$");
*/
    }

    private void assertNokia7600Script() {
        final int expectedNumNokia7600Scripts = 7;
        assertNumScripts(expectedNumNokia7600Scripts + super.getNumExpectedScripts());

        assertNokia7600ScriptByNameNoBundling();
        assertNokia7600ScriptByAllNoBundling();
    }

    private void assertNokia7600ScriptByAllNoBundling() {
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

    private void assertNokia7600ScriptByNameNoBundling() {
        assertScript("main.js script not found", "default/selenium/common/main.js");
        assertScript("results.js script not found", "default/selenium/results/results.js");
    }

    private void assertNokia7600Jsp() {
        assertTrue(getBrowser().isTextPresent("[default] bdp.jsp"));
    }

    private void assertNokia7600Img() {
        assertNumImgElements(2);
        assertImgWhenLeafGroupNodeImageFoundGifFormat();
        assertBrokenImgWhenNoImageFound();
        assertBodyContentOutputWhenDotNullImgFound();
    }

    private void assertImgWhenLeafGroupNodeImageFoundGifFormat() {
        assertImg("unmetered img not found",
                "unmeteredImg", "Unmetered", "Unmetered",
                "nokia7600/selenium/common/unmetered.gif", 115, 15);
    }

    private void assertBrokenImgWhenNoImageFound() {
        assertBrokenImg("App Store (broken) img not found",
                "appStoreImg", "App Store", "App Store",
                "selenium/common/app_store.image");
    }

    private void assertBodyContentOutputWhenDotNullImgFound() {
        assertTrue("WM text should be present resulting from img body content",
                getBrowser().isTextPresent("WM"));
    }

    private void assertDeviceProperties() {
        assertTrue(getBrowser().isTextPresent("'brwsrname': Nokia"));
        assertTrue(getBrowser().isTextPresent("'custom.imageCategory': S"));
    }

}

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
        assertLeafGroupJsp();
        assertHD800Img();
        assertDeviceProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getScaledImageFormat() {

        return "png";
    }

    private void assertHD800Css() {
        assertNumCssLinks(super.getNumExpectedLinks() + 2);
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

    private void assertHD800Scripts() {
        final int expectedNumHD800Scripts = 9;
        assertNumScripts(expectedNumHD800Scripts + super.getNumExpectedScripts());

        assertHD800ScriptsByName();
        assertHD800ScriptsByPackageNoBundling();
    }

    private void assertHD800ScriptsByName() {
        assertOnlyDefaultGroupScriptsResolvedByNameNoBundilng();
    }

    private void assertHD800ScriptsByPackageNoBundling() {
        assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithArbitraryOrder();
        assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithPartiallyDefinedOrder();
        assertMapComponentScriptsResolvedByPackageNoBundlingWithArbitraryOrder();
    }

    private void assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithArbitraryOrder() {
        assertScript("default/selenium/reporting/default-reporting1.js script not found",
        "default/selenium/reporting/default-reporting1.js");
        assertScript("default/selenium/reporting/default-reporting2.js script not found",
        "default/selenium/reporting/default-reporting2.js");
    }

    private void
    assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithPartiallyDefinedOrder() {

        assertScript("default/selenium/fielddecorators/decorator2.js script not found",
        "default/selenium/fielddecorators/decorator2.js");
        assertScript("default/selenium/fielddecorators/decorator1.js script not found",
        "default/selenium/fielddecorators/decorator1.js");
        assertScript("default/selenium/fielddecorators/decorator3.js script not found",
        "default/selenium/fielddecorators/decorator3.js");
    }

    private void assertMapComponentScriptsResolvedByPackageNoBundlingWithArbitraryOrder() {
        assertScript("default/selenium/component/map/map1.js script not found",
        "default/selenium/component/map/map1.js");
        assertScript("default/selenium/component/map/map2.js script not found",
        "default/selenium/component/map/map2.js");
    }

    private void assertOnlyDefaultGroupScriptsResolvedByNameNoBundilng() {
        assertScript("main.js script not found", "default/selenium/common/main.js");
        assertScript("results.js script not found", "default/selenium/results/results.js");
    }

    private void assertLeafGroupJsp() {
        assertTrue("[HD800] bdp.jsp couldn't be found",
                getBrowser().isTextPresent("[HD800] bdp.jsp"));
    }

    private void assertHD800Img() {
        final int expectedNumImages = 5 + super.getNumExpectedImages();
        assertNumImgElements(expectedNumImages);
        assertImgWhenDefaultGroupNodeImageFoundPngFormat();
        assertBrokenImgWhenNoImageFound();

        assertBodyContentIgnoredWhenImgFound();
        assertImgPathWhenImgFound();

        assertScaledYellowPagesImage(620, 248, getScaledImageFormat());
        assertScaledYellowPagesImagePath(620, 248, getScaledImageFormat());
        assertScaledSearchImage(55, 65, getScaledImageFormat(), "default");
    }


    private void assertImgWhenDefaultGroupNodeImageFoundPngFormat() {
        assertImg("unmetered img not found",
                "unmeteredImg", "Unmetered", "Unmetered",
                "default/selenium/common/unmetered.png", 310, 42);
    }

    private void assertBrokenImgWhenNoImageFound() {
        assertBrokenImg("App Store (broken) img not found",
                "appStoreImg", "App Store", "App Store",
        "selenium/common/app_store.image");
    }

    private void assertBodyContentIgnoredWhenImgFound() {
        assertImg("wm img not found",
                "wherisMobileImg", "Whereis Mobile", "Whereis Mobile",
                "HD800/selenium/common/wm.gif", 79, 75);
        assertFalse("WM text should not be present resulting from img body content",
                getBrowser().isTextPresent("WM"));

    }

    private void assertImgPathWhenImgFound() {
        assertImgPath("wm img path not found", "HD800/selenium/common/wm.gif");
    }

    private void assertDeviceProperties() {
        assertTrue(getBrowser().isTextPresent("'brwsrname': Mozilla"));
        assertTrue(getBrowser().isTextPresent("'custom.imageCategory': HD800"));
    }
}

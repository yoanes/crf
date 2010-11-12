package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class IphoneBdpPage extends BdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public IphoneBdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertAppProperty1LoadedFromMainPropertiesFile() {
        assertTrue("app.property1 not found on page",
                getBrowser().isTextPresent("'app.property1': appProperty1IponeIpodValue"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertPageStructure() {
        assertIphoneCss();
        assertIphoneScript();
        assertIphoneJsp();
        assertImg();
        assertMapText();
        assertDeviceProperties();
    }

    private void assertMapText() {
        assertTrue("Map not found", getBrowser().isTextPresent("Map goes here"));
    }

    private void assertImg() {
        assertNumImgElements(getExpectedNumImgElements());
        assertBodyContentIgnoredWhenImgFound();
        assertMapComponentImgFoundPngFormat();
        doAssertImg();
    }

    /**
     * @return number of expected img elements.
     */
    protected int getExpectedNumImgElements() {
        return 2;
    }

    private void assertBodyContentIgnoredWhenImgFound() {
        assertImg("wm img not found",
                "wherisMobileImg", "Whereis Mobile", "Whereis Mobile",
                "iphone-ipod/selenium/common/wm.gif", 60, 90);
        assertFalse("WM text should not be present resulting from img body content",
                getBrowser().isTextPresent("WM"));
    }

    private void assertMapComponentImgFoundPngFormat() {
        assertImg("in.img not found",
                "mapZoomIn", "Map Zoom In", "Map Zoom In",
		        "mapComponent-iphone-ipod/selenium/component/map/in.png", 45, 32);
    }

    /**
     * Subclasses to override to assert images.
     */
    protected abstract void doAssertImg();

    private void assertIphoneCss() {
        final int expectedNumIphoneCssLinks = 10;
        assertNumCssLinks(expectedNumIphoneCssLinks);

        assertCssLinksFoundInLeafGroupUptoDefaultGroup();

        assertCssLinkFoundInLeafGroupOnly();

        assertCssLinkFoundInDefaultGroupOnly();

        assertCssLinkFoundInIntermediateGroupOnly();

        assertCssLinkFoundInMultipleIntermediateGroupsOnly();

        assertCssLinkFoundFromMapComponent();

    }

    private void assertCssLinksFoundInLeafGroupUptoDefaultGroup() {
        assertCssLink("default/selenium/common/main.css link not found",
                "default/selenium/common/main.css");
        assertCssLink("webkit/selenium/common/main.css link not found",
                "webkit/selenium/common/main.css");
        assertCssLink("applewebkit/selenium/common/main.css link not found",
                "applewebkit/selenium/common/main.css");
        assertCssLink("iphone-ipod/selenium/common/main.css link not found",
                "iphone-ipod/selenium/common/main.css");
    }

    private void assertCssLinkFoundInLeafGroupOnly() {
        assertCssLink("iphone-ipod/selenium/common/columns.css link not found",
        "iphone-ipod/selenium/common/columns.css");
    }

    private void assertCssLinkFoundInDefaultGroupOnly() {
        assertCssLink("default/selenium/results/results.css link not found",
        "default/selenium/results/results.css");
    }

    private void assertCssLinkFoundInIntermediateGroupOnly() {
        assertCssLink("webkit/selenium/common/jazz.css link not found",
        "webkit/selenium/common/jazz.css");
    }

    private void assertCssLinkFoundInMultipleIntermediateGroupsOnly() {
        assertCssLink("webkit/selenium/common/decorations.css link not found",
        "webkit/selenium/common/decorations.css");
        assertCssLink("applewebkit/selenium/common/decorations.css link not found",
        "applewebkit/selenium/common/decorations.css");

    }

    private void assertCssLinkFoundFromMapComponent() {
        assertCssLink("mapComponent-iphone-ipod/selenium/component/map/map.css link not found",
        "mapComponent-iphone-ipod/selenium/component/map/map.css");
    }

    private void assertIphoneScript() {
        final int expectedNumIphoneScripts = 17;
        assertNumScripts(expectedNumIphoneScripts + super.getNumExpectedScripts());

        assertIphoneScriptByNameNoBundling();
        assertIphoneScriptByAllNoBundling();
    }

    private void assertIphoneScriptByNameNoBundling() {
        /*assertScript("default/selenium/common/main.js script not found",
                "default/selenium/common/main.js");
        assertScript("webkit/selenium/common/main.js script not found",
                "webkit/selenium/common/main.js");
        assertScript("applewebkit/selenium/common/main.js script not found",
                "applewebkit/selenium/common/main.js");*/
        assertScript("iphone-ipod/selenium/common/main.js script not found",
        "iphone-ipod/selenium/common/main.js");

        assertScript("iphone-ipod/selenium/common/columns.js script not found",
        "iphone-ipod/selenium/common/columns.js");

        assertScript("default/selenium/results/results.js script not found",
        "default/selenium/results/results.js");

        assertScript("webkit/selenium/common/jazz.js script not found",
        "webkit/selenium/common/jazz.js");

        /*assertScript("webkit/selenium/common/decorations.js script not found",
                "webkit/selenium/common/decorations.js");*/
        assertScript("applewebkit/selenium/common/decorations.js script not found",
        "applewebkit/selenium/common/decorations.js");
    }

    private void assertIphoneScriptByAllNoBundling() {
        assertIphoneFielddecoratorScripts();
        assertIphonMapScripts();
        assertIphoneGridScripts();
        assertIphoneAnimationScripts();
        assertIphoneLayersScripts();
    }

    private void assertIphonMapScripts() {
        /*assertScript("default/selenium/component/map/map1.js script not found",
                "default/selenium/component/map/map1.js");
        assertScript("default/selenium/component/map/map2.js script not found",
                "default/selenium/component/map/map2.js");
        assertScript("webkit/selenium/component/map/map1.js script not found",
                "webkit/selenium/component/map/map1.js");
        assertScript("webkit/selenium/component/map/map2.js script not found",
                "webkit/selenium/component/map/map2.js");
        assertScript("applewebkit/selenium/component/map/map1.js script not found",
                "applewebkit/selenium/component/map/map1.js");
        assertScript("applewebkit/selenium/component/map/map2.js script not found",
                "applewebkit/selenium/component/map/map2.js");*/
        assertScript(
                "mapComponent-iphone-ipod/selenium/component/map/map2.js script not found",
        "mapComponent-iphone-ipod/selenium/component/map/map2.js");
        assertScript(
                "mapComponent-iphone-ipod/selenium/component/map/map1.js script not found",
        "mapComponent-iphone-ipod/selenium/component/map/map1.js");
    }

    private void assertIphoneFielddecoratorScripts() {
        assertScript("default/selenium/fielddecorators/decorator2.js script not found",
        "default/selenium/fielddecorators/decorator2.js");
        assertScript("default/selenium/fielddecorators/decorator1.js script not found",
        "default/selenium/fielddecorators/decorator1.js");
        assertScript("default/selenium/fielddecorators/decorator3.js script not found",
        "default/selenium/fielddecorators/decorator3.js");
    }

    private void assertIphoneGridScripts() {
        assertScript("iphone-ipod/selenium/grid/grid2.js script not found",
        "iphone-ipod/selenium/grid/grid2.js");
        assertScript("iphone-ipod/selenium/grid/grid1.js script not found",
        "iphone-ipod/selenium/grid/grid1.js");
        assertScript("iphone-ipod/selenium/grid/grid3.js script not found",
        "iphone-ipod/selenium/grid/grid3.js");
    }

    private void assertIphoneAnimationScripts() {
        assertScript("webkit/selenium/animation/animation1.js script not found",
        "webkit/selenium/animation/animation1.js");
        assertScript("webkit/selenium/animation/animation2.js script not found",
        "webkit/selenium/animation/animation2.js");
    }

    private void assertIphoneLayersScripts() {
        /*assertScript("webkit/selenium/layers/layers1.js script not found",
                "webkit/selenium/layers/layers1.js");
        assertScript("webkit/selenium/layers/layers2.js script not found",
                "webkit/selenium/layers/layers2.js");*/
        assertScript("applewebkit/selenium/layers/layers1.js script not found",
        "applewebkit/selenium/layers/layers1.js");
        assertScript("applewebkit/selenium/layers/layers2.js script not found",
        "applewebkit/selenium/layers/layers2.js");
    }

    private void assertIphoneJsp() {
        assertTrue(getBrowser().isTextPresent("[iphone-ipod] bdp.jsp"));
    }

    private void assertDeviceProperties() {
        assertTrue(getBrowser().isTextPresent("'brwsrname': Safari"));
        assertTrue(getBrowser().isTextPresent("'custom.imageCategory': L"));
    }
}

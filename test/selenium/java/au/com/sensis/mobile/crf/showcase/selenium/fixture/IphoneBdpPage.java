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
        assertIphoneScripts();
        assertLeafGroupJsp();
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
        assertImgPathWhenImgFound();
        assertMapComponentImgFoundPngFormat();
        doAssertImg();
    }

    /**
     * @return number of expected img elements.
     */
    protected int getExpectedNumImgElements() {
        return 2 + super.getNumExpectedImages();
    }

    /**
     * @return Number to divide image dimensions by. Subclasses should override as necessary.
     */
    protected int getImageDimensionsDivisor() {
        return 1;
    }

    private void assertBodyContentIgnoredWhenImgFound() {
        assertImg("wm img not found", "wherisMobileImg", "Whereis Mobile", "Whereis Mobile",
                "iphone-ipod/selenium/common/wm.gif", 60 / getImageDimensionsDivisor(),
                90 / getImageDimensionsDivisor());
        assertFalse("WM text should not be present resulting from img body content", getBrowser()
                .isTextPresent("WM"));
    }

    private void assertImgPathWhenImgFound() {
        assertImgPath("wm img path not found", "iphone-ipod/selenium/common/wm.gif");
    }

    private void assertMapComponentImgFoundPngFormat() {
        assertImg("in.img not found", "mapZoomIn", "Map Zoom In", "Map Zoom In",
                "mapComponent-advanced/selenium/component/map/in.png",
                45 / getImageDimensionsDivisor(), 32 / getImageDimensionsDivisor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertGifImageWithPngFileExtension() {
        assertImg("gifWithPngFileExtension img not found", "gifWithPngFileExtension",
                "gifWithPngFileExtension", "gifWithPngFileExtension",
                "default/selenium/common/gif_with_png_file_extension.png",
                42 / getImageDimensionsDivisor(), 32 / getImageDimensionsDivisor());
    }

    /**
     * Subclasses to override to assert images.
     */
    protected abstract void doAssertImg();

    private void assertIphoneCss() {
        assertNumCssLinks(getExpectedNumIphoneCssLinks());

        assertCssLinksFoundInLeafGroupUptoDefaultGroup();

        assertCssLinkFoundInLeafGroupOnly();

        assertCssLinkFoundInDefaultGroupOnly();

        assertCssLinkFoundInIntermediateGroupOnly();

        assertCssLinkFoundInMultipleIntermediateGroupsOnly();

        assertCssLinkFoundFromMapComponent();

        doAssertCssLinks();

    }

    protected void doAssertCssLinks() {
        // Do nothing.
    }

    protected int getExpectedNumIphoneCssLinks() {
        return super.getNumExpectedLinks() + 10;
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
        assertCssLink("mapComponent-advanced/selenium/component/map/map.css link not found",
                "mapComponent-advanced/selenium/component/map/map.css");
    }

    private void assertIphoneScripts() {

        final int expectedNumIphoneHeadScripts = 27;
        assertNumHeadScripts(expectedNumIphoneHeadScripts + super.getNumExpectedHeadScripts());

        final int expectedNumIphoneBodyScripts = 0;
        assertNumBodyScripts(expectedNumIphoneBodyScripts + super.getNumExpectedBodyScripts());

        assertIphoneScriptsByName();
        assertIphoneScriptsByPackageNoBundling();
    }

    private void assertIphoneScriptsByName() {
        assertLeafGroupScriptResolvedByNameWithNoInheritenceToOverride();

        assertLeafGroupScriptResolvedByNameOverridingInheritedGroups();

        assertDefaultGroupScriptResolvedByName();

        assertIntermediateGroupScriptResolvedByNameWithNoInheritenceToOverride();

        assertIntermediateGroupScriptResolvedByNameOverridingInheritedGroup();
    }

    private void assertLeafGroupScriptResolvedByNameWithNoInheritenceToOverride() {
        assertScript("iphone-ipod/selenium/common/columns.js script not found",
            "iphone-ipod/selenium/common/columns.js");
    }

    private void assertLeafGroupScriptResolvedByNameOverridingInheritedGroups() {
        assertScript("iphone-ipod/selenium/common/main.js script not found",
            "iphone-ipod/selenium/common/main.js");
    }

    private void assertDefaultGroupScriptResolvedByName() {
        assertScript("default/selenium/results/results.js script not found",
        "default/selenium/results/results.js");
    }

    private void assertIntermediateGroupScriptResolvedByNameWithNoInheritenceToOverride() {
        assertScript("webkit/selenium/common/jazz.js script not found",
        "webkit/selenium/common/jazz.js");
    }

    private void assertIntermediateGroupScriptResolvedByNameOverridingInheritedGroup() {
        assertScript("applewebkit/selenium/common/decorations.js script not found",
            "applewebkit/selenium/common/decorations.js");
    }

    private void assertIphoneScriptsByPackageNoBundling() {
        assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithPartiallyDefinedOrder();

        assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithPartiallyDefinedOrderPlusExplicitWildcard();

        assertResolvedScriptsInheritenceFromMultipleGroupsUptoDefaultGroupWithCompleteOrderDefinedForSome();

        assertOnlyIntermediateGroupScriptsResolvedByPackageNoBundling();

        assertMultipleIntermediateGroupScriptsResolvedByPackageNoBundling();

        assertMapComponentScriptsResolvedByPackageNoBundlingWithCompleteOrder();
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

    private void assertOnlyDefaultGroupScriptsResolvedByPackageNoBundlingWithPartiallyDefinedOrderPlusExplicitWildcard() {
        assertScript("iphone-ipod/selenium/grid/grid2.js script not found",
                "iphone-ipod/selenium/grid/grid2.js");
        assertScript("iphone-ipod/selenium/grid/grid1.js script not found",
                "iphone-ipod/selenium/grid/grid1.js");
        assertScript("iphone-ipod/selenium/grid/grid3.js script not found",
                "iphone-ipod/selenium/grid/grid3.js");
    }

    private void assertResolvedScriptsInheritenceFromMultipleGroupsUptoDefaultGroupWithCompleteOrderDefinedForSome() {
        assertScript("default/selenium/reporting/default-reporting1.js script not found",
                "default/selenium/reporting/default-reporting1.js");
        assertScript("default/selenium/reporting/default-reporting2.js script not found",
                "default/selenium/reporting/default-reporting2.js");

        assertScript("webkit/selenium/reporting/webkit-reporting1.js script not found",
                "webkit/selenium/reporting/webkit-reporting1.js");
        assertScript("webkit/selenium/reporting/webkit-reporting2.js script not found",
                "webkit/selenium/reporting/webkit-reporting2.js");

        assertScript("applewebkit/selenium/reporting/applewebkit-reporting2.js script not found",
                "applewebkit/selenium/reporting/applewebkit-reporting2.js");
        assertScript("applewebkit/selenium/reporting/applewebkit-reporting1.js script not found",
                "applewebkit/selenium/reporting/applewebkit-reporting1.js");

        assertScript("iphone-ipod/selenium/reporting/iphone-ipod-reporting1.js script not found",
                "iphone-ipod/selenium/reporting/iphone-ipod-reporting1.js");
        assertScript("iphone-ipod/selenium/reporting/iphone-ipod-reporting2.js script not found",
                "iphone-ipod/selenium/reporting/iphone-ipod-reporting2.js");
    }

    private void assertMapComponentScriptsResolvedByPackageNoBundlingWithCompleteOrder() {
        assertScript("mapComponent-advanced/selenium/component/map/map2.js script not found",
                "mapComponent-advanced/selenium/component/map/map2.js");
        assertScript("mapComponent-advanced/selenium/component/map/map1.js script not found",
                "mapComponent-advanced/selenium/component/map/map1.js");
    }

    private void assertOnlyIntermediateGroupScriptsResolvedByPackageNoBundling() {
        assertScript("webkit/selenium/animation/animation1.js script not found",
                "webkit/selenium/animation/animation1.js");
        assertScript("webkit/selenium/animation/animation2.js script not found",
                "webkit/selenium/animation/animation2.js");
    }

    private void assertMultipleIntermediateGroupScriptsResolvedByPackageNoBundling() {
        assertScript("webkit/selenium/layers/webkit-layers1.js script not found",
                "webkit/selenium/layers/webkit-layers1.js");
        assertScript("webkit/selenium/layers/webkit-layers2.js script not found",
                "webkit/selenium/layers/webkit-layers2.js");

        assertScript("applewebkit/selenium/layers/layers1.js script not found",
                "applewebkit/selenium/layers/applewebkit-layers1.js");
        assertScript("applewebkit/selenium/layers/layers2.js script not found",
                "applewebkit/selenium/layers/applewebkit-layers2.js");
    }

    private void assertLeafGroupJsp() {
        assertTrue(getBrowser().isTextPresent("[iphone-ipod] bdp.jsp"));
    }

    private void assertDeviceProperties() {
        assertTrue(getBrowser().isTextPresent("'brwsrname': Safari"));
        assertImageCategoryText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void assertMapAddons() {
        assertTrue(getBrowser().isTextPresent("[advancedMapAddons] mapAddons.jsp"));
    }

    protected void assertImageCategoryText() {
        assertTrue("imageCategory is wrong",
                getBrowser().isTextPresent("'custom.imageCategory': L"));
    }

    /**
     * Assert expected out of the bundleScriptsTag.
     */
    @Override
    protected void assertBundleScriptsTagOutputPresent() {
        assertBundleScriptsTagJavaScriptVariable("iphoneIpodShowcaseAppBundlePackage1File1",
                "true");
        assertBundleScriptsTagJavaScriptVariable("iphoneIpodShowcaseAppBundlePackage1File2",
                "true");

        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage1File1", "null");
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage1File2", "null");

        // inherited from default group.
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage2File1", "true");
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage2File2", "true");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getScaledImageFormat() {
        return "png";
    }
}
